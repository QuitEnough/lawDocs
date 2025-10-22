package com.yana.dbservice.service;

import com.yana.dbservice.entity.Directory;

import java.util.List;

public interface DirectoryService {

    List<Directory> findDirectoryByUserId(Long userId);

    List<Directory> findAllDirectoriesInCertainDir(Long directoryId);

}
