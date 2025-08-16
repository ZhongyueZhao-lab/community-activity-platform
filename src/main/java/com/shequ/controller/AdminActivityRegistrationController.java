package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.ActivityRegistrationManagementDTO;
import com.shequ.dto.PageResult;
import com.shequ.service.ActivityRegistrationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/activity-registrations")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@Validated
public class AdminActivityRegistrationController {

    @Autowired
    private ActivityRegistrationManagementService registrationManagementService;

    /**
     * 获取活动报名列表（分页）
     */
    @GetMapping
    public ApiResponse<PageResult<ActivityRegistrationManagementDTO>> getActivityRegistrations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String activityTitle,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status) {
        try {
            PageResult<ActivityRegistrationManagementDTO> result = registrationManagementService
                    .getActivityRegistrations(page, size, activityId, activityTitle, userName, status);
            return ApiResponse.success("Successfully obtained the registration list", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the registration list：" + e.getMessage());
        }
    }

    /**
     * 获取特定活动的报名列表
     */
    @GetMapping("/activity/{activityId}")
    public ApiResponse<PageResult<ActivityRegistrationManagementDTO>> getRegistrationsByActivity(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status) {
        try {
            PageResult<ActivityRegistrationManagementDTO> result = registrationManagementService
                    .getRegistrationsByActivity(activityId, page, size, userName, status);
            return ApiResponse.success("Successfully obtained the activity registration list", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the activity registration list");
        }
    }

    /**
     * 获取报名详情
     */
    @GetMapping("/{registrationId}")
    public ApiResponse<ActivityRegistrationManagementDTO> getRegistrationDetail(@PathVariable Long registrationId) {
        try {
            ActivityRegistrationManagementDTO registration = registrationManagementService
                    .getRegistrationDetail(registrationId);
            return ApiResponse.success("Successfully obtained the registration details", registration);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the registration details");
        }
    }

    /**
     * 更新报名状态
     */
    @PutMapping("/{registrationId}/status")
    public ApiResponse<ActivityRegistrationManagementDTO> updateRegistrationStatus(
            @PathVariable Long registrationId,
            @RequestParam String status) {
        try {
            ActivityRegistrationManagementDTO updatedRegistration = registrationManagementService
                    .updateRegistrationStatus(registrationId, status);
            return ApiResponse.success("Successfully updated the registration status", updatedRegistration);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update the registration status");
        }
    }

    /**
     * 批量更新报名状态
     */
    @PutMapping("/batch/status")
    public ApiResponse<String> batchUpdateRegistrationStatus(
            @RequestBody @Valid BatchStatusUpdateRequest request) {
        try {
            registrationManagementService.batchUpdateRegistrationStatus(request.getRegistrationIds(), request.getStatus());
            return ApiResponse.success("The batch update of registration status was successful", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("The batch update of registration status failed");
        }
    }

    /**
     * 取消用户报名（管理员操作）
     */
    @DeleteMapping("/{registrationId}")
    public ApiResponse<String> cancelRegistration(@PathVariable Long registrationId) {
        try {
            registrationManagementService.cancelRegistrationByAdmin(registrationId);
            return ApiResponse.success("The registration has been successfully cancelled", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to cancel the registration");
        }
    }

    /**
     * 批量取消报名
     */
    @DeleteMapping("/batch")
    public ApiResponse<String> batchCancelRegistrations(@RequestBody @Valid BatchCancelRequest request) {
        try {
            registrationManagementService.batchCancelRegistrations(request.getRegistrationIds());
            return ApiResponse.success("The batch cancellation of registrations was successful", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Batch cancellation of registrations failed");
        }
    }

    /**
     * 导出活动报名数据
     */
    @GetMapping("/export")
    public ApiResponse<String> exportRegistrations(
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String activityTitle,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status) {
        try {
            String downloadUrl = registrationManagementService.exportRegistrations(activityId, activityTitle, userName, status);
            return ApiResponse.success("导出成功", downloadUrl);
        } catch (Exception e) {
            return ApiResponse.error("导出失败：" + e.getMessage());
        }
    }

    /**
     * 获取报名统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<RegistrationStatistics> getRegistrationStatistics(
            @RequestParam(required = false) Long activityId) {
        try {
            RegistrationStatistics statistics = registrationManagementService.getRegistrationStatistics(activityId);
            return ApiResponse.success("获取统计信息成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 手动添加报名记录（管理员操作）
     */
    @PostMapping("/manual")
    public ApiResponse<ActivityRegistrationManagementDTO> manualAddRegistration(
            @RequestBody @Valid ManualRegistrationRequest request) {
        try {
            ActivityRegistrationManagementDTO registration = registrationManagementService
                    .manualAddRegistration(request.getActivityId(), request.getUserId(), request.getNotes());
            return ApiResponse.success("手动添加报名成功", registration);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("手动添加报名失败");
        }
    }

    // 内部请求类
    public static class BatchStatusUpdateRequest {
        @Valid
        private java.util.List<Long> registrationIds;
        private String status;

        public java.util.List<Long> getRegistrationIds() {
            return registrationIds;
        }

        public void setRegistrationIds(java.util.List<Long> registrationIds) {
            this.registrationIds = registrationIds;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class BatchCancelRequest {
        @Valid
        private java.util.List<Long> registrationIds;

        public java.util.List<Long> getRegistrationIds() {
            return registrationIds;
        }

        public void setRegistrationIds(java.util.List<Long> registrationIds) {
            this.registrationIds = registrationIds;
        }
    }

    public static class ManualRegistrationRequest {
        private Long activityId;
        private Long userId;
        private String notes;

        public Long getActivityId() {
            return activityId;
        }

        public void setActivityId(Long activityId) {
            this.activityId = activityId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class RegistrationStatistics {
        private long totalRegistrations;
        private long registeredCount;
        private long cancelledCount;
        private long attendedCount;
        private double attendanceRate;
        private double cancellationRate;

        public RegistrationStatistics() {}

        public RegistrationStatistics(long totalRegistrations, long registeredCount,
                                      long cancelledCount, long attendedCount) {
            this.totalRegistrations = totalRegistrations;
            this.registeredCount = registeredCount;
            this.cancelledCount = cancelledCount;
            this.attendedCount = attendedCount;

            if (totalRegistrations > 0) {
                this.attendanceRate = (double) attendedCount / totalRegistrations * 100;
                this.cancellationRate = (double) cancelledCount / totalRegistrations * 100;
            }
        }

        // Getter and Setter methods
        public long getTotalRegistrations() {
            return totalRegistrations;
        }

        public void setTotalRegistrations(long totalRegistrations) {
            this.totalRegistrations = totalRegistrations;
        }

        public long getRegisteredCount() {
            return registeredCount;
        }

        public void setRegisteredCount(long registeredCount) {
            this.registeredCount = registeredCount;
        }

        public long getCancelledCount() {
            return cancelledCount;
        }

        public void setCancelledCount(long cancelledCount) {
            this.cancelledCount = cancelledCount;
        }

        public long getAttendedCount() {
            return attendedCount;
        }

        public void setAttendedCount(long attendedCount) {
            this.attendedCount = attendedCount;
        }

        public double getAttendanceRate() {
            return attendanceRate;
        }

        public void setAttendanceRate(double attendanceRate) {
            this.attendanceRate = attendanceRate;
        }

        public double getCancellationRate() {
            return cancellationRate;
        }

        public void setCancellationRate(double cancellationRate) {
            this.cancellationRate = cancellationRate;
        }
    }
}