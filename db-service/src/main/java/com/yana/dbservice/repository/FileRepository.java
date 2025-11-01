package com.yana.dbservice.repository;

import com.yana.dbservice.entity.Directory;
import org.jetbrains.annotations.NotNull;
import com.yana.dbservice.entity.File;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    void deleteById(@NotNull Long fileId);

    void findByUuid(UUID uuid);

    List<File> findFilesByUserId(Long userId);

    List<File> findFilesByDirectory(Directory directory);

    List<File> findFilesByDirectoryId(Long directoryId);

    @EntityGraph(attributePaths = {"directory"})
    List<File> findFilesWithDirectoryByDirectoryId(Long directoryId);

    @EntityGraph(attributePaths = {"directory"})
    List<File> findFilesWithDirectoryByUserId(Long userId);
}
