package com.yana.dbservice.service.impl;

import com.yana.dbservice.exception.FileActionException;
import com.yana.dbservice.service.MinioService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {

    @Value("${spring.minio.bucket}")
    private final String bucket;

    private final MinioClient minioClient;

    @Override
    public boolean save(UUID uuid, MultipartFile multipartFile) {

        log.debug("[MinioService] Saving file {} with uuid {}", multipartFile, uuid);

        try {
            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .stream(inputStream, inputStream.available(), -1)
                            .bucket(bucket)
                            .object(uuid.toString())
                            .build());
        } catch (Exception e) {
            throw new FileActionException("Unable to save file", e);
        }

        return true;
    }

    @Override
    public void delete(UUID uuid) {

        log.debug("[MinioService] deleting file with uuid {}", uuid);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(uuid.toString())
                            .build());
        } catch (Exception e) {
            throw new FileActionException("Unable to delete the file", e);
        }
    }

    @Override
    public InputStream find(UUID uuid) {

        log.debug("[MinioService] finding the file with uuid {}", uuid);

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(uuid.toString())
                            .build()
            );
        } catch (Exception e) {
            throw new FileActionException("Unable to download", e);
        }
    }

}
