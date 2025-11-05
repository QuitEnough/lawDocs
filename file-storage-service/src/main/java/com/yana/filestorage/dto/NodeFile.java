package com.yana.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

@Builder
public record NodeFile(
        String type,
        Long id,
        String name,
        @JsonIgnore Long parentId
) { }