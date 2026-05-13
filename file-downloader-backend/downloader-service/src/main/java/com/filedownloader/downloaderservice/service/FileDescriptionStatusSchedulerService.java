package com.filedownloader.downloaderservice.service;

import com.filedownloader.downloaderservice.db.repository.FileDescriptionRepository;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class FileDescriptionStatusSchedulerService {

    private static final int BATCH_SIZE = 50;

    private final FileDescriptionRepository fileDescriptionRepository;

    public FileDescriptionStatusSchedulerService(FileDescriptionRepository fileDescriptionRepository) {
        this.fileDescriptionRepository = fileDescriptionRepository;
    }

    @Scheduled(fixedDelayString = "${downloader.scheduler.file-description-status-delay:5000}")
    @Transactional
    public void processFailedFiles() {
        List<FileDescriptionEntity> fileDescriptions = lockFileDescriptionsWithFailedChunks();

        if (fileDescriptions.isEmpty()) {
            return;
        }

        fileDescriptions.forEach(fileDescription -> {
            fileDescription.setStatus(FileDescriptionStatus.FAILED);
            log.info("Marked file description {} as FAILED due to chunks with exhausted retries", fileDescription.getId());
        });
    }

    private List<FileDescriptionEntity> lockFileDescriptionsWithFailedChunks() {
        return fileDescriptionRepository.findAllWithFailedChunks(
                List.of(
                        FileDescriptionStatus.DOWNLOADING_CHUNKS
                ),
                5,
                PageRequest.of(0, BATCH_SIZE)
        );
    }
}
