package com.zentra.common.result;

import java.io.Serializable;

/**
 * Generic response wrapper for API responses
 *
 * @param <T> type of response data
 */
public class Result<T> {

    private Integer code; // status code (200 success, 400/401/500 error)
    private String msg;   // message
    private T data;       // response data

    /**
     * Success response with data
     *
     * @param data response data
     * @return Result object
     */
    public static <T> Result<T> success(T data) {

        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    /**
     * Success response without data
     *
     * @return Result object
     */

    public static <T> Result<T> success() {

        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        return result;
    }

    /**
     * Error response with custom message
     *
     * @param msg error message
     * @return Result object
     */
    public static <T> Result<T> error(String msg) {

        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;

    }

    /**
     * Error response with custom code and message
     *
     * @param code status code
     * @param msg  error message
     * @return Result object
     */
    public static <T> Result<T> error(Integer code, String msg) {

        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    // ===== Getter and Setter =====
    public Integer getCode() {

        return code;
    }

    public void setCode(Integer code) {

        this.code = code;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }

    public T getData() {

        return data;
    }

    public void setData(T data) {

        this.data = data;
    }
}