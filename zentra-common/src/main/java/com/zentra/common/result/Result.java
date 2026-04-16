package com.zentra.common.result;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public Result() {}

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // Return on success
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    // Return on failure
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    // getter & setter
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}