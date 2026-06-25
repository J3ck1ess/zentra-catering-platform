package com.zentra.server.service.impl;

import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.audit.AuditRecord;
import com.zentra.server.entity.AuditLog;
import com.zentra.server.mapper.AuditLogMapper;
import com.zentra.server.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Audit log service implementation
 */
@Service
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    public AuditLogServiceImpl(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    /**
     * Persist audit log
     */
    @Override
    public void record(AuditRecord auditRecord) {

        log.info(
                "[AUDIT] Audit log persistence started. operation={}",
                auditRecord.getOperation()
        );

        // Convert runtime record to persistence entity
        AuditLog auditLog = new AuditLog();
        BeanUtils.copyProperties(auditRecord, auditLog);

        // Persist audit log into database
        int rows = auditLogMapper.insert(auditLog);

        AssertUtil.checkRows(
                rows,
                ErrorCode.AUDIT_LOG_CREATE_FAILED,
                ErrorMessage.AUDIT_LOG_CREATE_FAILED
        );

        log.info(
                "[AUDIT] Audit log persisted successfully. operatorId={}, operation={}, success={}",
                auditLog.getOperatorId(),
                auditLog.getOperation(),
                auditLog.getSuccess()
        );
    }
}
