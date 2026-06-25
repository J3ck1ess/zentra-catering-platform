package com.zentra.server.audit;

import lombok.Data;

/**
 * Runtime audit record
 */
@Data
public class AuditRecord {

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
}
