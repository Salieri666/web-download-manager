package com.filedownloader.downloaderservice.model.mapper;

import com.filedownloader.downloaderservice.model.dto.FileChunkDto;
import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface FileChunkMapper {

    @Mapping(target = "fileId", source = "fileDescription.id")
    FileChunkDto toDto(FileChunkEntity entity);

    @Mapping(target = "fileDescription", source = "fileId", qualifiedByName = "uuidToFileDescription")
    FileChunkEntity toEntity(FileChunkDto dto);

    @Named("uuidToFileDescription")
    default FileDescriptionEntity mapFileDescription(UUID fileId) {
        if (fileId == null) {
            return null;
        }

        return FileDescriptionEntity.builder()
                .id(fileId)
                .build();
    }
}
