package com.spring.rest.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.rest.demo.entity.Category;

@Repository
public interface CategoryRepository
        extends JpaRepository<Category, Long> {

}