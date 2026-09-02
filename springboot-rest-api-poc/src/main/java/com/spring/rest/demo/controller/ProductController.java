package com.spring.rest.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.rest.demo.dto.ProductRequest;
import com.spring.rest.demo.dto.ProductResponse;
import com.spring.rest.demo.entity.Product;
import com.spring.rest.demo.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@Tag(
		name = "Product API",
		description = "APIs for creating, retrieving, updating and deleting products"
		)
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@Operation(
			summary = "Create product",
			description = "Creates a new product under an existing category"
			)
	@ApiResponse(
			responseCode = "201",
			description = "Product created successfully"
			)
	@ApiResponse(
			responseCode = "400",
			description = "Invalid product request"
			)
	@ApiResponse(
			responseCode = "404",
			description = "Category not found"
			)
	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(
			@Valid @RequestBody ProductRequest request) {

		Product savedProduct =
				productService.createProduct(request);

		ProductResponse response =
				toProductResponse(savedProduct);

		return new ResponseEntity<ProductResponse>(
				response,
				HttpStatus.CREATED
				);
	}

	@Operation(
			summary = "Get all products",
			description = "Retrieves all available products with their category details"
			)
	@ApiResponse(
			responseCode = "200",
			description = "Products retrieved successfully"
			)
	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllProducts() {

		List<ProductResponse> response =
				productService.getAllProducts()
				.stream()
				.map(this::toProductResponse)
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	@Operation(
			summary = "Get product by ID",
			description = "Returns a product for the given product ID"
			)
	@ApiResponse(
			responseCode = "200",
			description = "Product found"
			)
	@ApiResponse(
			responseCode = "404",
			description = "Product not found"
			)
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(
			@PathVariable Long id) {

		Product product =
				productService.getProductById(id);

		return ResponseEntity.ok(toProductResponse(product));
	}

	@Operation(
			summary = "Get products with pagination",
			description = "Retrieves products using pagination and sorting"
			)
	@ApiResponse(
			responseCode = "200",
			description = "Products retrieved successfully"
			)
	@ApiResponse(
			responseCode = "400",
			description = "Invalid pagination or sorting parameters"
			)
	@GetMapping("/page")
	public ResponseEntity<Page<ProductResponse>> getAllProductsPaginated(

			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {

		Page<Product> products =
				productService.getAllProducts(
						page,
						size,
						sortBy,
						direction
						);

		Page<ProductResponse> response =
				products.map(this::toProductResponse);

		return ResponseEntity.ok(response);
	}

	@Operation(
			summary = "Update product",
			description = "Updates an existing product and its category"
			)
	@ApiResponse(
			responseCode = "200",
			description = "Product updated successfully"
			)
	@ApiResponse(
			responseCode = "400",
			description = "Invalid request"
			)
	@ApiResponse(
			responseCode = "404",
			description = "Product or category not found"
			)
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(
			@PathVariable Long id,
			@Valid @RequestBody ProductRequest request) {

		Product product =
				productService.updateProduct(id, request);

		if (product == null) {
			return ResponseEntity.notFound().build();
		}


		return ResponseEntity.ok(toProductResponse(product));
	}

	@Operation(
			summary = "Delete product",
			description = "Deletes a product using its ID"
			)
	@ApiResponse(
			responseCode = "204",
			description = "Product deleted successfully"
			)
	@ApiResponse(
			responseCode = "404",
			description = "Product not found"
			)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(
			@PathVariable Long id) {

		boolean deleted =
				productService.deleteProduct(id);

		if (!deleted) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Find products by exact name",
			description = "Retrieves products whose name exactly matches the provided name"
			)
	@ApiResponse(
			responseCode = "200",
			description = "Search completed successfully"
			)
	@ApiResponse(
			responseCode = "400",
			description = "Product name parameter is missing"
			)
	@GetMapping("/search")
	public ResponseEntity<List<ProductResponse>> getProductsByName(
											@RequestParam String name) {

		List<ProductResponse> response =
				productService.getProductsByName(name)
				.stream()
				.map(this::toProductResponse)
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	private ProductResponse toProductResponse(Product product) {

		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getDescription(),
				product.getCategory() != null
				? product.getCategory().getId()
						: null,
						product.getCategory() != null
						? product.getCategory().getName()
								: null
				);
	}


}