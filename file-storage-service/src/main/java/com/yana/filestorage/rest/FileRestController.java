package com.yana.filestorage.rest;

import com.yana.filestorage.exception.FileActionException;
import com.yana.filestorage.service.FileService;
import com.yana.filestorage.service.MinioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
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

    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam String name,
                                           @RequestParam @NotNull MultipartFile file,
                                           @RequestParam(name = "directory_id", required = false) Long directoryId) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        var user = (UserDetailsImpl) authentication.getPrincipal();

        log.info("[FileController] Request to services for saving user with name {} and the file {}", name, file);
//        long fileId = fileService.save(name, directoryId, user.getId());
        long fileId = fileService.save(name, directoryId);
        UUID uuid = fileService.find(fileId);
        minioService.save(uuid, file);

        log.info("[Response] with saved data");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find")
    public void findFile(@RequestParam Long fileId, HttpServletResponse response) {
        log.info("[RequestParams] finding the file with id {}", fileId);
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
        log.info("[RequestParams] deleting the file with id {}", fileId);
        minioService.delete(fileService.find(fileId));
        fileService.delete(fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/files/{fileId}/owner/{userId}")
    public Boolean isFileOwner(@PathVariable Long fileId, @PathVariable Long userId) {
        // Логика проверки владельца файла
        return fileService.isFileOwner(fileId, userId);
    }

}
