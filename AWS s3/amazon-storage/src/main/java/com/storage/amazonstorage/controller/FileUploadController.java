package com.storage.amazonstorage.controller;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.storage.amazonstorage.entity.FileUpload;
import com.storage.amazonstorage.repo.FileUploadDAO;
import com.storage.amazonstorage.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

@RestController
public class FileUploadController {
    @Autowired
    private StorageService s3Service;

    @Autowired
    private FileUploadDAO fileUploadDAO;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        String bucketName = "s3storageprogramfromspring";
        String folderName = "";

        // Check if the file extension is one of the allowed types
        if (Arrays.asList("png", "jpeg", "pdf", "docx", "xlsx").contains(extension)) {
            // Determine which folder to save the file in based on its extension
            switch (extension) {
                case "png":
                case "jpeg":
                    folderName = "images";
                    break;
                case "pdf":
                case "docx":
                case "xlsx":
                    folderName = "documents";
                    break;
            }

            // Upload the file to S3
            byte[] fileContent = file.getBytes();
            String s3Key = folderName + "/" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileContent.length);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Key, inputStream, metadata);
            s3Service.amazonS3.putObject(putObjectRequest);

            // Save the file record to MySQL database
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(fileName);
            fileUpload.setExtension(extension);
            fileUpload.setS3Key(s3Key);
            fileUploadDAO.save(fileUpload);

            return ResponseEntity.ok("File uploaded successfully");
        } else {
            return ResponseEntity.badRequest().body("Only PNG, JPEG, PDF, DOCX, and XLSX files are allowed");
        }
    }
}
