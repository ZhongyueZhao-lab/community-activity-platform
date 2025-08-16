package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.entity.ActivityCategory;
import com.shequ.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@Validated
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;// 注入分类服务层 // Injecting service layer
    /**
     * 获取所有分类
     * Get all categories (enabled + disabled)
     */
    @GetMapping
    public ApiResponse<List<ActivityCategory>> getAllCategories() {
        try {
            List<ActivityCategory> categories = categoryService.getAllCategories();
            return ApiResponse.success("Successfully obtained the category list", categories);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the category list：" + e.getMessage());
        }
    }
    /**
     * 获取所有已启用的分类
     * Get all enabled categories only
     */
    @GetMapping("/enabled")
    public ApiResponse<List<ActivityCategory>> getEnabledCategories() {
        try {
            List<ActivityCategory> categories = categoryService.getEnabledCategories();
            return ApiResponse.success("Successfully obtained enabled categories", categories);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain enabled categories：" + e.getMessage());
        }
    }
    /**
     * 根据 ID 获取分类详情
     * Get category details by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<ActivityCategory> getCategory(@PathVariable Integer id) {
        try {
            ActivityCategory category = categoryService.getCategoryById(id);
            return ApiResponse.success("Successfully obtained category details", category);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to get category details");
        }
    }
    /**
     * 创建新分类
     * Create a new activity category
     */
    @PostMapping
    public ApiResponse<ActivityCategory> createCategory(@Valid @RequestBody ActivityCategory category) {
        try {
            ActivityCategory result = categoryService.createCategory(category);
            return ApiResponse.success("The category was created successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to create category");
        }
    }
    /**
     * 更新指定 ID 的分类
     * Update an existing category by ID
     */
    @PutMapping("/{id}")
    public ApiResponse<ActivityCategory> updateCategory(@PathVariable Integer id,
                                                        @Valid @RequestBody ActivityCategory category) {
        try {
            ActivityCategory result = categoryService.updateCategory(id, category);
            return ApiResponse.success("The category was updated successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update category");
        }
    }
    /**
     * 删除指定 ID 的分类
     * Delete a category by ID
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ApiResponse.success("The category was deleted successfully", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete category");
        }
    }
}

