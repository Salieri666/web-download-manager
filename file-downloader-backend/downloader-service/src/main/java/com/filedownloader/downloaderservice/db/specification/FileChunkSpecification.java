package com.filedownloader.downloaderservice.db.specification;

import com.filedownloader.corelib.utils.SpecificationBuilder;
import com.filedownloader.corelib.utils.SpecificationUtils;
import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.filter.FileChunkFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class FileChunkSpecification {

    public Specification<FileChunkEntity> byFilter(FileChunkFilter filter) {
        if (filter == null) {
            filter = FileChunkFilter.builder().build();
        }

        return new SpecificationBuilder<FileChunkEntity>()
                .and(byIds(filter.getIds()))
                .and(byFileIds(filter.getFileIds()))
                .and(byChunkIndex(filter.getChunkIndex()))
                .and(byStatus(filter.getStatus()))
                .and(byWorkerId(filter.getWorkerId()))
                .build();
    }

    private Specification<FileChunkEntity> byIds(Collection<UUID> ids) {
        return SpecificationUtils.searchIn(FileChunkEntity.Fields.id, ids);
    }

    private Specification<FileChunkEntity> byFileIds(Collection<UUID> fileIds) {
        return SpecificationUtils.searchIn(List.of(FileChunkEntity.Fields.fileDescription, "id"), fileIds);
    }

    private Specification<FileChunkEntity> byChunkIndex(Integer chunkIndex) {
        return SpecificationUtils.byFieldEqual(FileChunkEntity.Fields.chunkIndex, chunkIndex);
    }

    private Specification<FileChunkEntity> byStatus(com.filedownloader.downloaderservice.model.enums.FileChunkStatus status) {
        return SpecificationUtils.byFieldEqual(FileChunkEntity.Fields.status, status);
    }

    private Specification<FileChunkEntity> byWorkerId(String workerId) {
        return SpecificationUtils.searchLike(FileChunkEntity.Fields.workerId, workerId);
    }
}
