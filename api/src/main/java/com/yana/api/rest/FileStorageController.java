package com.yana.api.rest;

import com.yana.api.service.FileStorageService;
import com.yana.lawdocs.api.filestorage.dto.DirectoryResponse;
import com.yana.lawdocs.api.filestorage.dto.Node;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/files/**") //если убирать маппинг то убрать из WebConfig path
@RequiredArgsConstructor
@Tag(name = "File Storage", description = "API для работы с файлами и директориями")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @PostMapping("/directories")
    @Operation(summary = "Создание директории", description = "Создает новую директорию")
    public ResponseEntity<DirectoryResponse> createDirectory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String name,
            @RequestParam(required = false) Long parentId) {

        DirectoryResponse response = fileStorageService.createDirectory(authHeader, name, parentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/directories/{directoryId}")
    @Operation(summary = "Удаление директории", description = "Удаляет директорию по ID")
    public ResponseEntity<Void> deleteDirectory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long directoryId) {

        fileStorageService.deleteDirectory(authHeader, directoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получение данных пользователя", description = "Возвращает все файлы и директории пользователя")
    public ResponseEntity<Node> getUserData(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long userId) {

        Node userData = fileStorageService.getUserData(authHeader, userId);
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/directories/{directoryId}")
    @Operation(summary = "Получение данных директории", description = "Возвращает содержимое директории")
    public ResponseEntity<Node> getDirectoryData(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long directoryId) {

        Node directoryData = fileStorageService.getDirectoryData(authHeader, directoryId);
        return ResponseEntity.ok(directoryData);
    }

    @PutMapping("/directories/{directoryId}/rename")
    @Operation(summary = "Переименование директории", description = "Изменяет имя директории")
    public ResponseEntity<Void> renameDirectory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long directoryId,
            @RequestParam String newName) {

        fileStorageService.renameDirectory(authHeader, directoryId, newName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/files/{fileId}")
    @Operation(summary = "Удаление файла", description = "Удаляет файл по ID")
    public ResponseEntity<Void> deleteFile(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long fileId) {

        fileStorageService.deleteFile(authHeader, fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/files/{fileId}")
    @Operation(summary = "Получение файла", description = "Возвращает файл по ID")
    public ResponseEntity<Resource> getFile(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long fileId) {

        Resource file = fileStorageService.getFile(authHeader, fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PutMapping("/files/{fileId}/rename")
    @Operation(summary = "Переименование файла", description = "Изменяет имя файла")
    public ResponseEntity<Void> renameFile(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long fileId,
            @RequestParam String newName) {

        fileStorageService.renameFile(authHeader, fileId, newName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    @Operation(summary = "Загрузка файла", description = "Загружает файл в указанную директорию")
    public ResponseEntity<Void> uploadFile(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String name,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) Long directoryId) {

        fileStorageService.uploadFile(authHeader, name, file, directoryId);
        return ResponseEntity.ok().build();
    }

}
