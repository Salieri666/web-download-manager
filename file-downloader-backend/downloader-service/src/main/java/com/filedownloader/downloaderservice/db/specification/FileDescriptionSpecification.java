package com.filedownloader.downloaderservice.db.specification;

import com.filedownloader.corelib.utils.SpecificationBuilder;
import com.filedownloader.corelib.utils.SpecificationUtils;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import com.filedownloader.downloaderservice.model.filter.FileDescriptionFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class FileDescriptionSpecification {

    public Specification<FileDescriptionEntity> byFilter(FileDescriptionFilter filter) {
        if (filter == null) {
            filter = FileDescriptionFilter.builder().build();
        }

        return new SpecificationBuilder<FileDescriptionEntity>()
                .and(byIds(filter.getIds()))
                .and(byFilename(filter.getFilename()))
                .and(bySourceUrl(filter.getSourceUrl()))
                .and(byStatus(filter.getStatus()))
                .and(byMimeType(filter.getMimeType()))
                .build();
    }

    private Specification<FileDescriptionEntity> byIds(Collection<UUID> ids) {
        return SpecificationUtils.searchIn(FileDescriptionEntity.Fields.id, ids);
    }

    private Specification<FileDescriptionEntity> byFilename(String filename) {
        return SpecificationUtils.searchLike(FileDescriptionEntity.Fields.filename, filename);
    }

    private Specification<FileDescriptionEntity> bySourceUrl(String sourceUrl) {
        return SpecificationUtils.searchLike(FileDescriptionEntity.Fields.sourceUrl, sourceUrl);
    }

    private Specification<FileDescriptionEntity> byStatus(FileDescriptionStatus status) {
        return SpecificationUtils.byFieldEqual(FileDescriptionEntity.Fields.status, status);
    }

    private Specification<FileDescriptionEntity> byMimeType(String mimeType) {
        return SpecificationUtils.searchLike(FileDescriptionEntity.Fields.mimeType, mimeType);
    }
}
