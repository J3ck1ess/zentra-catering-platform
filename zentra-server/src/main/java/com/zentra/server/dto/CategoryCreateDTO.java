package com.zentra.server.dto;

import jakarta.validation.constraints.NotNull;

public class CategoryCreateDTO {

    @NotNull(message = "name cannot be null")
    private String name;

    @NotNull(message = "type cannot be null")
    private Integer type;

    private Integer status;
    private Integer sort;

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
