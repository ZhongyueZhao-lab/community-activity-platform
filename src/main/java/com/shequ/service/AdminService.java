package com.shequ.service;

import com.shequ.dto.DashboardStatsDTO;
import com.shequ.entity.Activity;
import com.shequ.repository.ActivityRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    public DashboardStatsDTO getDashboardStats() {
        // 总用户数
        long totalUsers = userRepository.count();

        // 总活动数
        long totalActivities = activityRepository.count();

        // 待审核活动数
        long pendingActivities = activityRepository.countByStatus(Activity.Status.pending);

        // 已发布活动数
        long publishedActivities = activityRepository.countByStatus(Activity.Status.published);

        // 本月新增用户数
        LocalDateTime firstDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(firstDayOfMonth);

        // 本月新增活动数
        long newActivitiesThisMonth = activityRepository.countByCreatedAtAfter(firstDayOfMonth);

        return new DashboardStatsDTO(totalUsers, totalActivities, pendingActivities,
                publishedActivities, newUsersThisMonth, newActivitiesThisMonth);
    }
}