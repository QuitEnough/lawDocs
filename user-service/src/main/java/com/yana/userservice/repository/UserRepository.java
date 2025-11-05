package com.yana.userservice.repository;

import com.yana.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = """
            SELECT f 
            FROM File f 
            WHERE f.id = :fileId 
            AND f.userId = :userId
            """)
    boolean isFileOwner(@Param("userId") Long userId, @Param("fileId") Long fileId);

}
