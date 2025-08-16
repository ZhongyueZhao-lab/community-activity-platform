package com.shequ.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MessageStatsDTO {

    private Long messageId;
    private String messageTitle;
    private long totalUsers;
    private long readCount;
    private double readRate;
    private List<ReadDetailDTO> readDetails;

    public MessageStatsDTO() {}

    public MessageStatsDTO(Long messageId, String messageTitle, long totalUsers, long readCount) {
        this.messageId = messageId;
        this.messageTitle = messageTitle;
        this.totalUsers = totalUsers;
        this.readCount = readCount;
        this.readRate = totalUsers > 0 ? Math.round((double) readCount / totalUsers * 100 * 100.0) / 100.0 : 0.0;
    }

    // 阅读详情内部类
    public static class ReadDetailDTO {
        private Long userId;
        private String userNickname;
        private Integer isRead;
        private LocalDateTime readTime;

        public ReadDetailDTO() {}

        public ReadDetailDTO(Long userId, String userNickname, Integer isRead, LocalDateTime readTime) {
            this.userId = userId;
            this.userNickname = userNickname;
            this.isRead = isRead;
            this.readTime = readTime;
        }

        // Getter和Setter方法
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public void setUserNickname(String userNickname) {
            this.userNickname = userNickname;
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

    // Getter和Setter方法
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getReadCount() {
        return readCount;
    }

    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }

    public double getReadRate() {
        return readRate;
    }

    public void setReadRate(double readRate) {
        this.readRate = readRate;
    }

    public List<ReadDetailDTO> getReadDetails() {
        return readDetails;
    }

    public void setReadDetails(List<ReadDetailDTO> readDetails) {
        this.readDetails = readDetails;
    }
}