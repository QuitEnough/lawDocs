package com.yana.filestorage.rest;

import com.yana.filestorage.entity.UserDetailsImpl;
import com.yana.filestorage.exception.FileActionException;
import com.yana.filestorage.service.CustomUserDetailsService;
import com.yana.filestorage.service.FileService;
import com.yana.filestorage.service.MinioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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
public class FileRestController {

    private final FileService fileService;
    private final MinioService minioService;
    private final CustomUserDetailsService customUserDetailsService;

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam String name,
                                           @RequestParam @NotNull MultipartFile file,
                                           @RequestParam(name = "directory_id", required = false) Long directoryId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (UserDetailsImpl) authentication.getPrincipal();

        log.info("[FileController] Request to services for saving user with name {} and the file {}", name, file);
        long fileId = fileService.save(name, directoryId, user.getId());
        UUID uuid = fileService.find(fileId);
        minioService.save(uuid, file);

        log.info("[Response] with saved data");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@UserAccessor.canUserAccessResource('file', #fileId)")
    @GetMapping("/find")
    public void findFile(@RequestParam Long fileId,
                         HttpServletResponse response,
                         @RequestHeader("Authorization") String authToken) {
        log.info("[RequestParams] finding the file with id {}", fileId);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (UserDetailsImpl) authentication.getPrincipal();

        // Проверяем, является ли пользователь владельцем файла
        boolean isOwner = customUserDetailsService.isFileOwner(user.getId(), fileId, authToken);
        if (!isOwner) {
            throw new FileActionException("Access denied");
        }

        try (InputStream stream = fileService.download(fileId)) {
            response.setHeader("Content-Disposition", "attachment");
            response.setStatus(HttpServletResponse.SC_OK);
            FileCopyUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            throw new FileActionException(e.getMessage());
        }
    }

    @PreAuthorize("@UserAccessor.canUserAccessResource('file', #fileId)")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestParam("id") Long fileId,
                                           @RequestHeader("Authorization") String authToken) {
        log.info("[RequestParams] deleting the file with id {}", fileId);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (UserDetailsImpl) authentication.getPrincipal();

        // Проверяем, является ли пользователь владельцем файла
        boolean isOwner = customUserDetailsService.isFileOwner(user.getId(), fileId, authToken);
        if (!isOwner) {
            throw new FileActionException("Access denied");
        }

        minioService.delete(fileService.find(fileId));
        fileService.delete(fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@UserAccessor.canUserAccessResource('file', #fileId)")
    @PutMapping("/rename")
    public ResponseEntity<Void> renameFile(@RequestParam Long fileId,
                                           @RequestParam String newName) {
        log.info("[Request] renaming file with id {} to {}", fileId, newName);
        fileService.renameFile(fileId, newName);

        log.info("[Response] file renamed successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
