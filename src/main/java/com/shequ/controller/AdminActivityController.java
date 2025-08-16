package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.ActivityDTO;
import com.shequ.dto.PageResult;
import com.shequ.dto.UserResponse;
import com.shequ.service.ActivityService;
import com.shequ.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/activities")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@Validated
public class AdminActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private AuthService authService;
    /**
     * Get paginated and optionally filtered activity list
     */
    @GetMapping
    public ApiResponse<PageResult<ActivityDTO>> getActivities(
            @RequestParam(defaultValue = "1") int page,             // Page number
            @RequestParam(defaultValue = "10") int size,           // Page size
            @RequestParam(required = false) String title,         // Optional title filter
            @RequestParam(required = false) String status,        // Optional status filter
            @RequestParam(required = false) Integer categoryId) {     // Optional category filter
        try {
            PageResult<ActivityDTO> result = activityService.getActivities(page, size, title, status, categoryId);
            return ApiResponse.success("Successfully obtained the activity list", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the activity list：" + e.getMessage());
        }
    }
    /**
     * Get detailed information of an activity by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<ActivityDTO> getActivity(@PathVariable Long id) {
        try {
            ActivityDTO activity = activityService.getActivityById(id);
            return ApiResponse.success("Successfully obtained the detailed information of the activity", activity);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the detailed information of the activity");
        }
    }
    /**
     * Create a new activity (with authentication)
     */
    @PostMapping
    public ApiResponse<ActivityDTO> createActivity(@Valid @RequestBody ActivityDTO activityDTO,
                                                   HttpServletRequest request) {
        try {
            // Get the current user
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);
            // Call the service layer to create an activity
            ActivityDTO result = activityService.createActivity(activityDTO, currentUser.getId());
            return ApiResponse.success("Successfully created the activity", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to create the activity");
        }
    }
    /**
     * Update existing activity by ID
     */
    @PutMapping("/{id}")
    public ApiResponse<ActivityDTO> updateActivity(@PathVariable Long id,
                                                   @Valid @RequestBody ActivityDTO activityDTO) {
        try {
            ActivityDTO result = activityService.updateActivity(id, activityDTO);
            return ApiResponse.success("The activity was updated successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update the activity");
        }
    }
    /**
     * Delete an activity by ID
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return ApiResponse.success("The activity was deleted successfully", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete the activity");
        }
    }
    /**
     * Update the status of an activity (e.g., ACTIVE, INACTIVE)
     */
    @PutMapping("/{id}/status")
    public ApiResponse<ActivityDTO> updateActivityStatus(@PathVariable Long id,
                                                         @RequestParam String status) {
        try {
            ActivityDTO result = activityService.updateActivityStatus(id, status);
            return ApiResponse.success("The activity status was updated successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update the activity status");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Authentication information is not provided");
    }
}
