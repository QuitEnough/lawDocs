package com.yana.ai.repository;

import com.yana.ai.model.LoadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<LoadedDocument, Long> {

    boolean existsByFilenameAndContentHash(String filename, String contentHash);

}
