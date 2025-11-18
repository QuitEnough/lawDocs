package com.yana.filestorage.service;

import com.yana.filestorage.dto.NodeBACKUP;
import com.yana.filestorage.dto.NodeDirBACKUP;
import com.yana.filestorage.dto.NodeFileBACKUP;
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

    public NodeBACKUP getDataForCertainDir(Long directoryId) {

        List<Directory> dirs = directoryService.findAllDirectoriesInCertainDir(directoryId);
        List<NodeDirBACKUP> nodeDirBACKUPList = new ArrayList<>();

        for (Directory dir : dirs) {
            NodeDirBACKUP nodeDirBACKUP = NodeDirBACKUP.builder()
                    .type("dir")
                    .id(dir.getId())
                    .name(dir.getName())
                    .build();
            nodeDirBACKUPList.add(nodeDirBACKUP);
        }

        List<File> files = fileService.findAllFilesInCertainDir(directoryId);
        List<NodeFileBACKUP> nodeFileBACKUPList = new ArrayList<>();

        for (File file : files) {
            NodeFileBACKUP nodeFileBACKUP = NodeFileBACKUP.builder()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName())
                    .build();
            nodeFileBACKUPList.add(nodeFileBACKUP);
        }

        return NodeBACKUP.generateNode(nodeDirBACKUPList, nodeFileBACKUPList);
    }

    //TODO: будет работать, если убрать аннотацию JsonIgnore или сделать dto response. Подумать, для чего этот метод
    public NodeBACKUP getRootDirsWithFilesForUser(Long userId) {
        List<NodeDirBACKUP> all = directoryService.findDirectoryByUserId(userId)
                .stream()
                .map(e -> NodeDirBACKUP.builder()
                        .type("dir")
                        .id(e.getId())
                        .name(e.getName())
                        .parentId(e.getParentId())
                        .childrenDirs(List.of())
                        .files(List.of())
                        .build())
                .toList();

        // Строим дерево используя Map для быстрого доступа
        MultiValueMap<Long, NodeDirBACKUP> childrenMap = new LinkedMultiValueMap<>();
        Map<Long, NodeDirBACKUP> dirMap = new HashMap<>();
        List<NodeDirBACKUP> rootList = new ArrayList<>();

        // Сначала создаем map всех директорий и собираем детей
        for (NodeDirBACKUP dir : all) {
            dirMap.put(dir.id(), dir);
            if (dir.parentId() == null) {
                rootList.add(dir);
            } else {
                childrenMap.add(dir.parentId(), dir);
            }
        }

        List<NodeDirBACKUP> mutableRootList = new ArrayList<>();
        Map<Long, NodeDirBACKUP> mutableDirMap = new HashMap<>();

        // Заменяем immutable списки на mutable и заполняем детей
        for (NodeDirBACKUP dir : all) {
            // Создаем mutable копии
            List<NodeDirBACKUP> mutableChildren = new ArrayList<>(
                    childrenMap.getOrDefault(dir.id(), Collections.emptyList())
            );

            // Создаем новую NodeDir с mutable списками
            NodeDirBACKUP mutableDir = NodeDirBACKUP.builder()
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
        for (NodeDirBACKUP mutableDir : mutableDirMap.values()) {
            if (mutableDir.childrenDirs() != null && !mutableDir.childrenDirs().isEmpty()) {
                List<NodeDirBACKUP> updatedChildren = mutableDir.childrenDirs().stream()
                        .map(child -> mutableDirMap.get(child.id()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                // Здесь проблема - NodeDir immutable, нужно пересоздать
                mutableDir = NodeDirBACKUP.builder()
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

    public NodeBACKUP process(Long userId, List<NodeDirBACKUP> nodeDirBACKUPS) {
        List<NodeFileBACKUP> topFiles = new ArrayList<>();

        // Получаем все файлы пользователя
        List<File> allFiles = fileRepository.findFilesWithDirectoryByUserId(userId);
        log.debug("Found {} files for user {}", allFiles.size(), userId);

        // Создаем map для быстрого доступа к файлам по directoryId
        Map<Long, List<NodeFileBACKUP>> filesByDirectory = new HashMap<>();

        for (File file : allFiles) {
            NodeFileBACKUP nodeFileBACKUP = NodeFileBACKUP.builder()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName())
                    .parentId(file.getDirectory().getId())
                    .build();

            Long directoryId = file.getDirectory().getId();
            if (directoryId == null) {
                topFiles.add(nodeFileBACKUP);
            } else {
                filesByDirectory.computeIfAbsent(directoryId, k -> new ArrayList<>()).add(nodeFileBACKUP);
            }
        }

        // Распределяем файлы по директориям используя BFS
        Queue<NodeDirBACKUP> queue = new LinkedList<>(nodeDirBACKUPS);
        int processedDirs = 0;

        while (!queue.isEmpty()) {
            NodeDirBACKUP currentDir = queue.poll();
            processedDirs++;

            // Добавляем файлы для текущей директории
            List<NodeFileBACKUP> dirFiles = filesByDirectory.get(currentDir.id());
            if (dirFiles != null) {
                currentDir.files().addAll(dirFiles);
            }

            // Добавляем поддиректории в очередь
            if (currentDir.childrenDirs() != null && !currentDir.childrenDirs().isEmpty()) {
                queue.addAll(currentDir.childrenDirs());
            }
        }

        return NodeBACKUP.generateNode(nodeDirBACKUPS, topFiles);
    }

}
