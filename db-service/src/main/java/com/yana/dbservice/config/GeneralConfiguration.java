package com.yana.dbservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfiguration {

    @Bean
    public MinioClient getMinioClient(@Value("${spring.minio.url}") String url,
                                      @Value("${spring.minio.access-key}") String accessKey,
                                      @Value("${spring.minio.secret-key}") String secretKey,
                                      @Value("${spring.minio.port}") int port) {
        return MinioClient.builder()
                .endpoint(url, port, false)
                .credentials(accessKey, secretKey)
                .build();
    }

}
