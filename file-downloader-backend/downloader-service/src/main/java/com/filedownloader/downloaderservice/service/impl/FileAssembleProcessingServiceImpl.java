package com.filedownloader.downloaderservice.service.impl;

import com.filedownloader.corelib.utils.TransactionUtils;
import com.filedownloader.downloaderservice.db.repository.FileDescriptionRepository;
import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import com.filedownloader.downloaderservice.service.FileAssembleProcessingService;
import com.filedownloader.exceptionlib.exception.BusinessException;
import com.filedownloader.exceptionlib.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileAssembleProcessingServiceImpl implements FileAssembleProcessingService {

    private static final Path READY_DOWNLOADS_DIR = Paths.get("ready-downloads");

    private final FileDescriptionRepository fileDescriptionRepository;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void process(UUID fileDescriptionId) {
        AssembleProcessingContext context = TransactionUtils.execute(transactionManager, () -> {
            FileDescriptionEntity fileDescription = fileDescriptionRepository.getEntityById(fileDescriptionId);
            List<ChunkFileContext> chunkFiles = fileDescription.getChunks().stream()
                    .sorted(Comparator.comparing(FileChunkEntity::getChunkIndex))
                    .map(chunk -> new ChunkFileContext(chunk.getId(), chunk.getStoragePath()))
                    .toList();

            if (chunkFiles.isEmpty()) {
                throw new BusinessException("File description has no chunks to assemble: " + fileDescriptionId);
            }

            return new AssembleProcessingContext(
                    fileDescription.getId(),
                    fileDescription.getFilename(),
                    chunkFiles
            );
        });

        Path assembledFilePath = buildAssembledFilePath(context);
        boolean assembledSuccessfully = false;

        try {
            assembleChunks(context, assembledFilePath);
            completeFileDescription(context.fileDescriptionId(), assembledFilePath);
            assembledSuccessfully = true;
            log.info(
                    "File assembly completed for fileDescriptionId={}, storagePath={}",
                    fileDescriptionId,
                    assembledFilePath
            );
        } catch (RuntimeException ex) {
            markFailed(context.fileDescriptionId(), ex);
            cleanupAssembledFile(assembledFilePath);
            throw ex;
        } finally {
            if (assembledSuccessfully) {
                cleanupTempFiles(context);
            }
        }
    }

    private void assembleChunks(AssembleProcessingContext context, Path assembledFilePath) {
        try {
            Files.createDirectories(assembledFilePath.getParent());

            try (OutputStream outputStream = Files.newOutputStream(
                    assembledFilePath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            )) {
                for (ChunkFileContext chunkFile : context.chunkFiles()) {
                    Path tempFilePath = Paths.get(chunkFile.storagePath());
                    if (!Files.exists(tempFilePath)) {
                        throw new BusinessException(
                                "Temporary chunk file does not exist for fileDescriptionId="
                                        + context.fileDescriptionId() + ", path=" + tempFilePath
                        );
                    }

                    try (InputStream inputStream = Files.newInputStream(tempFilePath)) {
                        long bytesCopied = inputStream.transferTo(outputStream);
                        if (bytesCopied <= 0 && Files.size(tempFilePath) > 0) {
                            throw new BusinessException(
                                    "Failed to append chunk file for fileDescriptionId="
                                            + context.fileDescriptionId() + ", path=" + tempFilePath
                            );
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new BusinessException(
                    "Failed to assemble file for fileDescriptionId=" + context.fileDescriptionId(),
                    e
            );
        }
    }

    private void completeFileDescription(UUID fileDescriptionId, Path assembledFilePath) {
        TransactionUtils.executeWithoutResult(transactionManager, () -> {
            FileDescriptionEntity fileDescription = fileDescriptionRepository.getEntityById(fileDescriptionId);
            fileDescription.setStoragePath(assembledFilePath.toString());
            fileDescription.setStatus(FileDescriptionStatus.DOWNLOAD_COMPLETED);
            fileDescription.setErrorMessage(null);
        });
    }

    private void markFailed(UUID fileDescriptionId, RuntimeException cause) {
        TransactionUtils.executeWithoutResult(transactionManager, () -> {
            FileDescriptionEntity fileDescription = fileDescriptionRepository.getEntityById(fileDescriptionId);
            fileDescription.setStatus(FileDescriptionStatus.FAILED);
            fileDescription.setErrorMessage(ExceptionUtils.getStackTraceAsString(cause));
        });
    }

    private Path buildAssembledFilePath(AssembleProcessingContext context) {
        String fileName = sanitizeFileName(context.fileName());
        String baseName = extractBaseName(fileName);
        String extension = extractExtension(fileName);
        String assembledFileName = extension.isBlank()
                ? context.fileDescriptionId() + "-" + baseName
                : context.fileDescriptionId() + "-" + baseName + "." + extension;
        return READY_DOWNLOADS_DIR.resolve(assembledFileName);
    }

    private void cleanupTempFiles(AssembleProcessingContext context) {
        for (ChunkFileContext chunkFile : context.chunkFiles()) {
            Path tempFilePath = Paths.get(chunkFile.storagePath());
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                log.warn("Failed to cleanup temp file {}", tempFilePath, e);
            }
        }
    }

    private void cleanupAssembledFile(Path assembledFilePath) {
        try {
            Files.deleteIfExists(assembledFilePath);
        } catch (IOException e) {
            log.warn("Failed to cleanup assembled file {}", assembledFilePath, e);
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName
                .replace('\\', '_')
                .replace('/', '_')
                .replace(':', '_')
                .replace('*', '_')
                .replace('?', '_')
                .replace('"', '_')
                .replace('<', '_')
                .replace('>', '_')
                .replace('|', '_');
    }

    private String extractBaseName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex <= 0) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }

    private String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex <= 0 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    private record AssembleProcessingContext(
            UUID fileDescriptionId,
            String fileName,
            List<ChunkFileContext> chunkFiles
    ) {
    }

    private record ChunkFileContext(
            UUID chunkId,
            String storagePath
    ) {
    }
}
