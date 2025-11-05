package com.yana.filestorage.repository;

import com.yana.filestorage.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    List<Directory> findDirectoriesByUserId(Long userId);

    List<Directory> findDirectoriesByParentId(Long parentId);

}
