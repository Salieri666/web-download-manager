package com.filedownloader.downloaderservice.service;

import com.filedownloader.downloaderservice.db.repository.FileDescriptionRepository;
import com.filedownloader.downloaderservice.db.specification.FileDescriptionSpecification;
import com.filedownloader.downloaderservice.model.mapper.FileDescriptionMapper;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionDto;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.downloaderservice.model.filter.FileDescriptionFilter;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileDescriptionService {

    private final FileDescriptionRepository repository;
    private final FileDescriptionSpecification specification;
    private final FileDescriptionMapper mapper;

    @Transactional(readOnly = true)
    public List<FileDescriptionDto> findAll(FileDescriptionFilter filter) {
        Specification<FileDescriptionEntity> spec = specification.byFilter(filter);
        return repository.findAll(spec).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public FileDescriptionDto getById(UUID id) {
        return mapper.toDto(repository.getEntityById(id));
    }

    @Transactional
    public FileDescriptionDto create(FileDescriptionDto dto) {
        FileDescriptionEntity entity = mapper.toEntity(dto);
        entity.setId(null);
        if (entity.getStatus() == null) {
            entity.setStatus(FileDescriptionStatus.PENDING);
        }
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public FileDescriptionDto update(UUID id, FileDescriptionDto dto) {
        FileDescriptionEntity entity = repository.getEntityById(id);
        apply(entity, dto);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(repository.getEntityById(id));
    }

    private void apply(FileDescriptionEntity entity, FileDescriptionDto dto) {
        entity.setFilename(dto.filename());
        entity.setStoragePath(dto.storagePath());
        entity.setSourceUrl(dto.sourceUrl());
        if (dto.status() != null) {
            entity.setStatus(dto.status());
        }
        entity.setTotalSize(dto.totalSize());
        entity.setMimeType(dto.mimeType());
        entity.setChecksum(dto.checksum());
        entity.setMetadata(dto.metadata());
    }
}
