package com.filedownloader.downloaderservice.service;

import com.filedownloader.corelib.model.dto.FileDTO;
import com.filedownloader.downloaderservice.model.dto.CreateFileDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionWithChunksDto;
import com.filedownloader.downloaderservice.model.filter.FileDescriptionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FileDescriptionService {

    FileDescriptionWithChunksDto getById(UUID id);

    Page<FileDescriptionDto> getAll(FileDescriptionFilter filter, Pageable pageable);

    FileDescriptionDto create(CreateFileDto dto);

    FileDTO download(UUID id);

    void delete(UUID id);
}
