package com.filedownloader.corelib.model.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseFilter<T> {

    @Builder.Default
    @Schema(description = "Set of identifiers of entities")
    private Set<T> ids = new HashSet<>();

    @JsonIgnore
    public abstract boolean isEmpty();

}
