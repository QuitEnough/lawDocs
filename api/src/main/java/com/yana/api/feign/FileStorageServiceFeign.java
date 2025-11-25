package com.yana.api.feign;

import com.yana.lawdocs.api.filestorage.dto.DirectoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "file-storage-service", url = "${file-storage-service.url}")
public interface FileStorageServiceFeign {

    @PostMapping("/directories/create")
    ResponseEntity<DirectoryResponse> createDirectory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("name") String name,
            @RequestParam(value = "parentId", required = false) Long parentId
    );

}
