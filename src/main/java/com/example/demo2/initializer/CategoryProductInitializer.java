package com.example.demo2.initializer;

import com.example.demo2.entity.Category;
import com.example.demo2.entity.Product;
import com.example.demo2.repository.CategoryRepository;
import com.example.demo2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CategoryProductInitializer {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Bean
    @Order(2)
    public CommandLineRunner createCategoriesAndProducts() {
        return args -> {
            if (categoryRepository.count() > 0) {
                return;
            }

            Category phones = new Category();
            phones.setName("Телефоны");
            categoryRepository.save(phones);

            Category laptops = new Category();
            laptops.setName("Ноутбуки");
            categoryRepository.save(laptops);

            productRepository.saveAll(List.of(
                    createProduct("iPhone 15", "Apple", "Смартфон Apple", new BigDecimal("999.99"), 10, phones),
                    createProduct("Samsung Galaxy S24", "Samsung", "Смартфон Samsung", new BigDecimal("799.99"), 15, phones),
                    createProduct("MacBook Pro", "Apple", "Ноутбук Apple", new BigDecimal("1999.99"), 5, laptops),
                    createProduct("Lenovo ThinkPad", "Lenovo", "Ноутбук Lenovo", new BigDecimal("1299.99"), 8, laptops)
            ));

            System.out.println("Тестовые данные загружены: категории и продукты");
        };
    }

    private Product createProduct(String name, String brand, String description, BigDecimal price, Integer stock, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setBrand(brand);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        return product;
    }
}