package com.filedownloader.downloaderservice.db.repository;

import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import com.filedownloader.downloaderservice.model.projection.FileChunkDownloadProgressProjection;
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
              and (fileChunk.status <> 'FAILED' or fileChunk.retryCount < :maxRetryCount)
              and fileDescription.status in :fileDescriptionStatuses
            order by fileDescription.createdDate asc, fileChunk.chunkIndex asc
            """)
    List<FileChunkEntity> findAllForChunkProcessing(
            @Param("chunkStatuses") Collection<FileChunkStatus> chunkStatuses,
            @Param("fileDescriptionStatuses") Collection<FileDescriptionStatus> fileDescriptionStatuses,
            @Param("maxRetryCount") Integer maxRetryCount,
            Pageable pageable
    );

    @Query("""
            select fileChunk.fileDescription.id as fileDescriptionId,
                   coalesce(sum(fileChunk.currentSize), 0) as downloadedSize
            from FileChunkEntity fileChunk
            where fileChunk.fileDescription.id in :fileDescriptionIds
            group by fileChunk.fileDescription.id
            """)
    List<FileChunkDownloadProgressProjection> findDownloadedSizeByFileDescriptionIds(
            @Param("fileDescriptionIds") Collection<UUID> fileDescriptionIds
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    @Query("""
            select fileChunk
            from FileChunkEntity fileChunk
            join fetch fileChunk.fileDescription fileDescription
            where fileChunk.id = :id
            """)
    java.util.Optional<FileChunkEntity> findByIdForUpdate(@Param("id") UUID id);

    default FileChunkEntity getEntityById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FileChunkEntity.class, String.valueOf(id)));
    }
}
