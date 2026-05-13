package com.filedownloader.downloaderservice.model.projection;

import java.util.UUID;

public interface FileChunkDownloadProgressProjection {

    UUID getFileDescriptionId();

    Long getDownloadedSize();

}
