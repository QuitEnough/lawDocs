package com.yana.api.feign;

import com.yana.lawdocs.api.filestorage.dto.DirectoryResponse;
import com.yana.lawdocs.api.filestorage.dto.Node;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-storage-service", url = "${file-storage-service.url}")
public interface FileStorageServiceFeign {

    @PostMapping("/create-directory")
    ResponseEntity<DirectoryResponse> createDirectory (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("name") String name,
            @RequestParam(value = "parentId", required = false) Long parentId
    );

    @DeleteMapping("/delete-directory")
    ResponseEntity<Void> deleteDirectory (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "id") Long directoryId
    );

    @GetMapping("/directories/user")
    ResponseEntity<Node> getAllDataForUser (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "id") Long userId
    );

    @GetMapping("/directories")
    ResponseEntity<Node> getDataForDir (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "id") Long directoryId
    );

    @PutMapping("/rename-directory")
    ResponseEntity<Void> renameDirectory (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long directoryId,
            @RequestParam String newName
    );

    @DeleteMapping("/delete-file")
    ResponseEntity<Void> deleteFile(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "id") Long fileId
    );

    @GetMapping("/find-file")
    ResponseEntity<Resource> findFile (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long fileId
    );

    @PutMapping("/rename-file")
    ResponseEntity<Void> renameFile (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long fileId,
            @RequestParam String newName
    );

    @PostMapping("/upload-file")
    ResponseEntity<Void> uploadFile (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String name,
            @RequestPart MultipartFile file,
            @RequestParam(value = "directory_id",required = false) Long directoryId
    );

}
