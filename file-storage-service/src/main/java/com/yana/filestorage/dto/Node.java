package com.yana.filestorage.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record Node(
        List<NodeDir> dirs,
        List<NodeFile> files
) {

    public static Node generateNode(List<NodeDir> dirs, List<NodeFile> files) {
        return new Node(dirs, files);
    }
}
