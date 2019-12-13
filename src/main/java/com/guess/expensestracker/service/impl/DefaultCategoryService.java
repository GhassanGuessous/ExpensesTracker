package com.guess.expensestracker.service.impl;

import com.guess.expensestracker.entity.Category;
import com.guess.expensestracker.repository.CategoryRepository;
import com.guess.expensestracker.service.CategoryService;
import com.guess.expensestracker.web.rest.CategoryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultCategoryService implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(DefaultCategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findOne(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
