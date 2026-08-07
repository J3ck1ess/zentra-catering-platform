package com.zentra.common.constant;

/**
 * Permission constants for RBAC authorization
 */
public final class PermissionConstants {

    /**
     * Employee management permissions
     */
    public static final String EMPLOYEE_VIEW = "employee:view";
    public static final String EMPLOYEE_CREATE = "employee:create";
    public static final String EMPLOYEE_UPDATE = "employee:update";
    public static final String EMPLOYEE_DELETE = "employee:delete";

    /**
     * User management permissions
     */
    public static final String USER_VIEW = "user:view";
    public static final String USER_UPDATE = "user:update";

    /**
     * Category management permissions
     */
    public static final String CATEGORY_VIEW = "category:view";
    public static final String CATEGORY_CREATE = "category:create";
    public static final String CATEGORY_UPDATE = "category:update";
    public static final String CATEGORY_DELETE = "category:delete";

    /**
     * Dish management permissions
     */
    public static final String DISH_VIEW = "dish:view";
    public static final String DISH_CREATE = "dish:create";
    public static final String DISH_UPDATE = "dish:update";
    public static final String DISH_DELETE = "dish:delete";

    /**
     * Order management permissions
     */
    public static final String ORDER_VIEW = "order:view";
    public static final String ORDER_UPDATE = "order:update";

    /**
     * Dashboard permissions
     */
    public static final String DASHBOARD_VIEW = "dashboard:view";

    private PermissionConstants() {
    }
}
