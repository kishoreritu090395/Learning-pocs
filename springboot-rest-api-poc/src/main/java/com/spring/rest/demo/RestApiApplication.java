package com.spring.rest.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.spring.rest.demo.entity.Category;
import com.spring.rest.demo.entity.Product;
import com.spring.rest.demo.repository.CategoryRepository;
import com.spring.rest.demo.repository.ProductRepository;
@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    } 
    
    @Bean
    CommandLineRunner loadData(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        return args -> {

            Category electronics =
                    categoryRepository.save(
                            new Category("Electronics")
                    );

            Category accessories =
                    categoryRepository.save(
                            new Category("Accessories")
                    );

            Product laptop =
                    new Product(
                            "Laptop",
                            1200.00,
                            "Gaming Laptop"
                    );

            laptop.setCategory(electronics);

            Product phone =
                    new Product(
                            "Phone",
                            800.00,
                            "Smart Phone"
                    );

            phone.setCategory(electronics);

            Product mouse =
                    new Product(
                            "Mouse",
                            50.00,
                            "Wireless Mouse"
                    );

            mouse.setCategory(accessories);

            productRepository.save(laptop);
            productRepository.save(phone);
            productRepository.save(mouse);
        };
    }
}   