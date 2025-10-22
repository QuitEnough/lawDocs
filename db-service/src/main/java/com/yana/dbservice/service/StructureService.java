package com.yana.dbservice.service;

import com.yana.dbservice.dto.Node;
import com.yana.dbservice.dto.NodeDir;
import com.yana.dbservice.dto.NodeFile;
import com.yana.dbservice.entity.Directory;
import com.yana.dbservice.entity.File;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class StructureService {

    private final FileService fileService;

    private final DirectoryService directoryService;

    public Node getDataForCertainDir(Long directoryId) {

        List<Directory> dirs = directoryService.findAllDirectoriesInCertainDir(directoryId);
        List<NodeDir> nodeDirList = new ArrayList<>();

        for (Directory directory : dirs) {
            NodeDir nodeDir = NodeDir.builder()
                    .type("dir")
                    .id(directory.getId())
                    .name(directory.getName())
                    .build();
            nodeDirList.add(nodeDir);
        }

        List<File> files = fileService.findAllFilesInCertainDir(directoryId);
        List<NodeFile> nodeFileList = new ArrayList<>();

        List<File> certainFiles = files.stream()
                .filter(file -> file.getDirectoryId().equals(directoryId))
                .toList();

        for (File file : certainFiles) {
            NodeFile nodeFile = NodeFile.builder()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName())
                    .build();
            nodeFileList.add(nodeFile);
        }

        return Node.generateNode(nodeDirList, nodeFileList);
    }

    public Node getEnvelopeDirsForUser(Long userId) {
        List<NodeDir> rootList = new ArrayList<>();
        List<NodeDir> toRemove = new ArrayList<>();

        List<NodeDir> all = directoryService.findDirectoryByUserId(userId)
                .stream()
                .map(e -> NodeDir.builder()
                        .type("dir")
                        .id(e.getId())
                        .name(e.getName())
                        .parentId(e.getParentId())
                        .childrenDirs(new ArrayList<>())
                        .files(new ArrayList<>())
                        .build())
                .toList();

        for (NodeDir dir : all) {
            if (dir.getParentId() == null) {
                rootList.add(dir);
                toRemove.add(dir);
            }
        }

        all.removeAll(toRemove);
        toRemove.clear();

        List<NodeDir> current = new LinkedList<>(rootList);
        List<NodeDir> next = new LinkedList<>();
        Iterator<NodeDir> iterator = current.iterator();

        while (!all.isEmpty()) {
            while (iterator.hasNext()) {
                NodeDir curr = iterator.next();
                for (NodeDir unknown : all) {
                    if (curr.getId().equals(unknown.getParentId())) {
                        curr.getChildrenDirs().add(unknown);

                        next.add(unknown);
                        toRemove.add(unknown);
                    }
                }
                all.removeAll(toRemove);
                toRemove.clear();
            }
            current = next;
        }

        return process(userId, rootList);
    }

    public Node process(Long userId, List<NodeDir> nodeDirs) {
        List<NodeFile> topFiles = new ArrayList<>();
        Node node = Node.generateNode(nodeDirs, topFiles);

        List<File> files = new ArrayList<>(fileService.findAllFilesByUserId(userId));
        List<File> toRemove = new ArrayList<>();

        Queue<NodeDir> queue = new LinkedList<>(nodeDirs);

        for (File file : files) {
            if (file.getDirectoryId() == null) {
                topFiles.add(NodeFile.builder()
                                .type("file")
                                .id(file.getId())
                                .name(file.getName())
                                .build());
                toRemove.add(file);
            }
        }

        files.removeAll(toRemove);
        toRemove.clear();

        NodeDir nodeDir;
        while ((nodeDir = queue.poll()) != null) {

            for (File file : files) {
                if (nodeDir.getId().equals(nodeDir.getParentId())) {
                    nodeDir.getFiles().add(NodeFile.builder()
                                    .type("file")
                                    .id(file.getId())
                                    .name(file.getName())
                                    .build());

                    queue.addAll(nodeDir.getChildrenDirs());
                }
            }
        }

        return node;
    }

}
