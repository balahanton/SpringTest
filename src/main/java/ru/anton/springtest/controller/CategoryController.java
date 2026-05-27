package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.CategoriesApi;
import ru.anton.springtest.dto.CategoryRequest;
import ru.anton.springtest.dto.CategoryResponse;
import ru.anton.springtest.model.Category;
import ru.anton.springtest.service.CategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoriesApi {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<CategoryResponse> createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());

        Category saved = categoryService.create(category);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(saved.getId());
        categoryResponse.setName(saved.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    @Override
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categoryResponseList = categoryService.findAll().stream()
                .map(category -> {
                    CategoryResponse categoryResponse = new CategoryResponse();
                    categoryResponse.setId(category.getId());
                    categoryResponse.setName(category.getName());
                    return categoryResponse;
                })
                .toList();
        return ResponseEntity.ok(categoryResponseList);
    }

    @Override
    public ResponseEntity<CategoryResponse> getCategoryById(UUID uuid) {
        Category category = categoryService.findById(uuid);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        return ResponseEntity.ok(categoryResponse);
    }

    @Override
    public ResponseEntity<CategoryResponse> updateCategory(UUID uuid, CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());

        Category updated = categoryService.update(uuid, category);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(updated.getId());
        categoryResponse.setName(updated.getName());
        return ResponseEntity.ok(categoryResponse);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(UUID uuid) {
        categoryService.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
