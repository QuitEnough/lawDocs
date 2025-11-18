package com.yana.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.util.List;

@Builder
public record NodeDirBACKUP(
        String type,
        Long id,
        String name,
        @JsonIgnore Long parentId,
        @JsonIgnore List<NodeDirBACKUP> childrenDirs,
        @JsonIgnore List<NodeFileBACKUP> files
) { }
