package com.filedownloader.downloaderservice.db.repository;

import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import com.filedownloader.exceptionlib.exception.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunkEntity, UUID>, JpaSpecificationExecutor<FileChunkEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    @Query("""
            select fileChunk
            from FileChunkEntity fileChunk
            join fileChunk.fileDescription fileDescription
            where fileChunk.status in :chunkStatuses
              and fileDescription.status in :fileDescriptionStatuses
            order by fileDescription.createdDate asc, fileChunk.chunkIndex asc
            """)
    List<FileChunkEntity> findAllForChunkProcessing(
            @Param("chunkStatuses") Collection<FileChunkStatus> chunkStatuses,
            @Param("fileDescriptionStatuses") Collection<FileDescriptionStatus> fileDescriptionStatuses,
            Pageable pageable
    );

    default FileChunkEntity getEntityById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FileChunkEntity.class, String.valueOf(id)));
    }
}
