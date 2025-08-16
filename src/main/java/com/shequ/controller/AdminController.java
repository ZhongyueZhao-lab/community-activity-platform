package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.DashboardStatsDTO;
import com.shequ.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ApiResponse<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = adminService.getDashboardStats();
            return ApiResponse.success("Successfully obtained statistical data", stats);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain statistical data：" + e.getMessage());
        }
    }
}