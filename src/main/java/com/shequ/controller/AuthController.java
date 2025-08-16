package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.*;
import com.shequ.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            LoginResponse response = authService.login(request, httpRequest);
            return ApiResponse.success("Login successfully", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Login failed. Please try again later.");
        }
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse response = authService.register(request);
            return ApiResponse.success("Registration successful", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Registration failed. Please try again later.");
        }
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // 从Authorization头中提取token
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                return ApiResponse.error(401, "No authentication information provided");
            }

            UserResponse response = authService.getCurrentUser(token);
            return ApiResponse.success("Successfully obtained user information", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain user information");
        }
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {

        return ApiResponse.success("Logout successfully", null);
    }
}