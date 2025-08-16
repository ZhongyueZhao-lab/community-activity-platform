package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.ActivityDetailDTO;
import com.shequ.dto.UserResponse;
import com.shequ.entity.ActivityRegistration;
import com.shequ.service.ActivityRegistrationService;
import com.shequ.service.ActivityService;
import com.shequ.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/public/activities")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class ActivityDetailController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRegistrationService registrationService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{id}/detail")
    public ApiResponse<ActivityDetailDTO> getActivityDetail(@PathVariable Long id,
                                                            HttpServletRequest request) {
        try {
            Long currentUserId = getCurrentUserId(request);
            ActivityDetailDTO activity = activityService.getActivityDetail(id, currentUserId);
            return ApiResponse.success("Successfully obtained the activity details", activity);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain activity details");
        }
    }

    @PostMapping("/{id}/register")
    public ApiResponse<String> registerForActivity(@PathVariable Long id,
                                                   @RequestBody(required = false) RegistrationRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            Long currentUserId = getCurrentUserIdRequired(httpRequest);
            String notes = request != null ? request.getNotes() : null;

            registrationService.registerForActivity(id, currentUserId, notes);
            return ApiResponse.success("Registration successful", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Registration failed. Please try again.");
        }
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<String> cancelRegistration(@PathVariable Long id,
                                                  HttpServletRequest request) {
        try {
            Long currentUserId = getCurrentUserIdRequired(request);

            registrationService.cancelRegistration(id, currentUserId);
            return ApiResponse.success("Registration cancellation was successful", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to cancel registration. Please try again.");
        }
    }

    @GetMapping("/{id}/participants")
    public ApiResponse<List<ActivityDetailDTO.ParticipantDTO>> getActivityParticipants(@PathVariable Long id) {
        try {
            List<ActivityDetailDTO.ParticipantDTO> participants = registrationService.getActivityParticipants(id);
            return ApiResponse.success("Successfully obtained the participant list", participants);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the participant list：" + e.getMessage());
        }
    }

    @GetMapping("/{id}/registration-status")
    public ApiResponse<RegistrationStatusResponse> getRegistrationStatus(@PathVariable Long id,
                                                                         HttpServletRequest request) {
        try {
            Long currentUserId = getCurrentUserId(request);

            RegistrationStatusResponse response = new RegistrationStatusResponse();

            if (currentUserId != null) {
                boolean isRegistered = registrationService.isUserRegistered(id, currentUserId);
                response.setRegistered(isRegistered);

                if (isRegistered) {
                    ActivityRegistration registration = registrationService.getUserRegistration(id, currentUserId)
                            .orElse(null);
                    if (registration != null) {
                        response.setRegistrationTime(registration.getRegistrationTime());
                        response.setStatus(registration.getStatus().name());
                        response.setNotes(registration.getNotes());
                    }
                }
            } else {
                response.setRegistered(false);
            }

            long registrationCount = registrationService.getActivityRegistrationCount(id);
            response.setTotalRegistrations(registrationCount);

            return ApiResponse.success("Successfully obtained the registration status", response);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the registration status：" + e.getMessage());
        }
    }

    // Administrator Interface: Mark User as Having Participated in the Activity
    @PostMapping("/{activityId}/mark-attended/{userId}")
    public ApiResponse<String> markAsAttended(@PathVariable Long activityId,
                                              @PathVariable Long userId,
                                              HttpServletRequest request) {
        try {
            // Verify administrator privileges
            Long currentUserId = getCurrentUserIdRequired(request);
            UserResponse currentUser = authService.getCurrentUser(extractToken(request));

            if (!"admin".equals(currentUser.getRole())) {
                return ApiResponse.error(403, "Insufficient permissions");
            }

            registrationService.markAsAttended(activityId, userId);
            return ApiResponse.success("Marked as attended successfully", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to mark as attended");
        }
    }

    // Inner class: Registration Request
    public static class RegistrationRequest {
        private String notes;

        public RegistrationRequest() {}

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    // Inner class: Registration Status Response
    public static class RegistrationStatusResponse {
        private boolean registered;
        private String status;
        private java.time.LocalDateTime registrationTime;
        private String notes;
        private long totalRegistrations;

        public RegistrationStatusResponse() {}

        // Getter and Setter methods
        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(boolean registered) {
            this.registered = registered;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public java.time.LocalDateTime getRegistrationTime() {
            return registrationTime;
        }

        public void setRegistrationTime(java.time.LocalDateTime registrationTime) {
            this.registrationTime = registrationTime;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public long getTotalRegistrations() {
            return totalRegistrations;
        }

        public void setTotalRegistrations(long totalRegistrations) {
            this.totalRegistrations = totalRegistrations;
        }
    }

    // Auxiliary methods
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token != null) {
                UserResponse user = authService.getCurrentUser(token);
                return user.getId();
            }
        } catch (Exception e) {
            // Ignore the error and return null to indicate that the user is not logged in.
        }
        return null;
    }

    private Long getCurrentUserIdRequired(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            throw new RuntimeException("Please log in first");
        }
        return userId;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}