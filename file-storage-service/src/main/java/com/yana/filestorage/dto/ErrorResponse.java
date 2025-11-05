package com.yana.filestorage.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int statusCode,
        String message
) { }
