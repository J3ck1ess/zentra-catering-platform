package com.zentra.server.service;

import com.zentra.server.audit.AuditRecord;

/**
 * Service interface for audit log logic
 */
public interface AuditLogService {

    /**
     * Record audit log
     */
    void record(AuditRecord record);
}
