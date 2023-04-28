package com.storage.service.program.amazons3.controller;

import com.storage.service.program.amazons3.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class StorageController {

    @Autowired
    private StorageService service;


    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam ("file") MultipartFile file){
        return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);

    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName){
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok().contentLength(data.length)
                .header("content-type", "application/octet-stream")
                .header("content-disposition", "attachment; filename=\""+fileName+"\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable("fileName") String fileName){
        return new ResponseEntity<>(service.deleteFile(fileName),HttpStatus.OK);
    }

}
