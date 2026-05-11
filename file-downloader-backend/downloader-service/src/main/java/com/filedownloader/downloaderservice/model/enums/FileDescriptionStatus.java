package com.filedownloader.downloaderservice.model.enums;

public enum FileDescriptionStatus {
    PENDING,
    HEADER_PROCESSING_PENDING,
    HEADER_PROCESSING,
    HEADER_PROCESSED,
    DOWNLOAD_PROCESSING,
    DOWNLOAD_COMPLETED,
    FAILED
}
