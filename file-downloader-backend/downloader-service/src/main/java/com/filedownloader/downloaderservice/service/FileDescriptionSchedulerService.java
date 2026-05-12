package com.filedownloader.downloaderservice.service;

import com.filedownloader.downloaderservice.db.repository.FileDescriptionRepository;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
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
public class FileDescriptionSchedulerService {

    private static final int HEADER_PROCESSING_BATCH_SIZE = 50;

    private final FileDescriptionRepository fileDescriptionRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final FileHeaderProcessingService fileHeaderProcessingService;

    public FileDescriptionSchedulerService(
            FileDescriptionRepository fileDescriptionRepository,
            @Qualifier("fileDescriptionTaskExecutor") TaskExecutor taskExecutor,
            FileHeaderProcessingService fileHeaderProcessingService
    ) {
        this.fileDescriptionRepository = fileDescriptionRepository;
        this.taskExecutor = (ThreadPoolTaskExecutor) taskExecutor;
        this.fileHeaderProcessingService = fileHeaderProcessingService;
    }


    @Scheduled(fixedDelayString = "${downloader.scheduler.file-processing-delay:5000}")
    @Transactional
    public void processFiles() {
        List<FileDescriptionEntity> fileDescriptions = lockFileDescriptionsForHeaderProcessing();
        int freeQueueSlots = getFreeQueueSlots();
        int filesToProcessCount = Math.min(fileDescriptions.size(), freeQueueSlots);

        if (filesToProcessCount <= 0) {
            log.debug("No free queue slots for header processing");
            return;
        }

        List<FileDescriptionEntity> filesToProcess = new ArrayList<>(fileDescriptions.subList(0, filesToProcessCount));
        filesToProcess.forEach(fileDescription -> fileDescription.setStatus(FileDescriptionStatus.HEADER_PROCESSING_PENDING));
        registerAfterCommitProcessing(filesToProcess);
        log.info(
                "Marked {} file descriptions as HEADER_PROCESSING_PENDING and scheduled after commit",
                filesToProcess.size()
        );

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
                        taskExecutor.execute(() -> fileHeaderProcessingService.process(fileDescription.getId()))
                );
            }
        });
    }

    private List<FileDescriptionEntity> lockFileDescriptionsForHeaderProcessing() {
        return fileDescriptionRepository.findAllForHeaderProcessing(
                List.of(
                        FileDescriptionStatus.PENDING
                ),
                PageRequest.of(0, HEADER_PROCESSING_BATCH_SIZE)
        );
    }
}
