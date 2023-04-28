package com.storage.service.AWS_S3.controller;

import com.storage.service.AWS_S3.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class S3Controller {

    @Autowired
    private FileServiceImpl service;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam ("file") MultipartFile file){
        return service.saveFile(file);
    }

    @GetMapping("/download/{fileName}")
    public byte[] downloadFile(@PathVariable ("fileName") String fileName){
       return service.downloadFile(fileName);
    }

    @DeleteMapping("{fileName}")
    public String deleteFile(@PathVariable ("fileName") String fileName){
        return service.deleteFile(fileName);
    }

    @GetMapping()
    public List<String> getAllFiles(){
        return service.listAllFiles();
    }
}
