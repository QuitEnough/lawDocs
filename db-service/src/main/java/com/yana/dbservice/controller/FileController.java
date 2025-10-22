package com.yana.dbservice.controller;

import com.yana.dbservice.service.FileService;
import com.yana.dbservice.service.MinioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    private final MinioService minioService;

    /*@PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam String name,
                                           @RequestParam @NotNull MultipartFile file,
                                           @RequestParam(name = "directory_id", required = false) Long directoryId) {

    }*/

}
