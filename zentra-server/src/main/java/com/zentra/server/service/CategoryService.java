package com.zentra.server.service;

import com.zentra.server.entity.Category;

import java.util.List;

/**
 * Service interface for category business logic
 */
public interface CategoryService {
    void create (Category category);

    List<Category> list();

    void delete(Long id);
}
