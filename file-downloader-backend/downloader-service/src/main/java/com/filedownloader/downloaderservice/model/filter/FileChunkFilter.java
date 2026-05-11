package com.filedownloader.downloaderservice.model.filter;

import com.filedownloader.corelib.model.filter.BaseFilter;
import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "File chunk filter")
public class FileChunkFilter extends BaseFilter<UUID> {

    @Schema(description = "File identifiers")
    private Set<UUID> fileIds;

    @Schema(description = "Chunk index")
    private Integer chunkIndex;

    @Schema(description = "Chunk status")
    private FileChunkStatus status;

    @Schema(description = "Worker identifier")
    private String workerId;

    @Override
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(getIds())
                && CollectionUtils.isEmpty(fileIds)
                && chunkIndex == null
                && status == null
                && StringUtils.isBlank(workerId);
    }
}
