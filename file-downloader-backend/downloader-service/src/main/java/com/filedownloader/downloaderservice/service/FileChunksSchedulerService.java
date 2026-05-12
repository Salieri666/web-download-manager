package com.filedownloader.downloaderservice.service;

import com.filedownloader.downloaderservice.db.repository.FileChunkRepository;
import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileChunksSchedulerService {

    private static final int BATCH_SIZE = 50;

    private final FileChunkRepository fileChunkRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final FileChunkProcessingService fileChunkProcessingService;

    public FileChunksSchedulerService(
            FileChunkRepository fileChunkRepository,
            @Qualifier("chunkDownloadingTaskExecutor") TaskExecutor taskExecutor,
            FileChunkProcessingService fileChunkProcessingService
    ) {
        this.fileChunkRepository = fileChunkRepository;
        this.taskExecutor = (ThreadPoolTaskExecutor) taskExecutor;
        this.fileChunkProcessingService = fileChunkProcessingService;
    }


    @Scheduled(fixedDelayString = "${downloader.scheduler.file-chunks-delay:2000}")
    @Transactional
    public void processChunks() {
        List<FileChunkEntity> fileChunks = lockFileChunksForProcessing();
        int freeQueueSlots = getFreeQueueSlots();
        int chunksToProcessCount = Math.min(fileChunks.size(), freeQueueSlots);

        if (chunksToProcessCount <= 0) {
            log.debug("No free queue slots for chunk processing");
            return;
        }

        List<FileChunkEntity> chunksToProcess = new ArrayList<>(fileChunks.subList(0, chunksToProcessCount));
        chunksToProcess.forEach(fileChunk -> fileChunk.setStatus(FileChunkStatus.UPLOADING));
        registerAfterCommitProcessing(chunksToProcess);
        log.info("Marked {} file chunks as UPLOADING and scheduled after commit", chunksToProcess.size());
    }

    private int getFreeQueueSlots() {
        var queue = taskExecutor.getThreadPoolExecutor().getQueue();
        return queue.remainingCapacity();
    }

    private void registerAfterCommitProcessing(List<FileChunkEntity> fileChunks) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                fileChunks.forEach(fileChunk ->
                        taskExecutor.execute(() -> fileChunkProcessingService.process(fileChunk.getId()))
                );
            }
        });
    }

    private List<FileChunkEntity> lockFileChunksForProcessing() {
        return fileChunkRepository.findAllForChunkProcessing(
                List.of(
                        FileChunkStatus.PENDING
                ),
                List.of(
                        FileDescriptionStatus.DOWNLOADING_CHUNKS
                ),
                PageRequest.of(0, BATCH_SIZE)
        );
    }
}
