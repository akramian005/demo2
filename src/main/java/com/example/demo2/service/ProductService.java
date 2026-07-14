package com.example.demo2.service;

import com.example.demo2.entity.Category;
import com.example.demo2.entity.Product;
import com.example.demo2.repository.CategoryRepository;
import com.example.demo2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAppProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт не найден"));
    }

    public Product createProduct(Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updated) {
        Product product = getProductById(id);
        product.setName(updated.getName());
        product.setBrand(updated.getBrand());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setStock(updated.getStock());
        if (updated.getCategory() != null) {
            Category category = categoryRepository.findById(updated.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}










