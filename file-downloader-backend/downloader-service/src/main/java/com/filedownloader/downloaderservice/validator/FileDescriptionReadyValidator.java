package com.filedownloader.downloaderservice.validator;

import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import com.filedownloader.exceptionlib.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class FileDescriptionReadyValidator {

    public void validate(FileDescriptionEntity fileDescription) {
        if (fileDescription.getStatus() != FileDescriptionStatus.DOWNLOAD_COMPLETED) {
            throw new BusinessException(
                    "File description is not ready for download: id="
                            + fileDescription.getId()
                            + ", status="
                            + fileDescription.getStatus()
            );
        }
    }
}
