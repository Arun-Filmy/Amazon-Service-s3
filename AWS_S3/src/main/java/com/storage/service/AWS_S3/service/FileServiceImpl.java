package com.storage.service.AWS_S3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Value("${bucketName}")
    private String bucketName;

    private final AmazonS3 s3;

    public FileServiceImpl(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        try {
            File file1 = convertMultiPartFileToFile(file);
            PutObjectResult putObjectResult = s3.putObject(bucketName, originalFilename, file1);
            return putObjectResult.getContentMd5();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) {

        S3Object object = s3.getObject(bucketName, fileName);
        S3ObjectInputStream objectContent = object.getObjectContent();
        try {
            byte[] byteArray = IOUtils.toByteArray(objectContent);
            return byteArray;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteFile(String fileName) {

        s3.deleteObject(bucketName, fileName);
        return "File Deleted";
    }

    @Override
    public List<String> listAllFiles() {

        ListObjectsV2Result listObjectsV2Result = s3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());

    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException{
        File converFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(converFile);
        fos.write(file.getBytes());
        fos.close();
        return converFile;
    }
}
