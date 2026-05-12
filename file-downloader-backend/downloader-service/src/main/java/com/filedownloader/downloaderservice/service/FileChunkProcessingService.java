package com.filedownloader.downloaderservice.service;

import java.util.UUID;

public interface FileChunkProcessingService {

    void process(UUID fileChunkId);

}
