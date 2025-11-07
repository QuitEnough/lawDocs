package com.yana.filestorage.dto;

import lombok.Data;

@Data
public class UserInfo {
    private Long userId;
    private String email;
    private String role;
}
