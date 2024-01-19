package com.example.highload.image.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinioConfig {

    @Value("${minio.access.name}")
    private String accessKey;
    @Value("${minio.access.secret}")
    private String accessSecret;
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.port}")
    private Integer minioPort;

    @Value("${minio.secure}")
    private boolean minioSecure;

    @Value("${minio.imageSize}")
    private long imageSize;

    @Value("${minio.fileSize}")
    private long fileSize;

    @Value("${minio.bucket.name}")
    String defaultBucketName;

    @Value("${minio.default.folder}")
    String defaultBaseFolder;

    @Bean
    public MinioClient generateMinioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .credentials(accessKey, accessSecret)
                    .endpoint(minioUrl, minioPort, minioSecure)
                    .build();
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
