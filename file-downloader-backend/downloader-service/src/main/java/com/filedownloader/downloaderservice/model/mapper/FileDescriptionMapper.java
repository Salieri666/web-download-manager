package com.filedownloader.downloaderservice.model.mapper;

import com.filedownloader.downloaderservice.model.dto.CreateFileDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionWithChunksDto;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {FileChunkMapper.class})
public interface FileDescriptionMapper {

    FileDescriptionDto toDto(FileDescriptionEntity entity);

    FileDescriptionWithChunksDto toDetailsDto(FileDescriptionEntity entity);

    FileDescriptionEntity toEntity(CreateFileDto dto);

}
