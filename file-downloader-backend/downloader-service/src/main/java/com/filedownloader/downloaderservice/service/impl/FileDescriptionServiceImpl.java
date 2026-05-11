package com.filedownloader.downloaderservice.service.impl;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileDescriptionServiceImpl implements FileDescriptionService {

    private final FileDescriptionRepository repository;
    private final FileDescriptionSpecification specification;
    private final FileDescriptionMapper fileDescriptionMapper;

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

    //TODO change logic
    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(repository.getEntityById(id));
    }

}
