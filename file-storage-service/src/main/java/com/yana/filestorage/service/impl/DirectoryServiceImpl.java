package com.yana.filestorage.service.impl;

import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.exception.AccessDeniedException;
import com.yana.filestorage.exception.DirectoryActionException;
import com.yana.filestorage.exception.DirectoryAlreadyExists;
import com.yana.filestorage.exception.DirectoryNotFoundException;
import com.yana.filestorage.repository.DirectoryRepository;
import com.yana.filestorage.service.DirectoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;

    @Override
    public List<Directory> findDirectoryByUserId(Long userId) {
        return directoryRepository.findDirectoriesByUserId(userId);
    }

    @Override
    public List<Directory> findAllDirectoriesInCertainDir(Long directoryId) {
        return directoryRepository.findDirectoriesByParentId(directoryId);
    }

    @Override
    public void deleteDirectory(Long directoryId) {
        directoryRepository.deleteById(directoryId);
    }

    @Override
    public boolean isDirectoryOwner(Long directoryId, Long userId) {
        Optional<Directory> directory = directoryRepository.findById(directoryId);
        return directory.isPresent() && directory.get().getUserId().equals(userId);
    }

    @Transactional
    @Override
    public Directory createDirectory(String name, Long parentId, Long userId) {
        if (parentId != null) {
            Directory parentDirectory = directoryRepository
                    .findById(parentId)
                    .orElseThrow(() -> new DirectoryNotFoundException("Parent directory not found"));

            if (!parentDirectory.getUserId().equals(userId)) {
                throw new AccessDeniedException("Access denied to parent directory");
            }
        }

        boolean directoryExists = directoryRepository.existsByNameAndParentIdAndUserId(name, parentId, userId);
        if (directoryExists) {
            throw new DirectoryAlreadyExists("Directory with this name already exists in the specified location");
        }

        Directory directory = Directory.builder()
                .name(name)
                .parentId(parentId)
                .userId(userId)
                .build();

        return directoryRepository.save(directory);
    }

    @Transactional
    @Override
    public void renameDirectory(Long directoryId, String newName) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new DirectoryNotFoundException("Directory not found"));

        if (newName == null || newName.trim().isEmpty()) {
            throw new DirectoryActionException("Directory name cannot be empty");
        }

        if (newName.equals(directory.getName())) {
            return;
        }

        boolean directoryExists = directoryRepository.existsByNameAndParentIdAndUserId(
                newName,
                directory.getParentId(),
                directory.getUserId());
        if (directoryExists) {
            throw new DirectoryAlreadyExists("Directory with this name already exists in the same location");
        }

        directory.setName(newName);
        directoryRepository.save(directory);
    }

    @Override
    public Directory findDirectoryById(Long directoryId) {
        return directoryRepository.findById(directoryId)
                .orElseThrow(() -> new DirectoryNotFoundException("Directory not found"));
    }
}
