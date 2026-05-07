package com.zentra.server.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating employee
 */
public class EmployeeUpdateDTO {

    @NotNull(message = "employee id cannot be null")
    private Long id;

    private String username;

    private String name;

    private String role;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
