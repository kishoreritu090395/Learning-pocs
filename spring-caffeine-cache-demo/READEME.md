# Spring Boot Ehcache POC

This POC demonstrates how to implement **in-memory caching using Ehcache with Spring Boot and Spring Cache abstraction**.

The application uses a simple Product API to demonstrate cache operations such as:

* Cache Hit
* Cache Miss
* Cache Put
* Cache Eviction
* TTL (Time-To-Live)
* Maximum cache size

## Technologies Used

* Java 8
* Spring Boot 2.7.18
* Spring Cache
* Ehcache 3
* Maven
* REST API

## Why Ehcache?

Ehcache is an in-memory caching solution for Java applications.

Instead of repeatedly retrieving the same data from the underlying data source, frequently accessed data can be stored in the cache.

For example:

```text
First Request

GET /products/1
       |
       v
Check Ehcache
       |
       v
CACHE MISS
       |
       v
Product Repository
       |
       v
Product returned
       |
       v
Store Product in Ehcache
```

Subsequent request:

```text
GET /products/1
       |
       v
Check Ehcache
       |
       v
CACHE HIT
       |
       v
Return Product
```

The repository method does not need to execute again until the cached entry expires or is evicted.

## Project Architecture

```text
Client
   |
   v
ProductController
   |
   v
ProductService
   |
   |-- @Cacheable
   |-- @CachePut
   |-- @CacheEvict
   |
   v
Spring Cache Abstraction
   |
   v
Ehcache
   |
   v
In-Memory Product Repository
```

The Product Repository is used as an in-memory data source for this POC, so no external database is required.

## Spring Cache Abstraction

The service layer uses Spring Cache annotations rather than directly accessing Ehcache.

### @Cacheable

Used when retrieving a product.

```java
@Cacheable(value = "products", key = "#id")
public Product getProduct(Integer id) {

    System.out.println(
        "CACHE MISS - Fetching product from data store: " + id
    );

    return repository.findById(id);
}
```

Spring first checks the `products` cache.

If the value exists, Spring returns it without executing the method.

If the value does not exist, Spring executes the method and stores the returned value in the cache.

## @CachePut

Used when updating a product.

```java
@CachePut(value = "products", key = "#id")
public Product updateProduct(
        Integer id,
        Product updatedProduct) {

    updatedProduct.setId(id);

    return repository.save(updatedProduct);
}
```

`@CachePut` always executes the method and updates the corresponding cache entry with the returned value.

## @CacheEvict

Used when deleting a product.

```java
@CacheEvict(value = "products", key = "#id")
public void deleteProduct(Integer id) {

    repository.deleteById(id);
}
```

When the product is deleted, Spring also removes the corresponding entry from Ehcache.

## Ehcache Configuration

Ehcache configuration is stored in:

```text
src/main/resources/ehcache.xml
```

Example configuration:

```xml
<config
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns="http://www.ehcache.org/v3">

    <cache alias="products">

        <key-type>java.lang.Integer</key-type>
        <value-type>com.spring.cache.demo.model.Product</value-type>

        <expiry>
            <ttl unit="seconds">60</ttl>
        </expiry>

        <resources>
            <heap unit="entries">100</heap>
        </resources>

    </cache>

</config>
```

## TTL - Time To Live

The configuration:

```xml
<ttl unit="seconds">60</ttl>
```

means that a cached product remains valid for 60 seconds.

Example:

```text
10:00:00
GET /products/1
→ CACHE MISS
→ Product stored in Ehcache

10:00:20
GET /products/1
→ CACHE HIT

10:00:50
GET /products/1
→ CACHE HIT

After TTL expires:

10:01:01
GET /products/1
→ CACHE MISS
→ Product loaded again
→ New value stored in cache
```

TTL prevents cached data from remaining indefinitely.

## Maximum Cache Size

The configuration:

```xml
<heap unit="entries">100</heap>
```

limits the cache to approximately 100 product entries in JVM heap memory.

This prevents the cache from growing without bounds.

## Spring Configuration

The application is configured to use the Ehcache JCache implementation.

Example `application.properties`:

```properties
spring.cache.type=jcache
spring.cache.jcache.config=classpath:ehcache.xml
```

Caching must also be enabled using:

```java
@EnableCaching
```

## API Endpoints

