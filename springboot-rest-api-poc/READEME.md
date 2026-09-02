# Spring Boot REST API POC

A sample Spring Boot REST API project created to demonstrate commonly used concepts in:

- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- REST API development
- DTOs
- Validation
- Exception Handling
- JPA Relationships
- Pagination and Sorting
- JPQL
- Native SQL
- Transaction Management
- Swagger / OpenAPI
- Spring Boot Actuator
- SLF4J Logging
- Spring Profiles

The application manages **Products** and **Categories**, where multiple products can belong to one category.

---

# Technologies Used

- Java 8
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- H2 Database
- Bean Validation
- Spring Transaction Management
- Spring Boot Actuator
- SLF4J
- Swagger / OpenAPI
- Maven

---

# Architecture

The application follows a layered architecture:

```text
Client
(Postman / Swagger / Browser)
        |
        v
   Controller
        |
        v
     Service
        |
        v
   Repository
        |
        v
Spring Data JPA
        |
        v
    Hibernate
        |
        v
    Database
```

## Controller Layer

Responsible for:

- Receiving HTTP requests
- Request validation
- Calling the service layer
- Returning HTTP responses
- Converting entities to response DTOs

## Service Layer

Responsible for:

- Business logic
- Calling repositories
- Product and Category relationship handling
- Transaction management

## Repository Layer

Uses Spring Data JPA for database operations.

## DTO Layer

Uses request and response DTOs to separate the REST API contract from JPA entities.

## Exception Layer

Provides centralized exception handling using:

```java
@RestControllerAdvice
```

---

# Product and Category Relationship

The application demonstrates a bidirectional JPA relationship.

```text
Category
   |
   | One-to-Many
   |
   +------ Product
   +------ Product
   +------ Product
```

Product contains:

```java
@ManyToOne
@JoinColumn(name = "category_id")
private Category category;
```

Category contains:

```java
@OneToMany(mappedBy = "category")
private List<Product> products;
```

Therefore:

```text
One Category
     |
     +---- Many Products
```

DTOs are used for REST responses so the complete JPA entity graph is not exposed.

This also prevents recursive JSON serialization between:

```text
Product → Category → Products → Category → Products...
```

---

# REST CRUD Operations

The application supports:

- Create Product
- Get Product by ID
- Get All Products
- Update Product
- Delete Product

---

# Validation

Bean Validation is used to validate incoming API requests.

Examples include:

```java
@NotBlank
@NotNull
@Size
@Min
@DecimalMin
```

Controller requests are validated using:

```java
@Valid
```

Example:

```java
@PostMapping
public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody ProductRequest request) {
```

Invalid requests return:

```text
400 Bad Request
```

---

# Global Exception Handling

Centralized exception handling is implemented using:

```java
@RestControllerAdvice
@ExceptionHandler
```

Custom exceptions include:

```text
ProductNotFoundException
CategoryNotFoundException
```

The application also handles:

```text
MethodArgumentNotValidException
HttpMessageNotReadableException
Generic unexpected exceptions
```

This keeps exception-handling logic separate from controllers.

---

# Spring Data JPA Queries

The project demonstrates different ways of retrieving data using Spring Data JPA.

## Derived Query Methods

Examples:

```java
findByName(...)
```

```java
findByNameContainingIgnoreCase(...)
```

```java
findByPriceBetween(...)
```

Spring Data JPA generates the query automatically based on the method name.

---

# JPQL Query

The project demonstrates a custom JPQL query.

Example:

```java
@Query(
    "SELECT p FROM Product p WHERE p.price >= :minPrice"
)
List<Product> findProductsAbovePrice(
        @Param("minPrice") Double minPrice);
```

JPQL operates on:

```text
Java Entities
and
Java Entity Properties
```

For example:

```text
Product
price
```

rather than database table and column names.

---

# Native SQL Query

The project also demonstrates native SQL.

Example:

```java
@Query(
    value = "SELECT * FROM PRODUCT WHERE PRICE >= :minPrice",
    nativeQuery = true
)
List<Product> findProductsAbovePriceNative(
        @Param("minPrice") Double minPrice);
```

Native SQL operates directly on:

```text
Database Tables
and
Database Columns
```

---

# Pagination and Sorting

Pagination and sorting are implemented using:

```java
Page
Pageable
PageRequest
Sort
```

Example:

```text
GET /api/products/page?page=0&size=5&sortBy=price&direction=desc
```

This means:

```text
page      = 0
size      = 5
sortBy    = price
direction = descending
```

---

# Transaction Management

Transaction boundaries are defined in the service layer using:

```java
@Transactional
```

