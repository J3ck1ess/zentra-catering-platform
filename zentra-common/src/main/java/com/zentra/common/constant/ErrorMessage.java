package com.zentra.common.constant;

/**
 * Global business error messages
 */
public class ErrorMessage {

    /**
     * Common messages
     */
    public static final String SUCCESS = "success";

    /**
     * Authentication messages
     */
    public static final String TOKEN_INVALID = "Invalid token";

    public static final String TOKEN_EXPIRED = "Token expired";

    public static final String NO_PERMISSION = "No permission to access this API";

    /**
     * Employee messages
     */
    public static final String EMPLOYEE_NOT_FOUND = "Employee not found";

    public static final String EMPLOYEE_USERNAME_OR_PASSWORD_ERROR = "Username or password incorrect";

    public static final String EMPLOYEE_DISABLED = "Employee account disabled";

    public static final String EMPLOYEE_USERNAME_ALREADY_EXISTS = "Employee username already exists";

    public static final String EMPLOYEE_CREATE_FAILED = "Failed to create employee";

    public static final String EMPLOYEE_UPDATE_FAILED = "Failed to update employee";

    public static final String EMPLOYEE_DELETE_FAILED = "Failed to delete employee";

    public static final String EMPLOYEE_STATUS_INVALID = "Invalid employee status";

    public static final String INVALID_EMPLOYEE_ROLE = "Invalid employee role";

    /**
     * User messages
     */
    public static final String USER_NOT_FOUND = "User not found";

    public static final String USERNAME_OR_PASSWORD_ERROR = "Username or password incorrect";

    public static final String USER_DISABLED = "User account disabled";

    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";

    public static final String USER_REGISTER_FAILED = "Failed to register user";

    public static final String USER_STATUS_UPDATE_FAILED = "Failed to update user status";

    public static final String USER_STATUS_INVALID = "Invalid user status";

    /**
     * Category messages
     */
    public static final String CATEGORY_NOT_FOUND = "Category not found";

    public static final String CATEGORY_HAS_DISHES = "Category cannot be deleted because it has dishes";

    public static final String CATEGORY_CREATE_FAILED = "Failed to create category";

    public static final String CATEGORY_UPDATE_FAILED = "Failed to update category";

    public static final String CATEGORY_DELETE_FAILED = "Failed to delete category";

    public static final String CATEGORY_TYPE_INVALID = "Invalid category type";

    public static final String CATEGORY_STATUS_INVALID = "Invalid category status";

    /**
     * Dish messages
     */
    public static final String DISH_NOT_FOUND = "Dish not found";

    public static final String DISH_CREATE_FAILED = "Failed to create dish";

    public static final String DISH_UPDATE_FAILED = "Failed to update dish";

    public static final String DISH_DELETE_FAILED = "Failed to delete dish";

    public static final String DISH_STATUS_INVALID = "Invalid dish status";

    public static final String DISH_DISABLED = "Dish is disabled";

    /**
     * Order messages
     */
    public static final String ORDER_NOT_FOUND = "Order not found";

    public static final String ORDER_CREATE_FAILED = "Failed to create order";

    public static final String ORDER_UPDATE_FAILED = "Failed to update order";

    public static final String ORDER_STATUS_INVALID = "Invalid order status";

    public static final String ORDER_STATUS_TRANSITION_INVALID = "Invalid order status transition";

    public static final String ORDER_ITEMS_EMPTY = "Order items cannot be empty";

    public static final String ORDER_ITEM_CREATE_FAILED = "Failed to create order item";

    private ErrorMessage() {
    }
}
