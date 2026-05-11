package com.filedownloader.downloaderservice.model.mapper;

import com.filedownloader.downloaderservice.model.dto.FileDescriptionDto;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileDescriptionMapper {

    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "updatedAt", source = "modifiedDate")
    FileDescriptionDto toDto(FileDescriptionEntity entity);

    @Mapping(target = "createdDate", source = "createdAt")
    @Mapping(target = "modifiedDate", source = "updatedAt")
    FileDescriptionEntity toEntity(FileDescriptionDto dto);
}