Write operations use:

```java
@Transactional
```

Read operations use:

```java
@Transactional(readOnly = true)
```

Examples:

```java
@Transactional
public Product createProduct(...) {
}
```

```java
@Transactional(readOnly = true)
public Product getProductById(...) {
}
```

Transactions help ensure database consistency.

If an unchecked exception occurs during a transaction, Spring normally rolls the transaction back.

---

# API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| GET | `/api/products/page` | Pagination and sorting |
| GET | `/api/products/search` | Exact name search |
| GET | `/api/products/search/contains` | Partial case-insensitive search |
| GET | `/api/products/price` | Search by price range |
| GET | `/api/products/above-price` | Search using JPQL |
| GET | `/api/products/above-price-native` | Search using native SQL |

---

# Sample API Requests

The following examples assume that the application is running at:

```text
http://localhost:8080
```

---

## 1. Create Product

### Endpoint

```http
POST /api/products
```

### Request Body

```json
{
  "name": "Keyboard",
  "price": 100,
  "description": "Mechanical Keyboard",
  "categoryId": 2
}
```

### Expected Status

```text
201 Created
```

### Example Response

```json
{
  "id": 4,
  "name": "Keyboard",
  "price": 100.0,
  "description": "Mechanical Keyboard",
  "categoryId": 2,
  "categoryName": "Accessories"
}
```

---

# 2. Get Product by ID

### Endpoint

```http
GET /api/products/1
```

### Expected Status

```text
200 OK
```

### Example Response

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 1200.0,
  "description": "Gaming Laptop",
  "categoryId": 1,
  "categoryName": "Electronics"
}
```

If the product does not exist:

```http
GET /api/products/999
```

Example:

```json
{
  "status": 404,
  "message": "Product not found with id: 999"
}
```

---

# 3. Get All Products

### Endpoint

```http
GET /api/products
```

### Example Response

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 1200.0,
    "description": "Gaming Laptop",
    "categoryId": 1,
    "categoryName": "Electronics"
  },
  {
    "id": 2,
    "name": "Phone",
    "price": 800.0,
    "description": "Smart Phone",
    "categoryId": 1,
    "categoryName": "Electronics"
  }
]
```

If there are no products:

```json
[]
```

The API still returns:

```text
200 OK
```

---

# 4. Update Product

### Endpoint

```http
PUT /api/products/1
```

### Request Body

```json
{
  "name": "Gaming Laptop Pro",
  "price": 1500,
  "description": "Updated Gaming Laptop",
  "categoryId": 1
}
```

### Example Response

```json
{
  "id": 1,
  "name": "Gaming Laptop Pro",
  "price": 1500.0,
  "description": "Updated Gaming Laptop",
  "categoryId": 1,
  "categoryName": "Electronics"
}
```

The category can also be changed by providing another valid:

```text
categoryId
```

---

# 5. Delete Product

### Endpoint

```http
DELETE /api/products/2
```

### Expected Status

```text
204 No Content
```

If the product does not exist:

```http
DELETE /api/products/999
```

Expected:

```text
404 Not Found
```

---

# 6. Pagination

### Endpoint

```http
GET /api/products/page?page=0&size=5
```

This retrieves:

```text
First page
Maximum 5 products
```

Page numbers start from:

```text
0
```

---

# 7. Pagination with Sorting

### Endpoint

```http
GET /api/products/page?page=0&size=5&sortBy=price&direction=desc
```

Parameters:

```text
page      = page number starting from 0
size      = number of products per page
sortBy    = Product property used for sorting
direction = asc or desc
```

Example:

```text
sortBy=price
direction=desc
```

returns products from highest price to lowest price.

---

# 8. Find Product by Exact Name

### Endpoint

```http
GET /api/products/search?name=Laptop
```

This endpoint uses:

```java
findByName(String name)
```

### Example Response

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 1200.0,
    "description": "Gaming Laptop",
    "categoryId": 1,
    "categoryName": "Electronics"
  }
]
```

If no matching product exists:

```json
[]
```

---

# 9. Search Product by Partial Name

### Endpoint

```http
GET /api/products/search/contains?name=lap
```

This endpoint uses:

```java
findByNameContainingIgnoreCase(String name)
```

The search is both:

```text
Partial
+
Case-insensitive
```

For example, these may all match `Laptop`:

```text
lap
LAP
Lap
top
Laptop
```

### Example Response

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 1200.0,
    "description": "Gaming Laptop",
    "categoryId": 1,
    "categoryName": "Electronics"
  }
]
```

---

# 10. Find Products by Price Range

### Endpoint

```http
GET /api/products/price?min=500&max=1500
```

