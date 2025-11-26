package com.yana.filestorage.service.impl;

import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.entity.File;
import com.yana.filestorage.exception.*;
import com.yana.filestorage.repository.DirectoryRepository;
import com.yana.filestorage.repository.FileRepository;
import com.yana.filestorage.service.FileService;
import com.yana.filestorage.service.MinioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    private final DirectoryRepository directoryRepository;

    private final MinioService minioService;

    @Transactional
    @Override
    public Long save(String name, Long directoryId, Long userId) {
        UUID uuid = UUID.randomUUID();

        Directory directory = null;
        if (directoryId != null) {
            directory = directoryRepository.findById(directoryId)
                    .orElse(null);
        }

        File file = File.builder()
                .name(name)
                .uuid(uuid)
                .directory(directory)
                .userId(userId)
                .build();

        fileRepository.save(file);
        return file.getId();
    }

    @Transactional
    @Override
    public void delete(Long fileId) {
        Optional<File> file = fileRepository.findById(fileId);
        if (file.isPresent()) {
            minioService.delete(file.get().getUuid());
            fileRepository.deleteById(fileId);
        } else {
            throw new FileActionException("The file is not present");
        }
    }

    @Override
    public UUID find(Long fileId) {
        Optional<File> file = fileRepository.findById(fileId);
        if (file.isPresent()) {
            return file.get().getUuid();
        } else {
            throw new FileActionException("The file is not present");
        }
    }

    @Override
    public InputStream download(Long fileId) {
        Optional<File> file = fileRepository.findById(fileId);
        if (file.isPresent()) {
            return minioService.find(file.get().getUuid());
        } else {
            throw new FileActionException("The file is not present");
        }
    }

    @Override
    public List<File> findAllFilesByUserId(Long userId) {
        return fileRepository.findFilesByUserId(userId);
    }

    @Override
    public List<File> findAllFilesInCertainDir(Long directoryId) {
        return fileRepository.findFilesByDirectoryId(directoryId);
    }

    @Override
    public boolean isFileOwner(Long fileId, Long userId) {
        Optional<File> file = fileRepository.findById(fileId);
        return file.isPresent() && file.get().getUserId().equals(userId);
    }

    @Override
    public void ensureFileOwnership(Long fileId, Long userId) {
        if (!isFileOwner(fileId, userId)) {
            throw new AccessDeniedException("File does not belong to user");
        }
    }

    @Transactional
    @Override
    public void renameFile(Long fileId, String newName) {
        var file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        if (newName == null || newName.trim().isEmpty()) {
            throw new FileActionException("File name cannot be empty");
        }

        if (newName.equals(file.getName())) {
            return;
        }

        var directoryId = (file.getDirectory() != null) ? file.getDirectory().getId() : null;

        var fileExists = fileRepository.existsByNameAndDirectoryIdAndUserId(
                newName,
                directoryId,
                file.getUserId());

        if (fileExists) {
            throw new FileAlreadyExistsException("File with this name already exists in the same directory");
        }

        file.setName(newName);
        fileRepository.save(file);
    }

}
