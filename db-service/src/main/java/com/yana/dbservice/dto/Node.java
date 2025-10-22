package com.yana.dbservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Node {

    private List<NodeDir> dirs;

    private List<NodeFile> files;

    public static Node generateNode(List<NodeDir> dirs, List<NodeFile> files) {
        return Node.builder()
                .dirs(dirs)
                .files(files)
                .build();
    }

}