This endpoint uses:

```java
findByPriceBetween(
    Double minPrice,
    Double maxPrice
)
```

It returns products whose prices fall within the specified range.

### Example Response

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 1200.0,
    "description": "Gaming Laptop",
    "categoryId": 1,
    "categoryName": "Electronics"
  },
  {
    "id": 2,
    "name": "Phone",
    "price": 800.0,
    "description": "Smart Phone",
    "categoryId": 1,
    "categoryName": "Electronics"
  }
]
```

---

# 11. Find Products Above Price Using JPQL

### Endpoint

```http
GET /api/products/above-price?min=800
```

This endpoint demonstrates a custom JPQL query:

```java
@Query(
    "SELECT p FROM Product p WHERE p.price >= :minPrice"
)
```

### Example Response

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 1200.0,
    "description": "Gaming Laptop",
    "categoryId": 1,
    "categoryName": "Electronics"
  },
  {
    "id": 2,
    "name": "Phone",
    "price": 800.0,
    "description": "Smart Phone",
    "categoryId": 1,
    "categoryName": "Electronics"
  }
]
```

---

# 12. Find Products Above Price Using Native SQL

### Endpoint

```http
GET /api/products/above-price-native?min=800
```

This endpoint demonstrates a native SQL query:

```java
@Query(
    value = "SELECT * FROM PRODUCT WHERE PRICE >= :minPrice",
    nativeQuery = true
)
```

The result should be similar to the JPQL endpoint.

The difference is how the query is written:

```text
JPQL
 ↓
Java Entity / Properties

Native SQL
 ↓
Database Table / Columns
```

---

# Sample Failure Requests

The project also demonstrates validation and exception-handling scenarios.

---

## Invalid Product Data

### Request

```http
POST /api/products
```

```json
{
  "name": "",
  "price": 0,
  "description": "Test Product",
  "categoryId": 0
}
```

### Expected Status

```text
400 Bad Request
```

### Example Response

```json
{
  "status": 400,
  "errors": {
    "name": "Product name is required",
    "price": "Price must be greater than 0",
    "categoryId": "Category ID must be greater than 0"
  }
}
```

---

# Category Not Found

### Request

```http
POST /api/products
```

```json
{
  "name": "Keyboard",
  "price": 100,
  "description": "Mechanical Keyboard",
  "categoryId": 999
}
```

### Expected Status

```text
404 Not Found
```

### Example Response

```json
{
  "status": 404,
  "message": "Category not found with id: 999"
}
```

---

# Product Not Found

### Request

```http
GET /api/products/999
```

### Expected Status

```text
404 Not Found
```

### Example Response

```json
{
  "status": 404,
  "message": "Product not found with id: 999"
}
```

---

# Invalid JSON / Request Body

Example:

```json
{
  "name": "Laptop",
  "price": "invalid-price",
  "categoryId": 1
}
```

Since `price` expects a numeric value, the request cannot be properly converted.

### Expected Status

```text
400 Bad Request
```

### Example Response

```json
{
  "status": 400,
  "message": "Invalid request body"
}
```

---

# Swagger / OpenAPI

Swagger/OpenAPI documentation is provided using Springdoc.

After starting the application, open:

```text
http://localhost:8080/swagger-ui.html
```

Swagger UI allows developers to:

- View REST endpoints
- View endpoint descriptions
- View request parameters
- View request/response models
- View HTTP response codes
- Execute APIs directly from the browser

The project demonstrates annotations including:

```java
@Operation
@ApiResponse
@Parameter
@Schema
```

Example:

```java
@Operation(
    summary = "Create product",
    description = "Creates a new product under an existing category"
)
@ApiResponse(
    responseCode = "201",
    description = "Product created successfully"
)
```

These annotations document API behavior but do not implement the actual HTTP behavior.

---

# Spring Boot Actuator

Spring Boot Actuator is included for application monitoring.

## Health

```text
http://localhost:8080/actuator/health
```

Example:

```json
{
  "status": "UP"
}
```

## Application Information

```text
http://localhost:8080/actuator/info
```

Actuator demonstrates how production applications can expose operational information for monitoring systems.

---

# Logging

Application logging is implemented using:

```text
SLF4J
```

Example:

```java
private static final Logger logger =
        LoggerFactory.getLogger(ProductService.class);
```

Logging levels are used according to the type of event.

```text
INFO
Create Product
Update Product
Delete Product

DEBUG
Get Product
Search Products
Pagination

WARN
Product not found
Category not found

ERROR
Unexpected application errors
```

Parameterized logging is used:

