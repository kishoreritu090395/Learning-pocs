package com.spring.rest.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring.rest.demo.entity.Product;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {
	
	List<Product> findByName(String name);
	
	List<Product> findByNameContainingIgnoreCase(String name);
	
	//findByNameContaining(...)
	//findByNameIgnoreCase(...)
	
	//WHERE price BETWEEN ? AND ?
	List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
	
	//uses Java Entity
	@Query("SELECT p FROM Product p WHERE p.price >= :minPrice")
	List<Product> findProductsAbovePrice(
	        @Param("minPrice") Double minPrice
	);
	
	//Uses DB column
	@Query(
		    value = "SELECT * FROM PRODUCT WHERE PRICE >= :minPrice",
		    nativeQuery = true
		)
		List<Product> findProductsAbovePriceNative(
		        @Param("minPrice") Double minPrice
		);

}