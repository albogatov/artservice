package com.example.highload.image.utils;

import com.example.highload.image.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MinioUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioUtil.class);

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    // Upload Files
    @SneakyThrows
    public void putObject(String bucketName, MultipartFile multipartFile, String filename, String fileType) {

        LOGGER.info("MinioUtil | putObject is called");

        LOGGER.info("MinioUtil | putObject | filename : " + filename);
        LOGGER.info("MinioUtil | putObject | fileType : " + fileType);

        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
        if (!bucketExists(minioConfig.getDefaultBucketName()))
            makeBucket(minioConfig.getDefaultBucketName());
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                                inputStream, -1, minioConfig.getFileSize())
                        .contentType(fileType)
                        .build());
    }

    // Check if bucket name exists
    @SneakyThrows
    public boolean bucketExists(String bucketName) {

        LOGGER.info("MinioUtil | bucketExists is called");

        boolean found =
                minioClient.bucketExists(
                        BucketExistsArgs.builder().
                                bucket(bucketName).
                                build());

        LOGGER.info("MinioUtil | bucketExists | found : " + found);

        if (found) {
            LOGGER.info("MinioUtil | bucketExists | message : " + bucketName + " exists");
        } else {
            LOGGER.info("MinioUtil | bucketExists | message : " + bucketName + " does not exist");
        }
        return found;
    }

    // Create bucket name
    @SneakyThrows
    public boolean makeBucket(String bucketName) {

        LOGGER.info("MinioUtil | makeBucket is called");

        boolean flag = bucketExists(bucketName);

        LOGGER.info("MinioUtil | makeBucket | flag : " + flag);

        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());

            return true;
        } else {
            return false;
        }
    }

    // List all buckets
    @SneakyThrows
    public List<Bucket> listBuckets() {
        LOGGER.info("MinioUtil | listBuckets is called");

        return minioClient.listBuckets();
    }

    // List all objects from the specified bucket
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {

        LOGGER.info("MinioUtil | listObjects is called");

        boolean flag = bucketExists(bucketName);

        LOGGER.info("MinioUtil | listObjects | flag : " + flag);

        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }


    // Delete object from the specified bucket
    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {

        LOGGER.info("MinioUtil | removeObject is called");

        boolean flag = bucketExists(bucketName);

        LOGGER.info("MinioUtil | removeObject | flag : " + flag);

        if (flag) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        }
        return false;
    }

    // Get file path from the specified bucket
    @SneakyThrows
    public String getObjectUrl(String bucketName, String objectName) {

        LOGGER.info("MinioUtil | getObjectUrl is called");
        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | getObjectUrl | flag : " + flag);

        String url = "";

        if (flag) {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(2, TimeUnit.MINUTES)
                            .build());
            LOGGER.info("MinioUtil | getObjectUrl | url : " + url);
        }
        return url;
    }

    // Get metadata of the object from the specified bucket
    @SneakyThrows
    public StatObjectResponse statObject(String bucketName, String objectName) {
        LOGGER.info("MinioUtil | statObject is called");

        boolean flag = bucketExists(bucketName);
        LOGGER.info("MinioUtil | statObject | flag : " + flag);
        if (flag) {
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectName).build());

            LOGGER.info("MinioUtil | statObject | stat : " + stat.toString());

            return stat;
        }
        return null;
    }
}
