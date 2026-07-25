package com.example.demo2.shop.service;

import com.example.demo2.shop.dto.CategoryUpdateDto;
import com.example.demo2.shop.entity.Category;
import com.example.demo2.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категоия не найдена"));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryUpdateDto dto) {
        Category category = getCategoryById(id);
        
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
