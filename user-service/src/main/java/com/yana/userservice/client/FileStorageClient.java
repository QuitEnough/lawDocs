package com.yana.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "file-storage-service", url = "${file-storage.url:http://db-service:8082}")
public interface FileStorageClient {

    @GetMapping("/files/{fileId}/owner/{userId}")
    Boolean isFileOwner(@PathVariable Long fileId, @PathVariable Long userId);

}
