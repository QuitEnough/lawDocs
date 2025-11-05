package com.yana.filestorage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

public interface MinioService {

    boolean save(UUID uuid, MultipartFile multipartFile);

    void delete(UUID uuid);

    InputStream find(UUID uuid);

}
