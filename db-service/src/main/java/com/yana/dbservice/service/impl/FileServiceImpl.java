package com.yana.dbservice.service.impl;

import com.yana.dbservice.entity.Directory;
import com.yana.dbservice.entity.File;
import com.yana.dbservice.exception.FileActionException;
import com.yana.dbservice.exception.DirectoryNotFoundException;
import com.yana.dbservice.repository.DirectoryRepository;
import com.yana.dbservice.repository.FileRepository;
import com.yana.dbservice.service.FileService;
import com.yana.dbservice.service.MinioService;
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

//    @Override
//    public Long save(String name, Long directoryId, Long userId) {
//
//        log.info("[FileService] saving the file with name {}, directoryId {}, userId {}", name, directoryId, userId);
//
//        UUID uuid = UUID.randomUUID();
//
//        File file = File.builder()
//                .name(name)
//                .uuid(uuid)
//                .directoryId(directoryId)
//                .userId(userId)
//                .build();
//
//        fileRepository.save(file);
//        return file.getId();
//    }

    @Override
    public Long save(String name, Long directoryId) {
        UUID uuid = UUID.randomUUID();

        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new DirectoryNotFoundException("Directory not found"));

        File file = File.builder()
                .name(name)
                .uuid(uuid)
                .directory(directory)
                .build();

        fileRepository.save(file);
        return file.getId();
    }

    @Override
    public void delete(Long fileId) {
        if (fileRepository.findById(fileId).isPresent()) {
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

}
