# Spring Security JWT POC

A learning project demonstrating Spring Security authentication and authorization using:

* Java 8
* Spring Boot 2.7.18
* Spring Security 5
* In-memory users
* BCrypt password encoding
* JWT authentication
* Custom JWT filter
* Role-based authorization
* Method-level security with `@PreAuthorize`

## Features

This project demonstrates:

* Username/password authentication
* In-memory `UserDetailsService`
* BCrypt password hashing
* JWT token generation
* JWT token validation
* Stateless authentication
* Custom `OncePerRequestFilter`
* `SecurityContextHolder`
* Role-based access control
* Method-level authorization
* Basic login exception handling

## Users

The application uses in-memory users.

```text
Username: user
Password: user123
Role: USER
```

```text
Username: admin
Password: admin123
Role: ADMIN
```

## Authentication Flow

```text
POST /auth/login
        |
        v
AuthenticationManager
        |
        v
AuthenticationProvider
        |
        v
UserDetailsService
        |
        v
InMemoryUserDetailsManager
        |
        v
BCrypt Password Check
        |
        v
Authentication Successful
        |
        v
JWT Generated
        |
        v
JWT Returned to Client
```

## JWT Request Flow

For protected APIs, the client sends:

```text
Authorization: Bearer <JWT>
```

The request flow is:

```text
Incoming Request
        |
        v
JwtAuthenticationFilter
        |
        v
Extract JWT
        |
        v
Validate JWT
        |
        v
Extract Username
        |
        v
Load UserDetails
        |
        v
Create Authentication
        |
        v
SecurityContextHolder
        |
        v
Authorization Check
        |
        v
Controller
```

## API Endpoints

| Method | Endpoint           | Access        |
| ------ | ------------------ | ------------- |
| POST   | `/auth/login`      | Public        |
| GET    | `/public`          | Public        |
| GET    | `/user/profile`    | USER or ADMIN |
| GET    | `/admin/dashboard` | ADMIN only    |

## Login Request

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "username": "user",
  "password": "user123"
}
```

## Login Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "user",
  "role": "ROLE_USER"
}
```

## Calling a Protected API

```http
GET /user/profile
Authorization: Bearer <JWT>
```

## Authentication vs Authorization

Authentication answers:

```text
Who are you?
```

Authorization answers:

```text
What are you allowed to access?
```

Example:

```text
USER JWT -> /user/profile -> 200 OK
USER JWT -> /admin/dashboard -> 403 Forbidden
ADMIN JWT -> /admin/dashboard -> 200 OK
```

## 401 vs 403

### 401 Unauthorized

Authentication failed.

Examples:

* Invalid username/password
* Missing JWT
* Invalid JWT
* Expired JWT

### 403 Forbidden

The user is authenticated but does not have the required role.

Example:

```text
ROLE_USER accessing an ADMIN-only API.
```

## JWT Configuration

JWT configuration is stored in `application.properties`.

Example:

```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=1800000
```

Set the JWT secret before running the application.

Example:

```bash
export JWT_SECRET=mysecretkeymysecretkeymysecretkey123456
```

## Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the Spring Boot main class from Eclipse.

The application starts by default on:

```text
http://localhost:8080
```

## Technologies

* Java 8
* Spring Boot 2.7.18
* Spring Security
* Maven
* JJWT
* BCrypt

## Purpose

This project is a proof of concept created to understand the core architecture of Spring Security and JWT-based stateless authentication.
