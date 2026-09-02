package com.spring.cache.demo.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.spring.cache.demo.model.Product;

@Repository
public class InMemoryProductRepository {

    private final Map<Integer, Product> productStore =
            new ConcurrentHashMap<Integer, Product>();

    public InMemoryProductRepository() {

        productStore.put(1,
                new Product(1, "Laptop", 1200.00));

        productStore.put(2,
                new Product(2, "Phone", 800.00));

        productStore.put(3,
                new Product(3, "Tablet", 500.00));
    }

    public Product findById(Integer id) {
        return productStore.get(id);
    }

    public Product save(Product product) {
        productStore.put(product.getId(), product);
        return product;
    }

    public void deleteById(Integer id) {
        productStore.remove(id);
    }
}