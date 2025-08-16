package com.shequ.dto;

public class DashboardStatsDTO {

    private long totalUsers;
    private long totalActivities;
    private long pendingActivities;
    private long publishedActivities;
    private long newUsersThisMonth;
    private long newActivitiesThisMonth;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(long totalUsers, long totalActivities, long pendingActivities,
                             long publishedActivities, long newUsersThisMonth, long newActivitiesThisMonth) {
        this.totalUsers = totalUsers;
        this.totalActivities = totalActivities;
        this.pendingActivities = pendingActivities;
        this.publishedActivities = publishedActivities;
        this.newUsersThisMonth = newUsersThisMonth;
        this.newActivitiesThisMonth = newActivitiesThisMonth;
    }

    // Getter和Setter方法
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalActivities() {
        return totalActivities;
    }

    public void setTotalActivities(long totalActivities) {
        this.totalActivities = totalActivities;
    }

    public long getPendingActivities() {
        return pendingActivities;
    }

    public void setPendingActivities(long pendingActivities) {
        this.pendingActivities = pendingActivities;
    }

    public long getPublishedActivities() {
        return publishedActivities;
    }

    public void setPublishedActivities(long publishedActivities) {
        this.publishedActivities = publishedActivities;
    }

    public long getNewUsersThisMonth() {
        return newUsersThisMonth;
    }

    public void setNewUsersThisMonth(long newUsersThisMonth) {
        this.newUsersThisMonth = newUsersThisMonth;
    }

    public long getNewActivitiesThisMonth() {
        return newActivitiesThisMonth;
    }

    public void setNewActivitiesThisMonth(long newActivitiesThisMonth) {
        this.newActivitiesThisMonth = newActivitiesThisMonth;
    }
}
