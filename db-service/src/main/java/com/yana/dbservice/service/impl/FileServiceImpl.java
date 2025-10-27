package com.yana.dbservice.service.impl;

import com.yana.dbservice.entity.File;
import com.yana.dbservice.repository.FileRepository;
import com.yana.dbservice.service.FileService;
import com.yana.dbservice.service.MinioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

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

        log.info("[FileService] saving the file with name {}, directoryId {}", name, directoryId);

        UUID uuid = UUID.randomUUID();

        File file = File.builder()
                .name(name)
                .uuid(uuid)
                .directoryId(directoryId)
                .build();

        fileRepository.save(file);
        return file.getId();
    }

    @Override
    public void delete(Long fileId) {
        log.debug("[FileService] deleting the file with id {}", fileId);
        fileRepository.deleteById(fileId);
    }

    @Override
    public UUID find(Long fileId) {

        log.debug("[FileService] finding the file with id {}", fileId);

        File file = fileRepository.findById(fileId).get();
        return file.getUuid();
    }

    @Override
    public InputStream download(Long fileId) {

        log.debug("[FileService] downloading the file with the id {}", fileId);

        File file = fileRepository.findById(fileId).get();
        //Optional<File> file = fileRepository.findById(fileId);
        return minioService.find(file.getUuid());
    }

    @Override
    public List<File> findAllFilesByUserId(Long userId) {
        log.debug("[FileService] finding files for user with id {}", userId);
        return fileRepository.findFilesByUserId(userId);
    }

    @Override
    public List<File> findAllFilesInCertainDir(Long directoryId) {
        log.debug("[FileService] finding files for user in directory with id {}", directoryId);
        return fileRepository.findFilesByDirectoryId(directoryId);
    }

}
