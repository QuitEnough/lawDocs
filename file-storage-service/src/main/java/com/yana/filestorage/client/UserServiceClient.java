package com.yana.filestorage.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url:http://user-service:8084}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}/files/{fileId}/owner")
    Boolean isFileOwner(@PathVariable Long userId, @PathVariable Long fileId);

    @GetMapping("/users/{userId}")
    UserResponse getUserById(@PathVariable Long userId, @RequestHeader("Authorization") String token);

    @GetMapping("/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable String email, @RequestHeader("Authorization") String token);

}
