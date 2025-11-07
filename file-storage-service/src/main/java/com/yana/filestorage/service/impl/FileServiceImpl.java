package com.yana.filestorage.service.impl;

import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.entity.File;
import com.yana.filestorage.exception.FileActionException;
import com.yana.filestorage.exception.DirectoryNotFoundException;
import com.yana.filestorage.repository.DirectoryRepository;
import com.yana.filestorage.repository.FileRepository;
import com.yana.filestorage.service.FileService;
import com.yana.filestorage.service.MinioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public Long save(String name, Long directoryId, Long userId) {
        UUID uuid = UUID.randomUUID();

        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new DirectoryNotFoundException("Directory not found"));

        File file = File.builder()
                .name(name)
                .uuid(uuid)
                .directory(directory)
                .userId(userId)
                .build();

        fileRepository.save(file);
        return file.getId();
    }

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

}
