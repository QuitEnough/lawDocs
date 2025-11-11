package com.yana.filestorage.service;

import com.yana.filestorage.entity.Directory;

import java.util.List;

public interface DirectoryService {

    List<Directory> findDirectoryByUserId(Long userId);

    List<Directory> findAllDirectoriesInCertainDir(Long directoryId);

    void deleteDirectory(Long directoryId);

    boolean isDirectoryOwner(Long directoryId, Long userId);

    Directory createDirectory(String name, Long parentId, Long userId);

    void renameDirectory(Long directoryId, String newName);

    Directory findDirectoryById(Long directoryId);

}
