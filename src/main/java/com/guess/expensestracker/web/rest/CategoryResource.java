package com.guess.expensestracker.web.rest;

import com.guess.expensestracker.entity.Category;
import com.guess.expensestracker.service.CategoryService;
import com.guess.expensestracker.web.rest.errors.BadRequestAlertException;
import com.guess.expensestracker.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    private static final String ENTITY_NAME = "category";

    private CategoryService categoryService;

    public CategoryResource(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to save Category : {}", category);
        if(category.getId() != null) {
            throw new BadRequestAlertException("A new category cannot have already an Id!", ENTITY_NAME);
        }
        Category result = categoryService.save(category);
        return ResponseEntity.created(new URI("/api/categories/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @PutMapping("/categories")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        log.debug("REST request to update Category : {}", category);
        if(category.getId() == null) {
            throw new BadRequestAlertException("Invalid Id!", ENTITY_NAME);
        }
        Category result = categoryService.save(category);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        log.debug("REST request to get all categories");
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        log.debug("REST request to get Category : {}", id);
        Optional<Category> category = categoryService.findOne(id);
        if(category.isPresent()) {
            return ResponseEntity.ok().body(category.get());
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("REST request to delete Category : {}", id);
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
