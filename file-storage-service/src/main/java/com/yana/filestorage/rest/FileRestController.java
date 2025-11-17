package com.yana.filestorage.rest;

import com.yana.filestorage.exception.FileActionException;
import com.yana.filestorage.service.FileService;
import com.yana.filestorage.service.MinioService;
import com.yana.filestorage.service.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final TokenExtractor tokenExtractor;

    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam String name,
                                           @RequestParam @NotNull MultipartFile file,
                                           @RequestParam(name = "directory_id", required = false) Long directoryId,
                                           HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);

        log.info("[FileController] Uploading file '{}' for user {}", name, user.userId());
        long fileId = fileService.save(name, directoryId, user.userId());
        UUID uuid = fileService.find(fileId);
        minioService.save(uuid, file);

        log.info("[Response] with saved data");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find")
    public void findFile(@RequestParam Long fileId,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        var user = tokenExtractor.extractFromRequest(request);
        fileService.ensureFileOwnership(fileId, user.userId());

        log.info("[Request] Finding file {} for user {}", fileId, user.userId());
        try (InputStream stream = fileService.download(fileId)) {
            response.setHeader("Content-Disposition", "attachment");
            response.setStatus(HttpServletResponse.SC_OK);
            FileCopyUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            throw new FileActionException(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestParam("id") Long fileId,
                                           HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);
        fileService.ensureFileOwnership(fileId, user.userId());

        log.info("[Request] Deleting file {} for user {}", fileId, user.userId());
        minioService.delete(fileService.find(fileId));
        fileService.delete(fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/rename")
    public ResponseEntity<Void> renameFile(@RequestParam Long fileId,
                                           @RequestParam String newName,
                                           HttpServletRequest request) {
        var user = tokenExtractor.extractFromRequest(request);
        fileService.ensureFileOwnership(fileId, user.userId());

        log.info("[Request] renaming file with id {} to '{} for user {}'", fileId, newName, user.userId());
        fileService.renameFile(fileId, newName);

        log.info("[Response] file renamed successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
