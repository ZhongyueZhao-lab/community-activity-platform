package com.shequ.dto;

import com.shequ.entity.ActivityCategory;

import java.util.List;

public class HomePageDTO {

    private List<BannerDTO> banners;
    private List<ActivityDTO> latestActivities;
    private List<ActivityDTO> featuredActivities;
    private List<ActivityCategory> categories;
    private SystemConfigDTO siteConfig;
    private HomeStatsDTO stats;

    public HomePageDTO() {}

    public HomePageDTO(List<BannerDTO> banners, List<ActivityDTO> latestActivities,
                       List<ActivityDTO> featuredActivities, List<ActivityCategory> categories,
                       SystemConfigDTO siteConfig, HomeStatsDTO stats) {
        this.banners = banners;
        this.latestActivities = latestActivities;
        this.featuredActivities = featuredActivities;
        this.categories = categories;
        this.siteConfig = siteConfig;
        this.stats = stats;
    }

    // 内部类：首页统计数据
    public static class HomeStatsDTO {
        private long totalActivities;
        private long totalUsers;
        private long thisMonthActivities;
        private long thisMonthUsers;

        public HomeStatsDTO() {}

        public HomeStatsDTO(long totalActivities, long totalUsers, long thisMonthActivities, long thisMonthUsers) {
            this.totalActivities = totalActivities;
            this.totalUsers = totalUsers;
            this.thisMonthActivities = thisMonthActivities;
            this.thisMonthUsers = thisMonthUsers;
        }

        // Getter和Setter方法
        public long getTotalActivities() {
            return totalActivities;
        }

        public void setTotalActivities(long totalActivities) {
            this.totalActivities = totalActivities;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public long getThisMonthActivities() {
            return thisMonthActivities;
        }

        public void setThisMonthActivities(long thisMonthActivities) {
            this.thisMonthActivities = thisMonthActivities;
        }

        public long getThisMonthUsers() {
            return thisMonthUsers;
        }

        public void setThisMonthUsers(long thisMonthUsers) {
            this.thisMonthUsers = thisMonthUsers;
        }
    }

    // 内部类：系统配置数据
    public static class SystemConfigDTO {
        private String siteName;
        private String siteDescription;
        private String contactEmail;
        private String contactPhone;

        public SystemConfigDTO() {}

        public SystemConfigDTO(String siteName, String siteDescription, String contactEmail, String contactPhone) {
            this.siteName = siteName;
            this.siteDescription = siteDescription;
            this.contactEmail = contactEmail;
            this.contactPhone = contactPhone;
        }

        // Getter和Setter方法
        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public String getSiteDescription() {
            return siteDescription;
        }

        public void setSiteDescription(String siteDescription) {
            this.siteDescription = siteDescription;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }
    }

    // Getter和Setter方法
    public List<BannerDTO> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerDTO> banners) {
        this.banners = banners;
    }

    public List<ActivityDTO> getLatestActivities() {
        return latestActivities;
    }

    public void setLatestActivities(List<ActivityDTO> latestActivities) {
        this.latestActivities = latestActivities;
    }

    public List<ActivityDTO> getFeaturedActivities() {
        return featuredActivities;
    }

    public void setFeaturedActivities(List<ActivityDTO> featuredActivities) {
        this.featuredActivities = featuredActivities;
    }

    public List<ActivityCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ActivityCategory> categories) {
        this.categories = categories;
    }

    public SystemConfigDTO getSiteConfig() {
        return siteConfig;
    }

    public void setSiteConfig(SystemConfigDTO siteConfig) {
        this.siteConfig = siteConfig;
    }

    public HomeStatsDTO getStats() {
        return stats;
    }

    public void setStats(HomeStatsDTO stats) {
        this.stats = stats;
    }
}