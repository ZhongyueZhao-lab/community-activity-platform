package com.shequ.dto;

import com.shequ.entity.SystemMessage;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class MessageDTO {

    private Long id;

    @NotBlank(message = "The message title cannot be empty")
    private String title;

    @NotBlank(message = "The message content cannot be empty")
    private String content;

    private String type = "notice";

    private String targetType = "all";

    private Long targetUserId;

    private Long relatedActivityId;

    private Long senderId;

    private String senderName;

    private Integer status = 1;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 用户相关字段
    private Integer isRead;
    private LocalDateTime readTime;

    public MessageDTO() {}

    public MessageDTO(SystemMessage message) {
        this.id = message.getId();
        this.title = message.getTitle();
        this.content = message.getContent();
        this.type = message.getType().name();
        this.targetType = message.getTargetType().name();
        this.targetUserId = message.getTargetUserId();
        this.relatedActivityId = message.getRelatedActivityId();
        this.senderId = message.getSenderId();
        this.status = message.getStatus();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Long getRelatedActivityId() {
        return relatedActivityId;
    }

    public void setRelatedActivityId(Long relatedActivityId) {
        this.relatedActivityId = relatedActivityId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }
}