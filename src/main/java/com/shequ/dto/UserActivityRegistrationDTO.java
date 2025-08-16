package com.shequ.dto;

import com.shequ.entity.ActivityRegistration;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserActivityRegistrationDTO {

    private Long registrationId;
    private Long activityId;
    private String activityTitle;
    private String activityDescription;
    private String activityLocation;
    private LocalDateTime activityStartTime;
    private LocalDateTime activityEndTime;
    private BigDecimal activityFee;
    private String activityCoverImage;
    private String activityStatus;
    private Integer categoryId;
    private String categoryName;
    private String tags;

    // 报名相关信息
    private LocalDateTime registrationTime;
    private ActivityRegistration.Status registrationStatus;
    private String registrationNotes;

    // 活动状态信息
    private Boolean isActivityExpired;
    private Boolean canCancel;
    private Boolean canAttend;
    private Long daysUntilActivity;

    public UserActivityRegistrationDTO() {}

    // Getter 和 Setter 方法
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public Boolean getIsActivityExpired() {
        return isActivityExpired;
    }

    public void setIsActivityExpired(Boolean isActivityExpired) {
        this.isActivityExpired = isActivityExpired;
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

    public Long getDaysUntilActivity() {
        return daysUntilActivity;
    }

    public void setDaysUntilActivity(Long daysUntilActivity) {
        this.daysUntilActivity = daysUntilActivity;
    }
}