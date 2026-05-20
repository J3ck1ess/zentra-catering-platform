package com.zentra.server.annotation;

import java.lang.annotation.*;

/**
 * Annotation for RBAC permission control
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * Required permission
     */
    String value();
}
