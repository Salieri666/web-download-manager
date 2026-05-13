package com.filedownloader.downloaderservice.model.mapper;

import com.filedownloader.downloaderservice.model.dto.CreateFileDto;
import com.filedownloader.downloaderservice.model.dto.FileChunkDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionWithChunksDto;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FileChunkMapper.class})
public interface FileDescriptionMapper {

    FileDescriptionDto toDto(FileDescriptionEntity entity);

    FileDescriptionWithChunksDto toDetailsDto(FileDescriptionEntity entity);

    FileDescriptionEntity toEntity(CreateFileDto dto);

    @AfterMapping
    default void populatePercentage(FileDescriptionEntity entity, @MappingTarget FileDescriptionWithChunksDto dto) {
        if (dto == null) {
            return;
        }

        dto.setPercentage(calculatePercentage(dto.getTotalSize(), dto.getChunks()));
    }

    default Integer calculatePercentage(Long totalSize, List<FileChunkDto> chunks) {
        if (totalSize == null || totalSize <= 0 || chunks == null || chunks.isEmpty()) {
            return 0;
        }

        long downloadedSize = chunks.stream()
                .filter( el -> el.getCurrentSize() != null)
                .mapToLong(FileChunkDto::getCurrentSize)
                .sum();

        return (int) Math.round(downloadedSize * 100.0d / totalSize);
    }

}
