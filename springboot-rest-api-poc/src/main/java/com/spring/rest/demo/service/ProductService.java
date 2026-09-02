package com.spring.rest.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.rest.demo.dto.ProductRequest;
import com.spring.rest.demo.entity.Category;
import com.spring.rest.demo.entity.Product;
import com.spring.rest.demo.exception.CategoryNotFoundException;
import com.spring.rest.demo.exception.ProductNotFoundException;
import com.spring.rest.demo.repository.CategoryRepository;
import com.spring.rest.demo.repository.ProductRepository;

@Service
public class ProductService {
	
	private static final Logger logger =
	        LoggerFactory.getLogger(ProductService.class);

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {

		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	public Product createProduct(ProductRequest request) {
		
		logger.info(
	            "Creating product with name: {}",
	            request.getName()
	    );

		Category category = categoryRepository
		        .findById(request.getCategoryId())
		        .orElseThrow(() -> {

		            logger.warn(
		                    "Category not found with id: {}",
		                    request.getCategoryId()
		            );

		            return new CategoryNotFoundException(
		                    "Category not found with id: "
		                            + request.getCategoryId()
		            );
		        });

		Product product = new Product();

		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setDescription(request.getDescription());
		product.setCategory(category);

		Product savedProduct = productRepository.save(product);
		
		logger.info(
		        "Product created successfully with id: {}",
		        savedProduct.getId()
		);

		return savedProduct;
	}

	@Transactional(readOnly = true)
	public Product getProductById(Long id) {
		return productRepository.findById(id)
	            .orElseThrow(() -> {

	                logger.warn(
	                        "Product not found with id: {}",
	                        id
	                );

	                return new ProductNotFoundException(
	                        "Product not found with id: " + id
	                );
	            });
	}

	@Transactional(readOnly = true)
	public List<Product> getAllProducts() {
		logger.debug("Fetching all products");
		List<Product> products = productRepository.findAll();

		logger.debug(
		        "Found {} products",
		        products.size()
		);

		return products;
	}

	@Transactional(readOnly = true)
	public Page<Product> getAllProducts(int page, int size, String sortBy, String direction) {

		logger.debug(
		        "Fetching products - page: {}, size: {}, sortBy: {}, direction: {}",
		        page,
		        size,
		        sortBy,
		        direction
		);
		
		Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		return productRepository.findAll(pageable);
	}

	@Transactional
	public boolean deleteProduct(Long id) {

		logger.info(
	            "Deleting product with id: {}",
	            id
	    );
		
		if (!productRepository.existsById(id)) {
			return false;
		}

		productRepository.deleteById(id);
		
		logger.info(
		            "Product deleted successfully with id: {}",
		            id
		    );
		return true;
	}

	@Transactional
	public Product updateProduct(Long id, ProductRequest request) {
		
		logger.info(
	            "Updating product with id: {}",
	            id
	    );

		// Find the product
		Product existingProduct =
	            productRepository.findById(id)
	                    .orElseThrow(() -> {

	                        logger.warn(
	                                "Product not found with id: {}",
	                                id
	                        );

	                        return new ProductNotFoundException(
	                                "Product not found with id: " + id
	                        );
	                    });

		// Find the category sent in the request
		Category category = categoryRepository
		        .findById(request.getCategoryId())
		        .orElseThrow(() -> {

		            logger.warn(
		                    "Category not found with id: {}",
		                    request.getCategoryId()
		            );

		            return new CategoryNotFoundException(
		                    "Category not found with id: "
		                            + request.getCategoryId()
		            );
		        });

		// Update product details
		existingProduct.setName(request.getName());
		existingProduct.setPrice(request.getPrice());
		existingProduct.setDescription(request.getDescription());

		// Update product's category
		existingProduct.setCategory(category);

		logger.info(
	            "Product updated successfully with id: {}",
	            id
	    );
		
		// Save updated product
		return productRepository.save(existingProduct);
	}

	@Transactional(readOnly = true)
	public List<Product> getProductsByName(String name) {
		logger.debug(
		        "Searching products by exact name: {}",
		        name
		);
		return productRepository.findByName(name);
	}

	@Transactional(readOnly = true)
	public List<Product> searchProductsByName(String name) {
		logger.debug(
		        "Searching products containing name: {}",
		        name
		);
		return productRepository.findByNameContainingIgnoreCase(name);
	}

	@Transactional(readOnly = true)
	public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
		logger.debug(
		        "Searching products with price >= {}",
		        minPrice
		);
		return productRepository.findByPriceBetween(minPrice, maxPrice);
	}

	@Transactional(readOnly = true)
	public List<Product> getProductsAbovePrice(Double minPrice) {

		return productRepository.findProductsAbovePrice(minPrice);
	}

	@Transactional(readOnly = true)
	public List<Product> getProductsAbovePriceNative(Double minPrice) {

		return productRepository.findProductsAbovePriceNative(minPrice);
	}
}