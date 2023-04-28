package com.storage.amazonstorage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class StorageService {

    @Autowired
    public AmazonS3 amazonS3;

    public String createBucket(String bucketName) {
        Bucket bucket = amazonS3.createBucket(bucketName);

        return bucket.getName();
    }

    public String createFolder(String bucketName, String folderName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            throw new RuntimeException("Bucket does not exist: " + bucketName);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                folderName + "/", emptyContent, metadata);

        amazonS3.putObject(putObjectRequest);

        return folderName;

    }
}