package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for user response
 */
@Schema(description = "User response")
public class UserDTO {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "sultan_bek")
    private String username;

    @Schema(description = "User nickname", example = "Sultan")
    private String nickname;

    @Schema(description = "Phone number", example = "+77001234567")
    private String phone;

    @Schema(description = "User status (1 = Active, 0 = Disabled)", example = "1")
    private Integer status;

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
