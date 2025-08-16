package com.shequ.dto;

import com.shequ.entity.Banner;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class BannerDTO {

    private Long id;

    @NotBlank(message = "轮播图标题不能为空")
    private String title;

    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    private String linkUrl;

    private String description;

    private Integer sortOrder = 0;

    private Integer status = 1;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer clickCount = 0;

    private Long creatorId;

    private String creatorName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public BannerDTO() {}

    public BannerDTO(Banner banner) {
        this.id = banner.getId();
        this.title = banner.getTitle();
        this.imageUrl = banner.getImageUrl();
        this.linkUrl = banner.getLinkUrl();
        this.description = banner.getDescription();
        this.sortOrder = banner.getSortOrder();
        this.status = banner.getStatus();
        this.startTime = banner.getStartTime();
        this.endTime = banner.getEndTime();
        this.clickCount = banner.getClickCount();
        this.creatorId = banner.getCreatorId();
        this.createdAt = banner.getCreatedAt();
        this.updatedAt = banner.getUpdatedAt();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
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
}