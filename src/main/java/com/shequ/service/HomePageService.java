package com.shequ.service;

import com.shequ.dto.ActivityDTO;
import com.shequ.dto.BannerDTO;
import com.shequ.dto.HomePageDTO;
import com.shequ.entity.Activity;
import com.shequ.entity.ActivityCategory;
import com.shequ.entity.Banner;
import com.shequ.entity.User;
import com.shequ.repository.ActivityRepository;
import com.shequ.repository.ActivityCategoryRepository;
import com.shequ.repository.BannerRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HomePageService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemConfigService configService;

    public HomePageDTO getHomePageData() {
        // 获取轮播图数据
        List<BannerDTO> banners = getActiveBanners();

        // 获取最新活动
        List<ActivityDTO> latestActivities = getLatestActivities();

        // 获取精选活动（报名人数多的活动）
        List<ActivityDTO> featuredActivities = getFeaturedActivities();

        // 获取活动分类
        List<ActivityCategory> categories = getEnabledCategories();

        // 获取站点配置
        HomePageDTO.SystemConfigDTO siteConfig = getSiteConfig();

        // 获取统计数据
        HomePageDTO.HomeStatsDTO stats = getHomeStats();

        return new HomePageDTO(banners, latestActivities, featuredActivities, categories, siteConfig, stats);
    }

    public List<BannerDTO> getActiveBanners() {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = bannerRepository.findActiveBanners(now);
        return banners.stream()
                .map(this::convertBannerToDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityDTO> getLatestActivities() {
        // 获取最新发布的6个活动
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        return activityRepository.findByStatus(Activity.Status.published, pageable)
                .getContent()
                .stream()
                .map(this::convertActivityToDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityDTO> getFeaturedActivities() {
        // 获取报名人数较多的精选活动，按当前参与人数倒序排列
        Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "currentParticipants"));
        return activityRepository.findByStatus(Activity.Status.published, pageable)
                .getContent()
                .stream()
                .filter(activity -> activity.getCurrentParticipants() > 0) // 过滤掉没有人报名的活动
                .map(this::convertActivityToDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityCategory> getEnabledCategories() {
        return categoryRepository.findByStatusOrderBySortOrder(1);
    }

    public HomePageDTO.SystemConfigDTO getSiteConfig() {
        String siteName = configService.getConfigValue("site_name", "社区活动平台");
        String siteDescription = configService.getConfigValue("site_description", "一个专注于社区活动组织的平台");
        String contactEmail = configService.getConfigValue("contact_email", "contact@example.com");
        String contactPhone = configService.getConfigValue("contact_phone", "400-000-0000");

        return new HomePageDTO.SystemConfigDTO(siteName, siteDescription, contactEmail, contactPhone);
    }

    public HomePageDTO.HomeStatsDTO getHomeStats() {
        // 总活动数
        long totalActivities = activityRepository.countByStatus(Activity.Status.published);

        // 总用户数
        long totalUsers = userRepository.count();

        // 本月新增活动数
        LocalDateTime firstDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        long thisMonthActivities = activityRepository.countByCreatedAtAfter(firstDayOfMonth);

        // 本月新增用户数
        long thisMonthUsers = userRepository.countByCreatedAtAfter(firstDayOfMonth);

        return new HomePageDTO.HomeStatsDTO(totalActivities, totalUsers, thisMonthActivities, thisMonthUsers);
    }

    private BannerDTO convertBannerToDTO(Banner banner) {
        BannerDTO dto = new BannerDTO(banner);

        // 设置创建者名称
        if (banner.getCreatorId() != null) {
            Optional<User> creator = userRepository.findById(banner.getCreatorId());
            creator.ifPresent(u -> dto.setCreatorName(u.getNickname()));
        }

        return dto;
    }

    public void recordBannerClick(Long bannerId) {
        Optional<Banner> bannerOptional = bannerRepository.findById(bannerId);
        if (bannerOptional.isPresent()) {
            Banner banner = bannerOptional.get();
            banner.setClickCount(banner.getClickCount() + 1);
            bannerRepository.save(banner);
        } else {
            throw new RuntimeException("轮播图不存在");
        }
    }

    private ActivityDTO convertActivityToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO(activity);

        // 设置分类名称
        if (activity.getCategoryId() != null) {
            Optional<ActivityCategory> category = categoryRepository.findById(activity.getCategoryId());
            category.ifPresent(c -> dto.setCategoryName(c.getName()));
        }

        // 设置创建者名称
        if (activity.getCreatorId() != null) {
            Optional<User> creator = userRepository.findById(activity.getCreatorId());
            creator.ifPresent(u -> dto.setCreatorName(u.getNickname()));
        }

        return dto;
    }
}