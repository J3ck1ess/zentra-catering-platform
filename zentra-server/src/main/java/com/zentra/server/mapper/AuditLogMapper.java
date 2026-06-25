package com.zentra.server.mapper;

import com.zentra.server.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {

    /**
     * Insert audit log
     */
    int insert(AuditLog auditLog);
}
