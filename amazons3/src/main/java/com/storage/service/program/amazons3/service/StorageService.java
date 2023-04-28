package com.storage.service.program.amazons3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class StorageService {

    @Autowired
    private AmazonS3 s3;

    @Value("${application.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file){
        File file1 = convertMultipartFiletoFile(file);
        String fileName= System.currentTimeMillis()+"_"+file.getOriginalFilename();
        s3.putObject(bucketName, fileName, file1);
        file1.delete();
        return "File uploaded : "+ fileName;
    }

    public byte[] downloadFile(String fileName){
        S3Object object = s3.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteFile(String fileName){
        s3.deleteObject(bucketName, fileName);
        return "File is removed: " + fileName;
    }


    private File convertMultipartFiletoFile(MultipartFile file){
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
