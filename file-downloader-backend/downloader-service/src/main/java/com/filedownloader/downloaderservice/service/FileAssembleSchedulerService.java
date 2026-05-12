package com.filedownloader.downloaderservice.service;

import com.filedownloader.downloaderservice.db.repository.FileDescriptionRepository;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
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
public class FileAssembleSchedulerService {

    private static final int BATCH_SIZE = 50;

    private final FileDescriptionRepository fileDescriptionRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final FileAssembleProcessingService fileAssembleProcessingService;

    public FileAssembleSchedulerService(
            FileDescriptionRepository fileDescriptionRepository,
            @Qualifier("assembleTaskExecutor") TaskExecutor taskExecutor,
            FileAssembleProcessingService fileAssembleProcessingService
    ) {
        this.fileDescriptionRepository = fileDescriptionRepository;
        this.taskExecutor = (ThreadPoolTaskExecutor) taskExecutor;
        this.fileAssembleProcessingService = fileAssembleProcessingService;
    }

    @Scheduled(fixedDelayString = "${downloader.scheduler.file-assemble-delay:3000}")
    @Transactional
    public void processFiles() {
        List<FileDescriptionEntity> fileDescriptions = lockFileDescriptionsForAssembleProcessing();
        int freeQueueSlots = getFreeQueueSlots();
        int filesToProcessCount = Math.min(fileDescriptions.size(), freeQueueSlots);

        if (filesToProcessCount <= 0) {
            log.debug("No free queue slots for assemble processing");
            return;
        }

        List<FileDescriptionEntity> filesToProcess = new ArrayList<>(fileDescriptions.subList(0, filesToProcessCount));
        filesToProcess.forEach(fileDescription -> fileDescription.setStatus(FileDescriptionStatus.ASSEMBLE_CHUNKS));
        registerAfterCommitProcessing(filesToProcess);
        log.info("Marked {} file descriptions as ASSEMBLE_CHUNKS and scheduled after commit", filesToProcess.size());
    }

    private int getFreeQueueSlots() {
        var queue = taskExecutor.getThreadPoolExecutor().getQueue();
        return queue.remainingCapacity();
    }

    private void registerAfterCommitProcessing(List<FileDescriptionEntity> fileDescriptions) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                fileDescriptions.forEach(fileDescription ->
                        taskExecutor.execute(() -> fileAssembleProcessingService.process(fileDescription.getId()))
                );
            }
        });
    }

    private List<FileDescriptionEntity> lockFileDescriptionsForAssembleProcessing() {
        return fileDescriptionRepository.findAllForAssembleProcessing(
                List.of(
                        FileDescriptionStatus.DOWNLOADING_CHUNKS
                ),
                FileChunkStatus.COMPLETED,
                PageRequest.of(0, BATCH_SIZE)
        );
    }
}
