package com.yana.filestorage.service;

import com.yana.filestorage.api.dto.Node;
import com.yana.filestorage.api.dto.NodeDir;
import com.yana.filestorage.api.dto.NodeFile;
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

    private static Node generateNode(List<NodeDir> dirs, List<NodeFile> files) {
        Node node = new Node();
        node.setDirs(dirs);
        node.setFiles(files);
        return node;
    }

    public Node getDataForCertainDir(Long directoryId) {

        List<Directory> dirs = directoryService.findAllDirectoriesInCertainDir(directoryId);
        List<NodeDir> nodeDirList = new ArrayList<>();

        for (Directory dir : dirs) {
            NodeDir nodeDir = new NodeDir()
                    .type("dir")
                    .id(dir.getId())
                    .name(dir.getName());
            nodeDirList.add(nodeDir);
        }

        List<File> files = fileService.findAllFilesInCertainDir(directoryId);
        List<NodeFile> nodeFileList = new ArrayList<>();

        for (File file : files) {
            NodeFile nodeFile = new NodeFile()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName());
            nodeFileList.add(nodeFile);
        }

        return generateNode(nodeDirList, nodeFileList);
    }

    public Node getRootDirsWithFilesForUser(Long userId) {
        // 1. Получаем исходные сущности
        List<Directory> allDirs = directoryService.findDirectoryByUserId(userId);
        List<File> allFiles = fileRepository.findFilesWithDirectoryByUserId(userId);

        // 2. Строим мапы
        Map<Long, Directory> dirById = allDirs.stream()
                .collect(Collectors.toMap(Directory::getId, d -> d));

        // parentId -> List<Directory>
        MultiValueMap<Long, Directory> childrenByParent = new LinkedMultiValueMap<>();
        for (Directory dir : allDirs) {
            Long parentId = dir.getParentId();
            childrenByParent.add(parentId, dir); // null parentId = корень
        }

        // directoryId -> List<NodeFile>
        MultiValueMap<Long, NodeFile> filesByDir = new LinkedMultiValueMap<>();
        List<NodeFile> rootFiles = new ArrayList<>(); // файлы без директории
        for (File file : allFiles) {
            NodeFile nodeFile = new NodeFile()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName())
                    .parentId(file.getDirectory() != null ? file.getDirectory().getId() : null);

            if (file.getDirectory() == null) {
                rootFiles.add(nodeFile);
            } else {
                filesByDir.add(file.getDirectory().getId(), nodeFile);
            }
        }

        // 3. Рекурсивно строим NodeDir (можно и итеративно, но рекурсия чище для деревьев)
        List<NodeDir> rootDirs = Optional.ofNullable(childrenByParent.get(null))
                .orElse(Collections.emptyList())
                .stream()
                .map(dir -> buildNodeDir(dir, dirById, childrenByParent, filesByDir))
                .collect(Collectors.toList());

        return new Node().dirs(rootDirs).files(rootFiles);
    }

    private NodeDir buildNodeDir(
            Directory dir,
            Map<Long, Directory> dirById,
            MultiValueMap<Long, Directory> childrenByParent,
            MultiValueMap<Long, NodeFile> filesByDir) {

        // Безопасное получение детей
        List<Directory> childDirs = childrenByParent.getOrDefault(dir.getId(), Collections.emptyList());
        List<NodeDir> children = childDirs.stream()
                .map(childDir -> buildNodeDir(childDir, dirById, childrenByParent, filesByDir))
                .collect(Collectors.toList());

        // Безопасное получение файлов
        List<NodeFile> files = filesByDir.getOrDefault(dir.getId(), Collections.emptyList());

        return new NodeDir()
                .type("dir")
                .id(dir.getId())
                .name(dir.getName())
                .parentId(dir.getParentId())
                .childrenDirs(children.isEmpty() ? List.of() : List.copyOf(children))
                .files(files.isEmpty() ? List.of() : List.copyOf(files));
    }

    //TODO: будет работать, если убрать аннотацию JsonIgnore или сделать dto response. Подумать, для чего этот метод
    /*public Node getRootDirsWithFilesForUser(Long userId) {
        List<NodeDir> all = directoryService.findDirectoryByUserId(userId)
                .stream()
                .map(e -> new NodeDir()
                        .type("dir")
                        .id(e.getId())
                        .name(e.getName())
                        .parentId(e.getParentId())
                        .childrenDirs(List.of())
                        .files(List.of()))
                .toList();

        // Строим дерево используя Map для быстрого доступа
        MultiValueMap<Long, NodeDir> childrenMap = new LinkedMultiValueMap<>();
        Map<Long, NodeDir> dirMap = new HashMap<>();
        List<NodeDir> rootList = new ArrayList<>();

        // Сначала создаем map всех директорий и собираем детей
        for (NodeDir dir : all) {
            dirMap.put(dir.getId(), dir);
            if (dir.getParentId() == null) {
                rootList.add(dir);
            } else {
                childrenMap.add(dir.getParentId(), dir);
            }
        }

        List<NodeDir> mutableRootList = new ArrayList<>();
        Map<Long, NodeDir> mutableDirMap = new HashMap<>();

        // Заменяем immutable списки на mutable и заполняем детей
        for (NodeDir dir : all) {
            // Создаем mutable копии
            List<NodeDir> mutableChildren = new ArrayList<>(
                    childrenMap.getOrDefault(dir.getId(), Collections.emptyList())
            );

            // Создаем новую NodeDir с mutable списками
            NodeDir mutableDir = new NodeDir()
                    .type("dir")
                    .id(dir.getId())
                    .name(dir.getName())
                    .parentId(dir.getParentId())
                    .childrenDirs(mutableChildren)
                    .files(new ArrayList<>());

            mutableDirMap.put(dir.getId(), mutableDir);

            if (dir.getParentId() == null) {
                mutableRootList.add(mutableDir);
            }
        }

        // Теперь нужно обновить parent ссылки в детях
        for (NodeDir mutableDir : mutableDirMap.values()) {
            if (mutableDir.getChildrenDirs() != null && !mutableDir.getChildrenDirs().isEmpty()) {
                List<NodeDir> updatedChildren = mutableDir.getChildrenDirs().stream()
                        .map(child -> mutableDirMap.get(child.getId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                // Здесь проблема - NodeDir immutable, нужно пересоздать
                mutableDir = new NodeDir()
                        .type(mutableDir.getType())
                        .id(mutableDir.getId())
                        .name(mutableDir.getName())
                        .parentId(mutableDir.getParentId())
                        .childrenDirs(updatedChildren)
                        .files(mutableDir.getFiles());
                mutableDirMap.put(mutableDir.getId(), mutableDir);
            }
        }

        // Обновляем rootList
        mutableRootList = mutableRootList.stream()
                .map(root -> mutableDirMap.get(root.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return process(userId, rootList);
    }*/

    public Node process(Long userId, List<NodeDir> nodeDir) {
        List<NodeFile> topFiles = new ArrayList<>();

        // Получаем все файлы пользователя
        List<File> allFiles = fileRepository.findFilesWithDirectoryByUserId(userId);
        log.debug("Found {} files for user {}", allFiles.size(), userId);

        // Создаем map для быстрого доступа к файлам по directoryId
        Map<Long, List<NodeFile>> filesByDirectory = new HashMap<>();

        for (File file : allFiles) {
            NodeFile nodeFile = new NodeFile()
                    .type("file")
                    .id(file.getId())
                    .name(file.getName())
                    .parentId(file.getDirectory().getId());

            Long directoryId = file.getDirectory().getId();
            if (directoryId == null) {
                topFiles.add(nodeFile);
            } else {
                filesByDirectory.computeIfAbsent(directoryId, k -> new ArrayList<>()).add(nodeFile);
            }
        }

        // Распределяем файлы по директориям используя BFS
        Queue<NodeDir> queue = new LinkedList<>(nodeDir);
        int processedDirs = 0;

        while (!queue.isEmpty()) {
            NodeDir currentDir = queue.poll();
            processedDirs++;

            // Добавляем файлы для текущей директории
            List<NodeFile> dirFiles = filesByDirectory.get(currentDir.getId());
            if (dirFiles != null) {
                currentDir.getFiles().addAll(dirFiles);
            }

            // Добавляем поддиректории в очередь
            if (currentDir.getChildrenDirs() != null && !currentDir.getChildrenDirs().isEmpty()) {
                queue.addAll(currentDir.getChildrenDirs());
            }
        }

        return generateNode(nodeDir, topFiles);
    }

}
