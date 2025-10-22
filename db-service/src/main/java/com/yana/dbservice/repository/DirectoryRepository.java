package com.yana.dbservice.repository;

import com.yana.dbservice.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    List<Directory> findDirectoriesByUserId(Long userId);

    List<Directory> findDirectoriesByParentId(Long parentId);

}
