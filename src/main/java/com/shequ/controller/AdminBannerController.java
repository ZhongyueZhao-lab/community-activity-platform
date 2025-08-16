package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.BannerDTO;
import com.shequ.dto.PageResult;
import com.shequ.dto.UserResponse;
import com.shequ.service.AuthService;
import com.shequ.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@Validated
public class AdminBannerController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ApiResponse<PageResult<BannerDTO>> getBanners(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<BannerDTO> result = bannerService.getBanners(page, size, title, status);
            return ApiResponse.success("Successfully obtained the carousel image list", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the carousel image list：" + e.getMessage());
        }
    }

    @GetMapping("/enabled")
    public ApiResponse<List<BannerDTO>> getEnabledBanners() {
        try {
            List<BannerDTO> banners = bannerService.getEnabledBanners();
            return ApiResponse.success("Successfully obtained the enabled carousel images", banners);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the enabled carousel images：" + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ApiResponse<List<BannerDTO>> getActiveBanners() {
        try {
            List<BannerDTO> banners = bannerService.getActiveBanners();
            return ApiResponse.success("Successfully obtained the active carousel images", banners);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the valid carousel images：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<BannerDTO> getBanner(@PathVariable Long id) {
        try {
            BannerDTO banner = bannerService.getBannerById(id);
            return ApiResponse.success("获取轮播图详情成功", banner);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图详情失败");
        }
    }

    @PostMapping
    public ApiResponse<BannerDTO> createBanner(@Valid @RequestBody BannerDTO bannerDTO,
                                               HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            BannerDTO result = bannerService.createBanner(bannerDTO, currentUser.getId());
            return ApiResponse.success("Successfully created the carousel image", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("创建轮播图失败");
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<BannerDTO> updateBanner(@PathVariable Long id,
                                               @Valid @RequestBody BannerDTO bannerDTO) {
        try {
            BannerDTO result = bannerService.updateBanner(id, bannerDTO);
            return ApiResponse.success("更新轮播图成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新轮播图失败");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBanner(@PathVariable Long id) {
        try {
            bannerService.deleteBanner(id);
            return ApiResponse.success("删除轮播图成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除轮播图失败");
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<BannerDTO> updateBannerStatus(@PathVariable Long id,
                                                     @RequestParam Integer status) {
        try {
            BannerDTO result = bannerService.updateBannerStatus(id, status);
            return ApiResponse.success("更新轮播图状态成功", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新轮播图状态失败");
        }
    }

    @PostMapping("/{id}/click")
    public ApiResponse<String> incrementClickCount(@PathVariable Long id) {
        try {
            bannerService.incrementClickCount(id);
            return ApiResponse.success("点击统计成功", null);
        } catch (Exception e) {
            return ApiResponse.error("点击统计失败：" + e.getMessage());
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("No authentication information provided");
    }
}