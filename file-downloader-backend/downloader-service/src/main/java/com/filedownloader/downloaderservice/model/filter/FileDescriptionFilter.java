package com.filedownloader.downloaderservice.model.filter;

import com.filedownloader.corelib.model.filter.BaseFilter;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "File description filter")
public class FileDescriptionFilter extends BaseFilter<UUID> {

    @Schema(description = "File name")
    private String filename;

    @Schema(description = "Source URL")
    private String sourceUrl;

    @Schema(description = "Description status")
    private FileDescriptionStatus status;

    @Schema(description = "MIME type")
    private String mimeType;

    @Override
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(getIds())
                && StringUtils.isBlank(filename)
                && StringUtils.isBlank(sourceUrl)
                && status == null
                && StringUtils.isBlank(mimeType);
    }
}
