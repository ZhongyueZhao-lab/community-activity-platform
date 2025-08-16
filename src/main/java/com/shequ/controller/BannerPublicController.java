package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.BannerDTO;
import com.shequ.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/banners")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class BannerPublicController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public ApiResponse<List<BannerDTO>> getActiveBanners() {
        try {
            List<BannerDTO> banners = bannerService.getActiveBanners();
            return ApiResponse.success("获取轮播图成功", banners);
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/click")
    public ApiResponse<String> recordClick(@PathVariable Long id) {
        try {
            bannerService.incrementClickCount(id);
            return ApiResponse.success("记录点击成功", null);
        } catch (Exception e) {
            return ApiResponse.error("记录点击失败：" + e.getMessage());
        }
    }
}