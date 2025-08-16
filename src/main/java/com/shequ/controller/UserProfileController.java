package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.*;
import com.shequ.service.AuthService;
import com.shequ.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AuthService authService;

    @GetMapping("/info")
    public ApiResponse<UserResponse> getProfile(HttpServletRequest request) {
        try {
            Long userId = getCurrentUserIdRequired(request);
            UserResponse userProfile = userProfileService.getUserProfile(userId);
            return ApiResponse.success("Successfully obtained personal information", userProfile);
        } catch (RuntimeException e) {
            logger.error("Failed to obtain personal information: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("An unknown error occurred while obtaining personal information.", e);
            return ApiResponse.error("Failed to obtain personal information");
        }
    }

    @PutMapping("/update")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserIdRequired(httpRequest);
            UserResponse updatedUser = userProfileService.updateUserProfile(userId, request);
            return ApiResponse.success("Successfully updated personal information", updatedUser);
        } catch (RuntimeException e) {
            logger.error("Failed to update personal information: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("An unknown error occurred while updating personal information.", e);
            return ApiResponse.error("Failed to update personal information");
        }
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                              HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserIdRequired(httpRequest);
            userProfileService.changePassword(userId, request);
            return ApiResponse.success("Password changed successfully", null);
        } catch (RuntimeException e) {
            logger.error("Failed to change password: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("An unknown error occurred while changing the password.", e);
            return ApiResponse.error("Failed to change password");
        }
    }

    @GetMapping("/registrations")
    public ApiResponse<PageResult<UserActivityRegistrationDTO>> getUserRegistrations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            Long userId = getCurrentUserIdRequired(request);
            PageResult<UserActivityRegistrationDTO> registrations =
                    userProfileService.getUserActivityRegistrations(userId, page, size, status);
            return ApiResponse.success("Successfully obtained registration records", registrations);
        } catch (RuntimeException e) {
            logger.error("Failed to obtain registration records: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("An unknown error occurred while obtaining registration records.", e);
            return ApiResponse.error("Failed to obtain registration records");
        }
    }

    @PutMapping("/avatar")
    public ApiResponse<UserResponse> updateAvatar(@RequestBody UpdateAvatarRequest request,
                                                  HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserIdRequired(httpRequest);
            UserResponse updatedUser = userProfileService.updateAvatar(userId, request.getAvatarUrl());
            return ApiResponse.success("Avatar updated successfully", updatedUser);
        } catch (RuntimeException e) {
            logger.error("Failed to update avatar: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("An unknown error occurred while updating the avatar.", e);
            return ApiResponse.error("Failed to update avatar");
        }
    }

    @GetMapping("/statistics")
    public ApiResponse<UserStatisticsDTO> getUserStatistics(HttpServletRequest request) {
        try {
            Long userId = getCurrentUserIdRequired(request);
            UserStatisticsDTO statistics = userProfileService.getUserStatistics(userId);
            return ApiResponse.success("Successfully obtained user statistics", statistics);
        } catch (RuntimeException e) {
            logger.error("Failed to obtain user statistics: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("An unknown error occurred while obtaining user statistics.", e);
            return ApiResponse.error("Failed to obtain user statistics");
        }
    }

    // 内部类：更新头像请求
    public static class UpdateAvatarRequest {
        private String avatarUrl;

        public UpdateAvatarRequest() {}

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

    // 内部类：用户统计信息
    public static class UserStatisticsDTO {
        private long totalRegistrations;
        private long attendedActivities;
        private long upcomingActivities;
        private long cancelledRegistrations;

        public UserStatisticsDTO() {}

        public UserStatisticsDTO(long totalRegistrations, long attendedActivities,
                                 long upcomingActivities, long cancelledRegistrations) {
            this.totalRegistrations = totalRegistrations;
            this.attendedActivities = attendedActivities;
            this.upcomingActivities = upcomingActivities;
            this.cancelledRegistrations = cancelledRegistrations;
        }

        public long getTotalRegistrations() {
            return totalRegistrations;
        }

        public void setTotalRegistrations(long totalRegistrations) {
            this.totalRegistrations = totalRegistrations;
        }

        public long getAttendedActivities() {
            return attendedActivities;
        }

        public void setAttendedActivities(long attendedActivities) {
            this.attendedActivities = attendedActivities;
        }

        public long getUpcomingActivities() {
            return upcomingActivities;
        }

        public void setUpcomingActivities(long upcomingActivities) {
            this.upcomingActivities = upcomingActivities;
        }

        public long getCancelledRegistrations() {
            return cancelledRegistrations;
        }

        public void setCancelledRegistrations(long cancelledRegistrations) {
            this.cancelledRegistrations = cancelledRegistrations;
        }
    }

    // 辅助方法：获取当前用户ID
    private Long getCurrentUserIdRequired(HttpServletRequest request) {
        String token = extractToken(request);
        UserResponse currentUser = authService.getCurrentUser(token);
        return currentUser.getId();
    }

    // 辅助方法：提取token
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Authentication information is not provided");
    }
}