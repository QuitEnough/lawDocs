package com.yana.filestorage.repository;

import com.yana.filestorage.entity.Directory;
import com.yana.filestorage.entity.File;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COUNT(f) > 0 "
            + "FROM File f "
            + "WHERE f.name = :name "
            + "AND f.directory.id = :directoryId "
            + "AND f.userId = :userId")
    boolean existsByNameAndDirectoryIdAndUserId(@Param("name") String name,
                                                @Param("directoryId") Long directoryId,
                                                @Param("userId") Long userId);

}
