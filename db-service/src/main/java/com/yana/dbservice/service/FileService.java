package com.yana.dbservice.service;

import com.yana.dbservice.entity.File;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface FileService {

//    Long save(String name, Long directoryId, Long userId);
    Long save(String name, Long directoryId);

    void delete(Long fileId);

    UUID find(Long fileId);

    InputStream download(Long fileId);

    List<File> findAllFilesByUserId(Long userId);

    List<File> findAllFilesInCertainDir(Long directoryId);

}
