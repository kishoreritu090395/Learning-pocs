package com.spring.security.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.demo.dto.ApiResponse;

@RestController
public class UserController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse>  publicApi() {
    	return ResponseEntity.ok(new ApiResponse(200,"This is a public endpoint"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> userApi(Authentication authentication) {
    	ApiResponse response = new ApiResponse( 200, "Welcome USER " + authentication.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> adminApi(Authentication authentication) {
    	ApiResponse response = new ApiResponse( 200, "Welcome ADMIN " + authentication.getName());

        return ResponseEntity.ok(response);
    }
    
}