package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Base response structure for API documentation
 */
@Schema(description = "Base API response")
public class BaseResponseDTO {

    @Schema(
            description = "Business response code",
            example = "1"
    )
    private Integer code;

    @Schema(
            description = "Response message",
            example = "Success"
    )
    private String msg;

    // Getters and setters
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
