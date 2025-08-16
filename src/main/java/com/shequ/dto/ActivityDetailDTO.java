package com.shequ.dto;

import com.shequ.entity.Activity;
import com.shequ.entity.ActivityRegistration;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ActivityDetailDTO {

    private Long id;
    private String title;
    private String description;
    private Integer categoryId;
    private String categoryName;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationStartTime;
    private LocalDateTime registrationEndTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal fee;
    private String images;
    private List<String> imageList; // 解析后的图片列表
    private String coverImage; // 活动封面图片
    private String tags;
    private List<String> tagList; // 解析后的标签列表
    private String contactInfo;
    private String status;
    private Long creatorId;
    private String creatorName;
    private String creatorAvatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 用户相关信息
    private Boolean isRegistered = false; // 当前用户是否已报名
    private ActivityRegistration.Status registrationStatus; // 当前用户的报名状态
    private LocalDateTime userRegistrationTime; // 当前用户的报名时间
    private String userRegistrationNotes; // 当前用户的报名备注

    // 报名相关信息
    private Boolean canRegister = false; // 是否可以报名
    private String cannotRegisterReason; // 不能报名的原因
    private List<ParticipantDTO> participants; // 参与者列表
    private Double participationRate; // 报名率

    // 活动状态信息
    private Boolean isExpired = false; // 活动是否已过期
    private Boolean isFull = false; // 活动是否已满员
    private Boolean isRegistrationOpen = false; // 报名是否开放
    private Long remainingDays; // 距离活动开始还有多少天
    private Long remainingSeats; // 剩余名额

    // 参与者内部类
    public static class ParticipantDTO {
        private Long userId;
        private String nickname;
        private String avatar;
        private LocalDateTime registrationTime;
        private ActivityRegistration.Status status;

        public ParticipantDTO() {}

        public ParticipantDTO(Long userId, String nickname, String avatar,
                              LocalDateTime registrationTime, ActivityRegistration.Status status) {
            this.userId = userId;
            this.nickname = nickname;
            this.avatar = avatar;
            this.registrationTime = registrationTime;
            this.status = status;
        }

        // Getter和Setter方法
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public LocalDateTime getRegistrationTime() {
            return registrationTime;
        }

        public void setRegistrationTime(LocalDateTime registrationTime) {
            this.registrationTime = registrationTime;
        }

        public ActivityRegistration.Status getStatus() {
            return status;
        }

        public void setStatus(ActivityRegistration.Status status) {
            this.status = status;
        }
    }

    // 构造函数
    public ActivityDetailDTO() {}

    public ActivityDetailDTO(Activity activity) {
        this.id = activity.getId();
        this.title = activity.getTitle();
        this.description = activity.getDescription();
        this.categoryId = activity.getCategoryId();
        this.location = activity.getLocation();
        this.latitude = activity.getLatitude();
        this.longitude = activity.getLongitude();
        this.startTime = activity.getStartTime();
        this.endTime = activity.getEndTime();
        this.registrationStartTime = activity.getRegistrationStartTime();
        this.registrationEndTime = activity.getRegistrationEndTime();
        this.maxParticipants = activity.getMaxParticipants();
        this.currentParticipants = activity.getCurrentParticipants();
        this.fee = activity.getFee();
        this.images = activity.getImages();
        this.coverImage = activity.getCoverImage();
        this.tags = activity.getTags();
        this.contactInfo = activity.getContactInfo();
        this.status = activity.getStatus().name();
        this.creatorId = activity.getCreatorId();
        this.createdAt = activity.getCreatedAt();
        this.updatedAt = activity.getUpdatedAt();

        // 计算相关状态
        calculateStatus();
    }

    private void calculateStatus() {
        LocalDateTime now = LocalDateTime.now();

        // 计算是否过期
        this.isExpired = this.endTime != null && this.endTime.isBefore(now);

        // 计算是否满员
        this.isFull = this.maxParticipants != null && this.maxParticipants > 0 &&
                this.currentParticipants != null && this.currentParticipants >= this.maxParticipants;

        // 计算报名是否开放
        boolean registrationTimeValid = true;
        if (this.registrationStartTime != null && this.registrationStartTime.isAfter(now)) {
            registrationTimeValid = false;
        }
        if (this.registrationEndTime != null && this.registrationEndTime.isBefore(now)) {
            registrationTimeValid = false;
        }
        this.isRegistrationOpen = registrationTimeValid && !this.isExpired && !this.isFull && "published".equals(this.status);

        // 计算剩余天数
        if (this.startTime != null) {
            this.remainingDays = java.time.temporal.ChronoUnit.DAYS.between(now.toLocalDate(), this.startTime.toLocalDate());
        }

        // 计算剩余名额
        if (this.maxParticipants != null && this.maxParticipants > 0 && this.currentParticipants != null) {
            this.remainingSeats = Long.valueOf(this.maxParticipants - this.currentParticipants);
        }

        // 计算报名率
        if (this.maxParticipants != null && this.maxParticipants > 0 && this.currentParticipants != null) {
            this.participationRate = (double) this.currentParticipants / this.maxParticipants * 100;
        }
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getRegistrationStartTime() {
        return registrationStartTime;
    }

    public void setRegistrationStartTime(LocalDateTime registrationStartTime) {
        this.registrationStartTime = registrationStartTime;
    }

    public LocalDateTime getRegistrationEndTime() {
        return registrationEndTime;
    }

    public void setRegistrationEndTime(LocalDateTime registrationEndTime) {
        this.registrationEndTime = registrationEndTime;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
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

    public Boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(Boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public ActivityRegistration.Status getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(ActivityRegistration.Status registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public LocalDateTime getUserRegistrationTime() {
        return userRegistrationTime;
    }

    public void setUserRegistrationTime(LocalDateTime userRegistrationTime) {
        this.userRegistrationTime = userRegistrationTime;
    }

    public String getUserRegistrationNotes() {
        return userRegistrationNotes;
    }

    public void setUserRegistrationNotes(String userRegistrationNotes) {
        this.userRegistrationNotes = userRegistrationNotes;
    }

    public Boolean getCanRegister() {
        return canRegister;
    }

    public void setCanRegister(Boolean canRegister) {
        this.canRegister = canRegister;
    }

    public String getCannotRegisterReason() {
        return cannotRegisterReason;
    }

    public void setCannotRegisterReason(String cannotRegisterReason) {
        this.cannotRegisterReason = cannotRegisterReason;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public Double getParticipationRate() {
        return participationRate;
    }

    public void setParticipationRate(Double participationRate) {
        this.participationRate = participationRate;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public Boolean getIsFull() {
        return isFull;
    }

    public void setIsFull(Boolean isFull) {
        this.isFull = isFull;
    }

    public Boolean getIsRegistrationOpen() {
        return isRegistrationOpen;
    }

    public void setIsRegistrationOpen(Boolean isRegistrationOpen) {
        this.isRegistrationOpen = isRegistrationOpen;
    }

    public Long getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Long remainingDays) {
        this.remainingDays = remainingDays;
    }

    public Long getRemainingSeats() {
        return remainingSeats;
    }

    public void setRemainingSeats(Long remainingSeats) {
        this.remainingSeats = remainingSeats;
    }
}