| Method | Endpoint         | Description      | Cache Operation |
| ------ | ---------------- | ---------------- | --------------- |
| GET    | `/products/{id}` | Retrieve product | `@Cacheable`    |
| PUT    | `/products/{id}` | Update product   | `@CachePut`     |
| DELETE | `/products/{id}` | Delete product   | `@CacheEvict`   |

## Testing Cache Miss

Request:

```text
GET http://localhost:8080/products/1
```

Expected response:

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 1200.0
}
```

Console:

```text
CACHE MISS - Fetching product from data store: 1
```

## Testing Cache Hit

Immediately call the same endpoint again:

```text
GET http://localhost:8080/products/1
```

The same product is returned.

However, the following message should NOT appear again:

```text
CACHE MISS - Fetching product from data store: 1
```

This confirms that Spring returned the product directly from Ehcache.

## Testing Cache Update

Request:

```text
PUT http://localhost:8080/products/1
```

Request body:

```json
{
  "name": "Gaming Laptop",
  "price": 1500
}
```

`@CachePut` updates both the underlying product data and the cached value.

The next:

```text
GET /products/1
```

returns the updated product directly from the cache.

## Testing Cache Eviction

Request:

```text
DELETE http://localhost:8080/products/1
```

`@CacheEvict` removes product `1` from the `products` cache.

A subsequent:

```text
GET /products/1
```

results in a cache miss.

## Cache Lifecycle

```text
GET Product
     |
     v
@Cacheable
     |
     +---- Cache Hit ----> Return cached Product
     |
     |
     +---- Cache Miss
              |
              v
         Repository
              |
              v
         Product returned
              |
              v
         Store in Ehcache
```

For updates:

```text
PUT Product
     |
     v
Repository Update
     |
     v
@CachePut
     |
     v
Update Ehcache
```

For deletion:

```text
DELETE Product
      |
      v
Repository Delete
      |
      v
@CacheEvict
      |
      v
Remove from Ehcache
```

## Ehcache vs Simple In-Memory Cache

| Feature                  | Spring Simple Cache | Ehcache |
| ------------------------ | ------------------- | ------- |
| In-memory                | Yes                 | Yes     |
| Spring Cache annotations | Yes                 | Yes     |
| Cache Hit/Miss           | Yes                 | Yes     |
| TTL                      | Limited/manual      | Yes     |
| Maximum size             | Limited/manual      | Yes     |
| Automatic eviction       | Limited             | Yes     |
| XML configuration        | No                  | Yes     |
| External server required | No                  | No      |

## Ehcache vs Caffeine

Both Ehcache and Caffeine are local JVM caching solutions.

### Caffeine

Caffeine is lightweight and optimized for high-performance in-memory caching.

Configuration is commonly performed using Java:

```java
Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterWrite(60, TimeUnit.SECONDS);
```

### Ehcache

Ehcache provides extensive cache configuration and commonly uses XML:

```xml
<expiry>
    <ttl unit="seconds">60</ttl>
</expiry>

<resources>
    <heap unit="entries">100</heap>
</resources>
```

Both can be used through Spring's Cache abstraction.

This means the service code can continue using:

```java
@Cacheable
@CachePut
@CacheEvict
```

while the underlying cache implementation changes.

## Key Learning

The most important concept demonstrated by this POC is the separation between:

```text
Application Business Logic
          |
          v
Spring Cache Abstraction
          |
          v
Cache Provider
```

The service depends on Spring Cache annotations rather than directly depending on Ehcache.

Therefore, the cache provider can be changed from:

```text
Simple Cache
     ↓
Caffeine
     ↓
Ehcache
     ↓
Redis
```

with minimal changes to the application's business logic.

## Limitations

Ehcache in this POC runs inside the Spring Boot application's JVM.

If multiple instances of the application are running:

```text
Application Instance 1
       |
       +-- Ehcache 1

Application Instance 2
       |
       +-- Ehcache 2

Application Instance 3
       |
       +-- Ehcache 3
```

each application instance has its own independent cache.

For applications that require a cache shared between multiple instances, a distributed cache such as Redis is more appropriate.

## Purpose

This POC was created to understand how Spring Cache abstraction works with Ehcache and to demonstrate cache retrieval, updates, eviction, TTL, and size-based cache management in a Spring Boot application.
