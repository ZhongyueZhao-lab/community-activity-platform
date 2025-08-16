package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.ActivityDTO;
import com.shequ.dto.BannerDTO;
import com.shequ.dto.HomePageDTO;
import com.shequ.entity.ActivityCategory;
import com.shequ.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/homepage")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @GetMapping
    public ApiResponse<HomePageDTO> getHomePageData() {
        try {
            HomePageDTO homePageData = homePageService.getHomePageData();
            return ApiResponse.success("获取首页数据成功", homePageData);
        } catch (Exception e) {
            return ApiResponse.error("获取首页数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/banners")
    public ApiResponse<List<BannerDTO>> getActiveBanners() {
        try {
            List<BannerDTO> banners = homePageService.getActiveBanners();
            return ApiResponse.success("获取轮播图成功", banners);
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图失败：" + e.getMessage());
        }
    }

    @GetMapping("/activities/latest")
    public ApiResponse<List<ActivityDTO>> getLatestActivities() {
        try {
            List<ActivityDTO> activities = homePageService.getLatestActivities();
            return ApiResponse.success("获取最新活动成功", activities);
        } catch (Exception e) {
            return ApiResponse.error("获取最新活动失败：" + e.getMessage());
        }
    }

    @GetMapping("/activities/featured")
    public ApiResponse<List<ActivityDTO>> getFeaturedActivities() {
        try {
            List<ActivityDTO> activities = homePageService.getFeaturedActivities();
            return ApiResponse.success("获取精选活动成功", activities);
        } catch (Exception e) {
            return ApiResponse.error("获取精选活动失败：" + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ApiResponse<List<ActivityCategory>> getEnabledCategories() {
        try {
            List<ActivityCategory> categories = homePageService.getEnabledCategories();
            return ApiResponse.success("获取活动分类成功", categories);
        } catch (Exception e) {
            return ApiResponse.error("获取活动分类失败：" + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ApiResponse<HomePageDTO.HomeStatsDTO> getHomeStats() {
        try {
            HomePageDTO.HomeStatsDTO stats = homePageService.getHomeStats();
            return ApiResponse.success("获取统计数据成功", stats);
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }

    @PostMapping("/banners/{id}/click")
    public ApiResponse<String> recordBannerClick(@PathVariable Long id) {
        try {
            // 记录轮播图点击
            homePageService.recordBannerClick(id);
            return ApiResponse.success("记录点击成功", null);
        } catch (Exception e) {
            return ApiResponse.error("记录点击失败：" + e.getMessage());
        }
    }
}