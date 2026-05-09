package com.zentra.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration
 */
public class UserRegisterDTO {

    @NotBlank(message = "username cannot be blank")
    @Size(min = 4, max = 20, message = "username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, max = 32, message = "password must be between 6 and 32 characters")
    private String password;

    @Size(max = 50, message = "nickname must be less than 50 characters")
    private String nickname;

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
