package com.yana.filestorage.service;

import com.yana.filestorage.dto.Node;
import com.yana.filestorage.dto.NodeDir;
import com.yana.filestorage.dto.NodeFile;
import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.entity.File;
import com.yana.filestorage.repository.FileRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StructureService {

    private final FileService fileService;
    private final DirectoryService directoryService;
    private final FileRepository fileRepository;

    public Node getDataForCertainDir(Long directoryId) {

        List<Directory> dirs = directoryService.findAllDirectoriesInCertainDir(directoryId);
        List<NodeDir> nodeDirList = new ArrayList<>();

        for (Directory dir : dirs) {
            NodeDir nodeDir = NodeDir.builder()
                    .type("dir")
                    .id(dir.getId())
                    .name(dir.getName())
                    .build();
            nodeDirList.add(nodeDir);
        }

        List<File> files = fileService.findAllFilesInCertainDir(directoryId);
        List<NodeFile> nodeFileList = new ArrayList<>();

        for (File file : files) {
            NodeFile nodeFile = NodeFile.builder()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName())
                    .build();
            nodeFileList.add(nodeFile);
        }

        return Node.generateNode(nodeDirList, nodeFileList);
    }

    //TODO: будет работать, если убрать аннотацию JsonIgnore или сделать dto response. Подумать, для чего этот метод
    public Node getRootDirsWithFilesForUser(Long userId) {
        List<NodeDir> all = directoryService.findDirectoryByUserId(userId)
                .stream()
                .map(e -> NodeDir.builder()
                        .type("dir")
                        .id(e.getId())
                        .name(e.getName())
                        .parentId(e.getParentId())
                        .childrenDirs(List.of())
                        .files(List.of())
                        .build())
                .toList();

        // Строим дерево используя Map для быстрого доступа
        MultiValueMap<Long, NodeDir> childrenMap = new LinkedMultiValueMap<>();
        Map<Long, NodeDir> dirMap = new HashMap<>();
        List<NodeDir> rootList = new ArrayList<>();

        // Сначала создаем map всех директорий и собираем детей
        for (NodeDir dir : all) {
            dirMap.put(dir.id(), dir);
            if (dir.parentId() == null) {
                rootList.add(dir);
            } else {
                childrenMap.add(dir.parentId(), dir);
            }
        }

        List<NodeDir> mutableRootList = new ArrayList<>();
        Map<Long, NodeDir> mutableDirMap = new HashMap<>();

        // Заменяем immutable списки на mutable и заполняем детей
        for (NodeDir dir : all) {
            // Создаем mutable копии
            List<NodeDir> mutableChildren = new ArrayList<>(
                    childrenMap.getOrDefault(dir.id(), Collections.emptyList())
            );

            // Создаем новую NodeDir с mutable списками
            NodeDir mutableDir = NodeDir.builder()
                    .type("dir")
                    .id(dir.id())
                    .name(dir.name())
                    .parentId(dir.parentId())
                    .childrenDirs(mutableChildren)
                    .files(new ArrayList<>())
                    .build();

            mutableDirMap.put(dir.id(), mutableDir);

            if (dir.parentId() == null) {
                mutableRootList.add(mutableDir);
            }
        }

        // Теперь нужно обновить parent ссылки в детях
        for (NodeDir mutableDir : mutableDirMap.values()) {
            if (mutableDir.childrenDirs() != null && !mutableDir.childrenDirs().isEmpty()) {
                List<NodeDir> updatedChildren = mutableDir.childrenDirs().stream()
                        .map(child -> mutableDirMap.get(child.id()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                // Здесь проблема - NodeDir immutable, нужно пересоздать
                mutableDir = NodeDir.builder()
                        .type(mutableDir.type())
                        .id(mutableDir.id())
                        .name(mutableDir.name())
                        .parentId(mutableDir.parentId())
                        .childrenDirs(updatedChildren)
                        .files(mutableDir.files())
                        .build();
                mutableDirMap.put(mutableDir.id(), mutableDir);
            }
        }

        // Обновляем rootList
        mutableRootList = mutableRootList.stream()
                .map(root -> mutableDirMap.get(root.id()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return process(userId, rootList);
    }

    public Node process(Long userId, List<NodeDir> nodeDirs) {
        List<NodeFile> topFiles = new ArrayList<>();

        // Получаем все файлы пользователя
        List<File> allFiles = fileRepository.findFilesWithDirectoryByUserId(userId);
        log.debug("Found {} files for user {}", allFiles.size(), userId);

        // Создаем map для быстрого доступа к файлам по directoryId
        Map<Long, List<NodeFile>> filesByDirectory = new HashMap<>();

        for (File file : allFiles) {
            NodeFile nodeFile = NodeFile.builder()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName())
                    .parentId(file.getDirectory().getId())
                    .build();

            Long directoryId = file.getDirectory().getId();
            if (directoryId == null) {
                topFiles.add(nodeFile);
            } else {
                filesByDirectory.computeIfAbsent(directoryId, k -> new ArrayList<>()).add(nodeFile);
            }
        }

        // Распределяем файлы по директориям используя BFS
        Queue<NodeDir> queue = new LinkedList<>(nodeDirs);
        int processedDirs = 0;

        while (!queue.isEmpty()) {
            NodeDir currentDir = queue.poll();
            processedDirs++;

            // Добавляем файлы для текущей директории
            List<NodeFile> dirFiles = filesByDirectory.get(currentDir.id());
            if (dirFiles != null) {
                currentDir.files().addAll(dirFiles);
            }

            // Добавляем поддиректории в очередь
            if (currentDir.childrenDirs() != null && !currentDir.childrenDirs().isEmpty()) {
                queue.addAll(currentDir.childrenDirs());
            }
        }

        return Node.generateNode(nodeDirs, topFiles);
    }

}
