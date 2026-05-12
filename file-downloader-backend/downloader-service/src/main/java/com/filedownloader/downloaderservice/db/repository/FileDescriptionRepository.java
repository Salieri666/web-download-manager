package com.filedownloader.downloaderservice.db.repository;

import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileDescriptionRepository extends JpaRepository<FileDescriptionEntity, UUID>, JpaSpecificationExecutor<FileDescriptionEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    @Query("""
            select fileDescription
            from FileDescriptionEntity fileDescription
            where fileDescription.id = :id
            """)
    Optional<FileDescriptionEntity> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))//skip locked
    @Query("""
            select fileDescription
            from FileDescriptionEntity fileDescription
            where fileDescription.status in :statuses
            order by fileDescription.createdDate asc
            """)
    List<FileDescriptionEntity> findAllForHeaderProcessing(
            @Param("statuses") Collection<FileDescriptionStatus> statuses,
            Pageable pageable
    );

    default FileDescriptionEntity getEntityById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FileDescriptionEntity.class, String.valueOf(id)));
    }
}
