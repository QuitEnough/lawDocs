package com.yana.dbservice.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int statusCode,
        String message
) {}
