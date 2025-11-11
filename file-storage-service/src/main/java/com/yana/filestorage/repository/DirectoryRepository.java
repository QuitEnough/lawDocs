package com.yana.filestorage.repository;

import com.yana.filestorage.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    List<Directory> findDirectoriesByUserId(Long userId);

    List<Directory> findDirectoriesByParentId(Long parentId);

    @Query("SELECT COUNT(d) > 0 "
            + "FROM Directory d "
            + "WHERE d.name = :name "
            + "AND d.parentId = :parentId "
            + "AND d.userId = :userId")
    boolean existsByNameAndParentIdAndUserId(@Param("name") String name,
                                             @Param("parentId") Long parentId,
                                             @Param("userId") Long userId);

}
