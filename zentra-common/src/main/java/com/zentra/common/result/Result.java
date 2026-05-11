package com.zentra.common.result;

import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;

/**
 * Generic response wrapper for API responses
 *
 * @param <T> type of response data
 */
public class Result<T> {

    private Integer code; // status code
    private String msg;   // message
    private T data;       // response data

    private Result() {
    }

    /**
     * Success response with data
     */
    public static <T> Result<T> success(T data) {

        Result<T> result = new Result<>();
        result.setCode(ErrorCode.SUCCESS);
        result.setMsg(ErrorMessage.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * Success response without data
     */

    public static <T> Result<T> success() {

        Result<T> result = new Result<>();
        result.setCode(ErrorCode.SUCCESS);
        result.setMsg(ErrorMessage.SUCCESS);
        return result;
    }

    /**
     * Error response with custom message
     */
    public static <T> Result<T> error(String msg) {

        Result<T> result = new Result<>();
        result.setCode(ErrorCode.FAILURE);
        result.setMsg(msg);
        return result;

    }

    /**
     * Error response with custom code and message
     */
    public static <T> Result<T> error(Integer code, String msg) {

        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    // Getter and Setter
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