package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.ActivityDTO;
import com.shequ.dto.PageResult;
import com.shequ.entity.ActivityCategory;
import com.shequ.service.ActivityService;
import com.shequ.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class PublicController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/activities")
    public ApiResponse<PageResult<ActivityDTO>> getPublicActivities(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer categoryId) {
        try {
            // 只获取已发布的活动
            PageResult<ActivityDTO> result = activityService.getActivities(page, size, title, "published", categoryId);
            return ApiResponse.success("Successfully obtained the activity list", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the activity list：" + e.getMessage());
        }
    }

    @GetMapping("/activities/{id}")
    public ApiResponse<ActivityDTO> getPublicActivity(@PathVariable Long id) {
        try {
            ActivityDTO activity = activityService.getActivityById(id);
            // 检查活动是否已发布
            if (!"published".equals(activity.getStatus())) {
                return ApiResponse.error("The activity does not exist or has not been published");
            }
            return ApiResponse.success("Successfully obtained the activity details", activity);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the activity details");
        }
    }

    @GetMapping("/categories")
    public ApiResponse<List<ActivityCategory>> getEnabledCategories() {
        try {
            List<ActivityCategory> categories = categoryService.getEnabledCategories();
            return ApiResponse.success("Successfully obtained the category list", categories);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the category list：" + e.getMessage());
        }
    }
}