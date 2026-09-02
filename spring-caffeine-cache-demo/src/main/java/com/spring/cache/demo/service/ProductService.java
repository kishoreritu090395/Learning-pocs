package com.spring.cache.demo.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.spring.cache.demo.model.Product;
import com.spring.cache.demo.repository.InMemoryProductRepository;

@Service
public class ProductService {

    private final InMemoryProductRepository repository;


    public ProductService(
            InMemoryProductRepository repository) {

        this.repository = repository;
    }

    //Cache name = products
    //Cache key  = product id
    @Cacheable(value = "products", key = "#id")
    public Product getProduct(Integer id) {

        System.out.println(
                "CACHE MISS - Fetching product from data store: " + id
        );

        return repository.findById(id);
    }
    
    @CachePut(value = "products", key = "#id")
    public Product updateProduct(
            Integer id,
            Product updatedProduct) {

        updatedProduct.setId(id);

        System.out.println(
                "Updating product and cache: " + id
        );

        return repository.save(updatedProduct);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Integer id) {

        System.out.println(
                "Deleting product and evicting cache: " + id
        );

        repository.deleteById(id);
    }
}