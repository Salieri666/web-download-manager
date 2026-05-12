package com.filedownloader.downloaderservice.service.impl;

import com.filedownloader.downloaderservice.service.FileChunkProcessingService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileChunkProcessingServiceImpl implements FileChunkProcessingService {

    @Override
    public void process(UUID fileChunkId) {
        // Implement chunk processing later.
    }
}
