package com.yana.ai.repository;

import com.yana.ai.model.LoadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<LoadedDocument, Long> {

    boolean existsByFilenameAndContentHash(String filename, String contentHash);

}
