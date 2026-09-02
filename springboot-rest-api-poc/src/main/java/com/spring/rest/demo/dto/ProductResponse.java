package com.spring.rest.demo.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private Double price;
    private String description;
    private Long categoryId;
    private String categoryName;

    public ProductResponse(
            Long id,
            String name,
            Double price,
            String description,
            Long categoryId,
            String categoryName) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}