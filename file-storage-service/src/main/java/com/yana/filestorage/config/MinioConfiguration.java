package com.yana.filestorage.config;

import com.yana.filestorage.exception.MinioBucketException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Bean
    public MinioClient getMinioClient(@Value("${spring.minio.url}") String url,
                                      @Value("${spring.minio.access-key}") String accessKey,
                                      @Value("${spring.minio.secret-key}") String secretKey,
                                      @Value("${spring.minio.port}") int port,
                                      @Value("${spring.minio.bucket}") String bucketName) {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(url, port, false)
                .credentials(accessKey, secretKey)
                .build();

        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new MinioBucketException("MinIO bucket initialization failed");
        }

        return minioClient;
    }

}
