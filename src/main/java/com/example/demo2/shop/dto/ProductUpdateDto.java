package com.example.demo2.shop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDto {
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
}
