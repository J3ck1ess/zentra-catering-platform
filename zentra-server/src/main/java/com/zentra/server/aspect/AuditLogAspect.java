package com.zentra.server.aspect;

import com.zentra.common.context.AuthContext;
import com.zentra.server.annotation.AuditLog;
import com.zentra.server.audit.AuditRecord;
import com.zentra.server.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Audit log aspect
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    /**
     * Audit log service
     */
    private final AuditLogService auditLogService;

    /**
     * Record audit log around annotated methods
     */
    @Around("@annotation(auditLog)")
    public Object around(
            ProceedingJoinPoint joinPoint,
            AuditLog auditLog
    ) throws Throwable {

        long startTime = System.currentTimeMillis();

        boolean success = false;

        String errorMessage = null;

        Object result = null;

        try {

            result = joinPoint.proceed();

            success = true;

            return result;

        } catch (Exception ex) {

            errorMessage = ex.getMessage();

            throw ex;

        } finally {

            AuditRecord auditRecord = new AuditRecord();

            long executionTime = System.currentTimeMillis() - startTime;

            // Populate operator information
            auditRecord.setOperatorId(AuthContext.getCurrentUserId());
            auditRecord.setOperatorRole(AuthContext.getCurrentRole());

            // Populate audit metadata
            auditRecord.setOperation(auditLog.operation());
            auditRecord.setResourceType(auditLog.resourceType());

            // Populate execution result
            auditRecord.setExecutionTime(executionTime);
            auditRecord.setSuccess(success);
            auditRecord.setErrorMessage(
                    errorMessage == null ? null : errorMessage.substring(0,
                            Math.min(errorMessage.length(), 500))
            );

            // Resolve current HTTP request
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (requestAttributes != null) {

                HttpServletRequest request = requestAttributes.getRequest();

                auditRecord.setRequestUri(request.getRequestURI());
                auditRecord.setRequestMethod(request.getMethod());
            }

            try {

                auditLogService.record(auditRecord);

            } catch (Exception ex) {

                log.error(
                        "[AUDIT] Failed to persist audit log. operation={}",
                        auditRecord.getOperation(),
                        ex
                );
            }

            log.info(
                    "[AUDIT] Audit log recorded. operatorId={}, operation={}, success={}",
                    auditRecord.getOperatorId(),
                    auditRecord.getOperation(),
                    auditRecord.getSuccess()
            );
        }
    }
}