package com.zentra.server.annotation;

import java.lang.annotation.*;

/**
 * Audit log annotation for business operations
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /**
     * Business operation name
     */
    String operation();

    /**
     * Business resource type
     */
    String resourceType();
}
