package com.filedownloader.downloaderservice.db.repository;

import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.exceptionlib.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileDescriptionRepository extends JpaRepository<FileDescriptionEntity, UUID>, JpaSpecificationExecutor<FileDescriptionEntity> {

    Optional<FileDescriptionEntity> findByFilename(String filename);

    default FileDescriptionEntity getEntityById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FileDescriptionEntity.class, String.valueOf(id)));
    }
}
