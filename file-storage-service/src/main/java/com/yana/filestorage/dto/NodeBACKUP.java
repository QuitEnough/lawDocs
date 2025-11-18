package com.yana.filestorage.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record NodeBACKUP(
        List<NodeDirBACKUP> dirs,
        List<NodeFileBACKUP> files
) {

    public static NodeBACKUP generateNode(List<NodeDirBACKUP> dirs, List<NodeFileBACKUP> files) {
        return new NodeBACKUP(dirs, files);
    }
}
