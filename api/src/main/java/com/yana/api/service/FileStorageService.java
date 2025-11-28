package com.yana.api.service;

import com.yana.api.exception.FileOperationException;
import com.yana.api.feign.FileStorageServiceFeign;
import com.yana.lawdocs.api.filestorage.dto.DirectoryResponse;
import com.yana.lawdocs.api.filestorage.dto.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileStorageServiceFeign fileStorageFeign;
    private final TokenValidationService tokenValidationService;

    public DirectoryResponse createDirectory(String authHeader, String name, Long parentId) {
        try {
            log.info("Creating directory: {} with parentId: {}", name, parentId);
            var response = fileStorageFeign.createDirectory(authHeader, name, parentId);
            log.info("Directory created successfully: {}", name);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to create directory: {}", name, e);
            throw new FileOperationException("Failed to create directory: " + e.getMessage());
        }
    }

    public void deleteDirectory(String authHeader, Long directoryId) {
        try {
            log.info("Deleting directory with id: {}", directoryId);
            fileStorageFeign.deleteDirectory(authHeader, directoryId);
            log.info("Directory deleted successfully: {}", directoryId);
        } catch (Exception e) {
            log.error("Failed to delete directory: {}", directoryId, e);
            throw new FileOperationException("Failed to delete directory: " + e.getMessage());
        }
    }

    public Node getUserData(String authHeader, Long userId) {
        try {
            log.info("Fetching user data for userId: {}", userId);
            var response = fileStorageFeign.getAllDataForUser(authHeader, userId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch user data for userId: {}", userId, e);
            throw new FileOperationException("Failed to fetch user data: " + e.getMessage());
        }
    }

    public Node getDirectoryData(String authHeader, Long directoryId) {
        try {
            log.info("Fetching data for directory: {}", directoryId);
            var response = fileStorageFeign.getDataForDir(authHeader, directoryId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch directory data: {}", directoryId, e);
            throw new FileOperationException("Failed to fetch directory data: " + e.getMessage());
        }
    }

    public void renameDirectory(String authHeader, Long directoryId, String newName) {
        try {
            log.info("Renaming directory {} to {}", directoryId, newName);
            fileStorageFeign.renameDirectory(authHeader, directoryId, newName);
            log.info("Directory renamed successfully");
        } catch (Exception e) {
            log.error("Failed to rename directory: {}", directoryId, e);
            throw new FileOperationException("Failed to rename directory: " + e.getMessage());
        }
    }

    public void deleteFile(String authHeader, Long fileId) {
        try {
            log.info("Deleting file: {}", fileId);
            fileStorageFeign.deleteFile(authHeader, fileId);
            log.info("File deleted successfully: {}", fileId);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileId, e);
            throw new FileOperationException("Failed to delete file: " + e.getMessage());
        }
    }

    public Resource getFile(String authHeader, Long fileId) {
        try {
            log.info("Fetching file: {}", fileId);
            var response = fileStorageFeign.findFile(authHeader, fileId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch file: {}", fileId, e);
            throw new FileOperationException("Failed to fetch file: " + e.getMessage());
        }
    }

    public void renameFile(String authHeader, Long fileId, String newName) {
        try {
            log.info("Renaming file {} to {}", fileId, newName);
            fileStorageFeign.renameFile(authHeader, fileId, newName);
            log.info("File renamed successfully");
        } catch (Exception e) {
            log.error("Failed to rename file: {}", fileId, e);
            throw new FileOperationException("Failed to rename file: " + e.getMessage());
        }
    }

    public void uploadFile(String authHeader, String name, MultipartFile file, Long directoryId) {
        try {
            log.info("Uploading file: {} to directory: {}", name, directoryId);
            fileStorageFeign.uploadFile(authHeader, name, file, directoryId);
            log.info("File uploaded successfully: {}", name);
        } catch (Exception e) {
            log.error("Failed to upload file: {}", name, e);
            throw new FileOperationException("Failed to upload file: " + e.getMessage());
        }
    }

}
