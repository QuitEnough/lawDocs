package com.yana.dbservice.service.impl;

import com.yana.dbservice.entity.Directory;
import com.yana.dbservice.repository.DirectoryRepository;
import com.yana.dbservice.service.DirectoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;

    @Override
    public List<Directory> findDirectoryByUserId(Long userId) {
        log.debug("[DirectoryService] finding directories for user with id {}", userId);
        return directoryRepository.findDirectoriesByUserId(userId);
    }

    @Override
    public List<Directory> findAllDirectoriesInCertainDir(Long directoryId) {
        log.debug("[DirectoryService] finding directories in directory with id {}", directoryId);
        return directoryRepository.findDirectoriesByParentId(directoryId);
    }

}
