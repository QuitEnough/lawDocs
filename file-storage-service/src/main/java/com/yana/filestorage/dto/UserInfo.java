package com.yana.filestorage.dto;

public record UserInfo(
        Long userId,
        String email,
        String role
) {
}
