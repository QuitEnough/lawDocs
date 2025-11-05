package com.yana.filestorage.service;

import com.yana.filestorage.entity.Directory;

import java.util.List;

public interface DirectoryService {

    List<Directory> findDirectoryByUserId(Long userId);

    List<Directory> findAllDirectoriesInCertainDir(Long directoryId);

    void deleteDirectory(Long directoryId);

}
