package com.yana.dbservice.service.impl;

import com.yana.dbservice.entity.Directory;
import com.yana.dbservice.repository.DirectoryRepository;
import com.yana.dbservice.service.DirectoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
