package com.shequ.dto;

import com.shequ.entity.ActivityRegistration;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ActivityRegistrationManagementDTO {

    // 报名基本信息
    private Long registrationId;
    private Long activityId;
    private Long userId;
    private LocalDateTime registrationTime;
    private ActivityRegistration.Status registrationStatus;
    private String registrationNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 活动信息
    private String activityTitle;
    private String activityDescription;
    private String activityLocation;
    private LocalDateTime activityStartTime;
    private LocalDateTime activityEndTime;
    private BigDecimal activityFee;
    private String activityCoverImage;
    private String activityStatus;
    private Integer activityMaxParticipants;
    private Integer activityCurrentParticipants;

    // 用户信息
    private String userNickname;
    private String userEmail;
    private String userPhone;
    private String userAvatar;

    // 状态信息
    private Boolean canCancel = false;
    private Boolean canAttend = false;
    private Boolean isActivityExpired = false;
    private Boolean isActivityFull = false;
    private Long daysUntilActivity;

    // 构造函数
    public ActivityRegistrationManagementDTO() {}

    // Getter和Setter方法
    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

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

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public ActivityRegistration.Status getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(ActivityRegistration.Status registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getRegistrationNotes() {
        return registrationNotes;
    }

    public void setRegistrationNotes(String registrationNotes) {
        this.registrationNotes = registrationNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }

    public LocalDateTime getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(LocalDateTime activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public LocalDateTime getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(LocalDateTime activityEndTime) {
        this.activityEndTime = activityEndTime;
    }

    public BigDecimal getActivityFee() {
        return activityFee;
    }

    public void setActivityFee(BigDecimal activityFee) {
        this.activityFee = activityFee;
    }

    public String getActivityCoverImage() {
        return activityCoverImage;
    }

    public void setActivityCoverImage(String activityCoverImage) {
        this.activityCoverImage = activityCoverImage;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Integer getActivityMaxParticipants() {
        return activityMaxParticipants;
    }

    public void setActivityMaxParticipants(Integer activityMaxParticipants) {
        this.activityMaxParticipants = activityMaxParticipants;
    }

    public Integer getActivityCurrentParticipants() {
        return activityCurrentParticipants;
    }

    public void setActivityCurrentParticipants(Integer activityCurrentParticipants) {
        this.activityCurrentParticipants = activityCurrentParticipants;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }

    public Boolean getCanAttend() {
        return canAttend;
    }

    public void setCanAttend(Boolean canAttend) {
        this.canAttend = canAttend;
    }

    public Boolean getIsActivityExpired() {
        return isActivityExpired;
    }

    public void setIsActivityExpired(Boolean isActivityExpired) {
        this.isActivityExpired = isActivityExpired;
    }

    public Boolean getIsActivityFull() {
        return isActivityFull;
    }

    public void setIsActivityFull(Boolean isActivityFull) {
        this.isActivityFull = isActivityFull;
    }

    public Long getDaysUntilActivity() {
        return daysUntilActivity;
    }

    public void setDaysUntilActivity(Long daysUntilActivity) {
        this.daysUntilActivity = daysUntilActivity;
    }

    // 辅助方法
    public String getDisplayStatus() {
        if (registrationStatus == null) {
            return "Unknown";
        }

        switch (registrationStatus) {
            case registered:
                return "Registered";
            case cancelled:
                return "Cancelled";
            case attended:
                return "Attended";
            default:
                return registrationStatus.name();
        }
    }

    public String getDisplayActivityStatus() {
        if (activityStatus == null) {
            return "Unknown";
        }

        switch (activityStatus.toLowerCase()) {
            case "draft":
                return "Draft";
            case "pending":
                return "Pending";
            case "published":
                return "Published";
            case "cancelled":
                return "Cancelled";
            case "completed":
                return "Completed";
            default:
                return activityStatus;
        }
    }

    public boolean isRegistrationActive() {
        return registrationStatus == ActivityRegistration.Status.registered;
    }

    public boolean isRegistrationCancelled() {
        return registrationStatus == ActivityRegistration.Status.cancelled;
    }

    public boolean isRegistrationAttended() {
        return registrationStatus == ActivityRegistration.Status.attended;
    }

    public String getParticipationRateText() {
        if (activityMaxParticipants == null || activityMaxParticipants <= 0) {
            return activityCurrentParticipants + " / ∞";
        }
        return activityCurrentParticipants + " / " + activityMaxParticipants;
    }

    public Double getParticipationRate() {
        if (activityMaxParticipants == null || activityMaxParticipants <= 0) {
            return 0.0;
        }
        if (activityCurrentParticipants == null) {
            return 0.0;
        }
        return (double) activityCurrentParticipants / activityMaxParticipants * 100;
    }

    public String getActivityFeeText() {
        if (activityFee == null) {
            return "Free";
        }
        return activityFee.compareTo(BigDecimal.ZERO) == 0 ? "Free" : "$" + activityFee;
    }

    public boolean hasUserContact() {
        return (userEmail != null && !userEmail.trim().isEmpty()) ||
                (userPhone != null && !userPhone.trim().isEmpty());
    }

    public String getUserContactInfo() {
        StringBuilder contact = new StringBuilder();

        if (userEmail != null && !userEmail.trim().isEmpty()) {
            contact.append("Email: ").append(userEmail);
        }

        if (userPhone != null && !userPhone.trim().isEmpty()) {
            if (contact.length() > 0) {
                contact.append(" | ");
            }
            contact.append("Phone: ").append(userPhone);
        }

        return contact.toString();
    }

    public boolean canPerformAction(String action) {
        switch (action.toLowerCase()) {
            case "cancel":
                return canCancel != null && canCancel;
            case "attend":
                return canAttend != null && canAttend;
            case "edit":
                return registrationStatus == ActivityRegistration.Status.registered;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "ActivityRegistrationManagementDTO{" +
                "registrationId=" + registrationId +
                ", activityId=" + activityId +
                ", userId=" + userId +
                ", activityTitle='" + activityTitle + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", registrationStatus=" + registrationStatus +
                ", registrationTime=" + registrationTime +
                '}';
    }
}