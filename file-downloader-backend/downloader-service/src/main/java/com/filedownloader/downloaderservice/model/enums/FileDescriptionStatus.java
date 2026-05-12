package com.filedownloader.downloaderservice.model.enums;

public enum FileDescriptionStatus {
    PENDING,
    HEADER_PROCESSING_PENDING,
    HEADER_PROCESSING,
    DOWNLOADING_CHUNKS,
    ASSEMBLE_CHUNKS,
    DOWNLOAD_COMPLETED,
    FAILED
}
