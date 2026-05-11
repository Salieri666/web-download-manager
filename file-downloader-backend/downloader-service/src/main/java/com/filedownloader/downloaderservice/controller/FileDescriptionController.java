package com.filedownloader.downloaderservice.controller;

import com.filedownloader.downloaderservice.model.dto.CreateFileDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionDto;
import com.filedownloader.downloaderservice.model.dto.FileDescriptionWithChunksDto;
import com.filedownloader.downloaderservice.model.filter.FileDescriptionFilter;
import com.filedownloader.downloaderservice.service.FileDescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springdoc.core.annotations.ParameterObject;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/file-description", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "File description", description = "File description API")
public class FileDescriptionController {

    private final FileDescriptionService service;

    @PostMapping("/all")
    @Operation(summary = "Get all file descriptions by filter")
    public Page<FileDescriptionDto> getAllFileDescriptions(
            @RequestBody(required = false) FileDescriptionFilter filter,
            @ParameterObject Pageable pageable
    ) {
        log.info("Get all file descriptions by filter {}", filter);
        return service.getAll(filter, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get file description by id")
    public FileDescriptionWithChunksDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create file description")
    public FileDescriptionDto create(@Valid @RequestBody CreateFileDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete file description")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
