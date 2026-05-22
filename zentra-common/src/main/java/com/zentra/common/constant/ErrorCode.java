package com.zentra.common.constant;

/**
 * Global business error codes
 */
public class ErrorCode {

    /**
     * Success
     */
    public static final Integer SUCCESS = 1;

    /**
     * General failure
     */
    public static final Integer FAILURE = 0;

    /**
     * Common Request Errors
     */
    public static final Integer BAD_REQUEST = 40000;

    /**
     * Authentication Errors
     */
    public static final Integer TOKEN_INVALID = 40001;

    public static final Integer TOKEN_EXPIRED = 40002;

    public static final Integer NO_PERMISSION = 40003;

    /**
     * Employee Errors
     */
    public static final Integer EMPLOYEE_NOT_FOUND = 40101;

    public static final Integer EMPLOYEE_USERNAME_OR_PASSWORD_ERROR = 40102;

    public static final Integer EMPLOYEE_DISABLED = 40103;

    public static final Integer EMPLOYEE_USERNAME_ALREADY_EXISTS = 40104;

    public static final Integer EMPLOYEE_CREATE_FAILED = 40105;

    public static final Integer EMPLOYEE_UPDATE_FAILED = 40106;

    public static final Integer EMPLOYEE_DELETE_FAILED = 40107;

    public static final Integer EMPLOYEE_STATUS_INVALID = 40108;

    public static final Integer INVALID_EMPLOYEE_ROLE = 40109;

    /**
     * User Errors
     */
    public static final Integer USER_NOT_FOUND = 40201;

    public static final Integer USERNAME_OR_PASSWORD_ERROR = 40202;

    public static final Integer USER_DISABLED = 40203;

    public static final Integer USERNAME_ALREADY_EXISTS = 40204;

    public static final Integer USER_REGISTER_FAILED = 40205;

    public static final Integer USER_STATUS_UPDATE_FAILED = 40206;

    public static final Integer USER_STATUS_INVALID = 40207;

    /**
     * Category Errors
     */
    public static final Integer CATEGORY_NOT_FOUND = 50001;

    public static final Integer CATEGORY_HAS_DISHES = 50002;

    public static final Integer CATEGORY_CREATE_FAILED = 50003;

    public static final Integer CATEGORY_UPDATE_FAILED = 50004;

    public static final Integer CATEGORY_DELETE_FAILED = 50005;

    public static final Integer CATEGORY_TYPE_INVALID = 50006;

    public static final Integer CATEGORY_STATUS_INVALID = 50007;

    /**
     * Dish Errors
     */
    public static final Integer DISH_NOT_FOUND = 51001;

    public static final Integer DISH_CREATE_FAILED = 51002;

    public static final Integer DISH_UPDATE_FAILED = 51003;

    public static final Integer DISH_DELETE_FAILED = 51004;

    public static final Integer DISH_STATUS_INVALID = 51005;

    public static final Integer DISH_DISABLED = 51006;

    /**
     * Order Errors
     */
    public static final Integer ORDER_NOT_FOUND = 52001;

    public static final Integer ORDER_CREATE_FAILED = 52002;

    public static final Integer ORDER_UPDATE_FAILED = 52003;

    public static final Integer ORDER_STATUS_INVALID = 52004;

    public static final Integer ORDER_STATUS_TRANSITION_INVALID = 52005;

    public static final Integer ORDER_ITEMS_EMPTY = 52006;

    public static final Integer ORDER_ITEM_CREATE_FAILED = 52007;

    private ErrorCode() {
    }
}
