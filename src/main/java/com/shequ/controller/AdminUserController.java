package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.PageResult;
import com.shequ.dto.UserResponse;
import com.shequ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// Controller for managing users in the admin system
@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class AdminUserController {

    @Autowired
    private UserService userService;// Injecting user service

    @GetMapping
    public ApiResponse<PageResult<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") int page,    // Current page
            @RequestParam(defaultValue = "10") int size,    // Page size
            @RequestParam(required = false) String keyword,   // Search keyword
            @RequestParam(required = false) String role,     // User role filter
            @RequestParam(required = false) Integer status) {   // User status
        try {
            PageResult<UserResponse> result = userService.getUsers(page, size, keyword, role, status);
            return ApiResponse.success("Successfully obtained the user list", result);   // Successfully obtained the user list
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the user list：" + e.getMessage());   // Failed to obtain
        }
    }
    /**
     * Get user details by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ApiResponse.success("Successfully obtained user details", user);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to get user details");
        }
    }
    /**
     * Update user status (e.g., activate/deactivate)
     */
    @PutMapping("/{id}/status")
    public ApiResponse<UserResponse> updateUserStatus(@PathVariable Long id,
                                                      @RequestParam Integer status) {
        try {
            UserResponse result = userService.updateUserStatus(id, status);
            return ApiResponse.success("The user status was updated successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update user status");
        }
    }
    /**
     * Update user role (e.g., promote to admin)
     */
    @PutMapping("/{id}/role")
    public ApiResponse<UserResponse> updateUserRole(@PathVariable Long id,
                                                    @RequestParam String role) {
        try {
            UserResponse result = userService.updateUserRole(id, role);
            return ApiResponse.success("The user role was updated successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update user role");
        }
    }
    /**
     * Delete user by ID (could be logical or physical)
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ApiResponse.success("The user was deleted successfully", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete user");
        }
    }
    /**
     * Reset user's password
     */
    @PutMapping("/{id}/password")
    public ApiResponse<UserResponse> resetUserPassword(@PathVariable Long id,
                                                       @RequestParam String newPassword) {
        try {
            UserResponse result = userService.resetUserPassword(id, newPassword);
            return ApiResponse.success("The password was reset successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to reset password");
        }
    }
}
