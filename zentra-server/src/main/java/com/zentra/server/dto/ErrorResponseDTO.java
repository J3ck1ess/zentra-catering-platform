package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for error response
 */
@Schema(description = "Error response")
public class ErrorResponseDTO {

    @Schema(description = "Business error code", example = "40001")
    private Integer code;

    @Schema(description = "Error message", example = "Invalid token")
    private String msg;

    // Getter and setter
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
}
