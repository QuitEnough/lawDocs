package com.yana.filestorage.rest;

import com.yana.filestorage.api.client.FilesApi;
import com.yana.filestorage.exception.FileActionException;
import com.yana.filestorage.service.FileService;
import com.yana.filestorage.service.MinioService;
import com.yana.filestorage.service.TokenExtractor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
@Slf4j
public class FileRestController implements FilesApi {

    private final FileService fileService;
    private final MinioService minioService;
    private final TokenExtractor tokenExtractor;

    @Override
    public ResponseEntity<Void> uploadFile(String authorization,
                                           String name,
                                           MultipartFile file,
                                           Long directoryId) {
        var user = tokenExtractor.extractFromHeader(authorization);

        log.info("[FileController] Uploading file '{}' for user {}", name, user.getUserId());
        long fileId = fileService.save(name, directoryId, user.getUserId());
        UUID uuid = fileService.find(fileId);
        minioService.save(uuid, file);

        log.info("[Response] with saved data");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Resource> findFile(String authorization,
                                             Long fileId) {
        var user = tokenExtractor.extractFromHeader(authorization);
        fileService.ensureFileOwnership(fileId, user.getUserId());

        log.info("[Request] Finding file {} for user {}", fileId, user.getUserId());
        InputStream stream = fileService.download(fileId);
        Resource resource = new InputStreamResource(stream) {
            @Override
            public long contentLength() {
                return -1;
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Override
    public ResponseEntity<Void> renameFile(String authorization,
                                           Long fileId,
                                           String newName) {
        var user = tokenExtractor.extractFromHeader(authorization);
        fileService.ensureFileOwnership(fileId, user.getUserId());

        log.info("[Request] renaming file with id {} to '{} for user {}'", fileId, newName, user.getUserId());
        fileService.renameFile(fileId, newName);

        log.info("[Response] file renamed successfully");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteFile(String authorization,
                                           Long fileId) {
        var user = tokenExtractor.extractFromHeader(authorization);
        fileService.ensureFileOwnership(fileId, user.getUserId());

        log.info("[Request] Deleting file {} for user {}", fileId, user.getUserId());
        minioService.delete(fileService.find(fileId));
        fileService.delete(fileId);
        return ResponseEntity.ok().build();
    }

}
