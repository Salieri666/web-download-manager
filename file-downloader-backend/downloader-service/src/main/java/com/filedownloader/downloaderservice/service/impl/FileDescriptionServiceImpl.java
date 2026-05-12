package com.filedownloader.downloaderservice.service.impl;

import com.filedownloader.corelib.model.dto.FileDTO;
import com.filedownloader.downloaderservice.db.repository.FileDescriptionRepository;
import com.filedownloader.downloaderservice.db.specification.FileDescriptionSpecification;
import com.filedownloader.downloaderservice.model.dto.CreateFileDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionWithChunksDto;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import com.filedownloader.downloaderservice.model.filter.FileDescriptionFilter;
import com.filedownloader.downloaderservice.model.mapper.FileDescriptionMapper;
import com.filedownloader.downloaderservice.service.FileDescriptionService;
import com.filedownloader.downloaderservice.validator.FileDescriptionReadyValidator;
import com.filedownloader.exceptionlib.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileDescriptionServiceImpl implements FileDescriptionService {

    private final FileDescriptionRepository repository;
    private final FileDescriptionSpecification specification;
    private final FileDescriptionMapper fileDescriptionMapper;
    private final FileDescriptionReadyValidator readyValidator;

    @Override
    @Transactional(readOnly = true)
    public Page<FileDescriptionDto> getAll(FileDescriptionFilter filter, Pageable pageable) {
        Specification<FileDescriptionEntity> spec = specification.byFilter(filter);
        return repository.findAll(spec, pageable).map(fileDescriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public FileDescriptionWithChunksDto getById(UUID id) {
        return fileDescriptionMapper.toDetailsDto(repository.getEntityById(id));
    }

    @Override
    @Transactional
    public FileDescriptionDto create(CreateFileDto dto) {
        FileDescriptionEntity entity = fileDescriptionMapper.toEntity(dto);
        entity.setStatus(FileDescriptionStatus.PENDING);
        FileDescriptionEntity savedEntity = repository.save(entity);

        return fileDescriptionMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public FileDTO download(UUID id) {
        FileDescriptionEntity fileDescription = repository.getEntityById(id);
        readyValidator.validate(fileDescription);

        Path filePath = Paths.get(fileDescription.getStoragePath()).toAbsolutePath().normalize();

        try {
            byte[] body = Files.readAllBytes(filePath);
            long contentLength = Files.size(filePath);

            return FileDTO.builder()
                    .fileName(filePath.getFileName().toString())
                    .contentType(org.springframework.http.MediaType.parseMediaType(fileDescription.getMimeType()))
                    .contentLength(contentLength)
                    .body(body)
                    .lastModified(fileDescription.getModifiedDate())
                    .build();
        } catch (IOException e) {
            throw new BusinessException(
                    "Failed to read file for download: fileDescriptionId=" + id + ", path=" + filePath,
                    e
            );
        }
    }

    //TODO change logic
    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(repository.getEntityById(id));
    }

}
