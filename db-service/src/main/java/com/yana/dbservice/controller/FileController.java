package com.yana.dbservice.controller;

import com.yana.dbservice.exception.FileActionException;
import com.yana.dbservice.service.FileService;
import com.yana.dbservice.service.MinioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam String name,
                                           @RequestParam @NotNull MultipartFile file,
                                           @RequestParam(name = "directory_id", required = false) Long directoryId) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        var user = (UserDetailsImpl) authentication.getPrincipal();

        log.debug("[FileController] Request to services for saving user with name {} and the file {}", name, file);
        long fileId = fileService.save(name, directoryId, user.getId());
        UUID uuid = fileService.find(fileId);
        minioService.save(uuid, file);

        log.debug("[Response] with saved data");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find")
    public void findFile(@RequestParam Long fileId, HttpServletResponse response) {
        log.debug("[RequestParams] finding the file with id {}", fileId);
        try (InputStream stream = fileService.download(fileId)) {
            response.setHeader("Content-Disposition", "attachment");
            response.setStatus(HttpServletResponse.SC_OK);
            FileCopyUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            throw new FileActionException(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestParam("id") Long fileId) {
        log.debug("[RequestParams] deleting the file with id {}", fileId);
        minioService.delete(fileService.find(fileId));
        fileService.delete(fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
