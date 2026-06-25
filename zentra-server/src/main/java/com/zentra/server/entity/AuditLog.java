package com.zentra.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLog {

    private Long id;

    private Long operatorId;

    private String operatorRole;

    private String operation;

    private String resourceType;

    private Long resourceId;

    private String requestUri;

    private String requestMethod;

    private Long executionTime;

    private Boolean success;

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
