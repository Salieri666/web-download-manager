package com.filedownloader.downloaderservice.db.repository;

import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.exceptionlib.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunkEntity, UUID>, JpaSpecificationExecutor<FileChunkEntity> {

    List<FileChunkEntity> findAllByFileDescription_Id(UUID fileId);

    List<FileChunkEntity> findAllByFileDescription(FileDescriptionEntity fileDescription);

    default FileChunkEntity getEntityById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FileChunkEntity.class, String.valueOf(id)));
    }
}