```java
logger.info(
    "Product created successfully with id: {}",
    savedProduct.getId()
);
```

instead of:

```java
System.out.println(...)
```

Unexpected exceptions are logged on the server while API consumers receive a generic error message.

---

# Spring Profiles

Environment-specific configuration is implemented using Spring Profiles.

Configuration files:

```text
application.properties

application-dev.properties

application-prod.properties
```

---

## Development Profile

The development environment uses:

```text
H2 Database
DEBUG logging
Hibernate SQL logging
H2 Console
```

Example:

```properties
spring.datasource.url=jdbc:h2:mem:productdb

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.h2.console.enabled=true

logging.level.com.spring.rest.demo=DEBUG
```

---

## Production Profile

The production profile demonstrates:

```text
External database
Environment-variable based credentials
INFO logging
Hibernate SQL logging disabled
H2 Console disabled
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/productdb
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

spring.h2.console.enabled=false

logging.level.com.spring.rest.demo=INFO
```

Passwords and other secrets should not be committed to source control.

---

# Running the Application

Clone the repository and navigate to the project directory.

Run using Maven:

```bash
mvn clean install
```

Then:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Alternatively, run the Spring Boot main application class directly from Eclipse or another IDE.

The application starts at:

```text
http://localhost:8080
```

---

# H2 Database Console

When using the development profile:

```text
http://localhost:8080/h2-console
```

Use the datasource settings configured in:

```text
application-dev.properties
```

---

# JPA vs Hibernate

This project uses:

```text
Spring Data JPA
+
JPA
+
Hibernate
```

Relationship:

```text
Application
     |
     v
Spring Data JPA
     |
     v
    JPA
     |
     v
 Hibernate
     |
     v
 Database
```

## JPA

JPA defines persistence concepts and APIs.

Examples used in this project:

```java
@Entity
@Id
@GeneratedValue
@ManyToOne
@OneToMany
@JoinColumn
```

JPA is a specification.

---

## Hibernate

Hibernate is the JPA implementation/provider used by Spring Boot in this project.

Hibernate performs tasks such as:

```text
Entity mapping
SQL generation
Relationship management
Persistence context management
Dirty checking
Database interaction
```

For example, when the application executes:

```java
productRepository.save(product);
```

Hibernate ultimately generates and executes the required SQL.

---

## Spring Data JPA

Spring Data JPA provides repository abstractions that reduce boilerplate code.

Example:

```java
public interface ProductRepository
        extends JpaRepository<Product, Long> {
}
```

This provides methods such as:

```java
save()
findById()
findAll()
existsById()
deleteById()
```

without writing their implementations manually.

---

# Project Structure

```text
src/main/java
└── com.spring.rest.demo
    │
    ├── controller
    │   └── ProductController.java
    │
    ├── dto
    │   ├── ProductRequest.java
    │   └── ProductResponse.java
    │
    ├── entity
    │   ├── Product.java
    │   └── Category.java
    │
    ├── exception
    │   ├── ProductNotFoundException.java
    │   ├── CategoryNotFoundException.java
    │   └── GlobalExceptionHandler.java
    │
    ├── repository
    │   ├── ProductRepository.java
    │   └── CategoryRepository.java
    │
    └── service
        └── ProductService.java
```

---

# Concepts Demonstrated

This project demonstrates:

- Spring Boot application development
- RESTful API design
- Layered architecture
- Spring MVC
- Spring Data JPA
- Hibernate ORM
- JPA entity mapping
- One-to-Many relationship
- Many-to-One relationship
- DTO pattern
- Request validation
- Global exception handling
- Custom exceptions
- Derived query methods
- JPQL
- Native SQL
- Pagination
- Sorting
- Transaction management
- Transaction rollback
- Swagger / OpenAPI
- API documentation
- Spring Boot Actuator
- Health monitoring
- SLF4J logging
- Logging levels
- Spring Profiles
- Environment-specific configuration

---

# Future Enhancements

Future improvements planned for this project include:

- JUnit unit tests
- Mockito service-layer tests
- MockMvc controller tests
- Integration tests
- Additional Hibernate concepts
- Lazy vs Eager loading
- N+1 query problem
- JOIN FETCH
- Improved API error response model

---

# Purpose of This Project

This project was created as a learning POC to demonstrate how commonly used Spring Boot concepts work together in a real REST application.

The main objective is to understand the complete flow:

```text
HTTP Request
     ↓
Controller
     ↓
DTO / Validation
     ↓
Service
     ↓
Transaction
     ↓
Spring Data JPA
     ↓
Hibernate
     ↓
Database
     ↓
Response DTO
     ↓
HTTP Response
```