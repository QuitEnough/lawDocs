package com.yana.dbservice.repository;

import org.jetbrains.annotations.NotNull;
import com.yana.dbservice.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, Long> {

    void deleteById(@NotNull Long fileId);

    void findByUuid(UUID uuid);

    List<File> findFilesByUserId(Long userId);

    List<File> findFilesByDirectoryId(Long directoryId);

}
