package com.bandlab.assignment.service.clients.impl;

import com.bandlab.assignment.service.clients.StorageClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class S3StorageClient implements StorageClient {

    private static final String BUCKET_NAME = "test-bucket";
    private S3Client s3Client;

    @Override
    public String uploadFile(File file) throws IOException {

        String uuid = UUID.randomUUID().toString();
        String key = uuid + "-" + file.getName();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest, file.toPath());
        // return s3 object url
        return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(BUCKET_NAME).key(putObjectRequest.key()).build()).toExternalForm();

    }
}
