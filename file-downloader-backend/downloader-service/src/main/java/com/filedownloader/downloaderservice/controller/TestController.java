package com.filedownloader.downloaderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Test", description = "Test API")
@RequestMapping(path = "/api/v1/test", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {

    @GetMapping("/{id}")
    @Operation(summary = "Get note by id")
    public String getStringById(@PathVariable String id) {
        log.info("Get string by id {}", id);
        return "Your id is " + id;
    }

    /*
    @PostMapping
public ResponseEntity<UserDto> create(@RequestBody UserCreateRequest req) {
    UserDto saved = service.create(req);

    return ResponseEntity
            .created(URI.create("/users/" + saved.id()))
            .body(saved);
}
     */
}
