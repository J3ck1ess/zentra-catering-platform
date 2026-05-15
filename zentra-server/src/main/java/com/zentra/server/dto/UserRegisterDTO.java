package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration
 */
@Schema(description = "User registration request")
public class UserRegisterDTO {

    @Schema(description = "Username", example = "sultan_bek")
    @NotBlank(message = "username cannot be blank")
    @Size(min = 4, max = 20, message = "username must be between 4 and 20 characters")
    private String username;

    @Schema(description = "Password", example = "123456")
    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, max = 32, message = "password must be between 6 and 32 characters")
    private String password;

    @Schema(description = "User nickname", example = "Sultan")
    @Size(max = 50, message = "nickname must be less than 50 characters")
    private String nickname;

    @Schema(description = "Phone number", example = "+77001234567")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "invalid phone number format"
    )
    private String phone;

    // Getter and Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
