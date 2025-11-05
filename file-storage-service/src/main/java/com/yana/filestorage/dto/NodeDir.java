package com.yana.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.util.List;

@Builder
public record NodeDir(
        String type,
        Long id,
        String name,
        @JsonIgnore Long parentId,
        @JsonIgnore List<NodeDir> childrenDirs,
        @JsonIgnore List<NodeFile> files
) { }
