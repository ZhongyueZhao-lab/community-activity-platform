package com.shequ.service;

import com.shequ.controller.UserProfileController;
import com.shequ.dto.*;
import com.shequ.entity.Activity;
import com.shequ.entity.ActivityCategory;
import com.shequ.entity.ActivityRegistration;
import com.shequ.entity.User;
import com.shequ.repository.ActivityCategoryRepository;
import com.shequ.repository.ActivityRegistrationRepository;
import com.shequ.repository.ActivityRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRegistrationRepository registrationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityCategoryRepository categoryRepository;

    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateUserProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));

        // 验证昵称
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }

        // 验证手机号唯一性
        if (StringUtils.hasText(request.getPhone())) {
            if (!request.getPhone().equals(user.getPhone()) &&
                    userRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("The mobile phone number has been used by another user.");
            }
            user.setPhone(request.getPhone());
        }

        // 验证邮箱唯一性
        if (StringUtils.hasText(request.getEmail())) {
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("The email has been used by another user.");
            }
            user.setEmail(request.getEmail());
        }

        // 更新其他字段
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }

        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));

        // 验证当前密码
        if (!user.getPassword().equals(request.getCurrentPassword())) {
            throw new RuntimeException("The current password is incorrect");
        }

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("The new password and the confirmed password do not match.");
        }

        // 检查新密码是否与当前密码相同
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new RuntimeException("The new password cannot be the same as the current password.");
        }

        // 更新密码
        user.setPassword(request.getNewPassword());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setAvatar(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser);
    }

    public PageResult<UserActivityRegistrationDTO> getUserActivityRegistrations(
            Long userId, int page, int size, String status) {

        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "registrationTime"));

        Page<ActivityRegistration> registrationPage;

        if (StringUtils.hasText(status)) {
            try {
                ActivityRegistration.Status statusEnum = ActivityRegistration.Status.valueOf(status);
                registrationPage = registrationRepository.findByUserIdAndStatus(userId, statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                registrationPage = registrationRepository.findByUserId(userId, pageable);
            }
        } else {
            registrationPage = registrationRepository.findByUserId(userId, pageable);
        }

        // 获取活动信息
        List<Long> activityIds = registrationPage.getContent().stream()
                .map(ActivityRegistration::getActivityId)
                .collect(Collectors.toList());

        Map<Long, Activity> activityMap = activityRepository.findAllById(activityIds).stream()
                .collect(Collectors.toMap(Activity::getId, Function.identity()));

        // 获取分类信息
        List<Integer> categoryIds = activityMap.values().stream()
                .map(Activity::getCategoryId)
                .filter(categoryId -> categoryId != null)
                .collect(Collectors.toList());

        Map<Integer, ActivityCategory> categoryMap = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(ActivityCategory::getId, Function.identity()));

        // 转换为DTO
        List<UserActivityRegistrationDTO> registrationDTOs = registrationPage.getContent().stream()
                .map(registration -> convertToUserActivityRegistrationDTO(
                        registration, activityMap.get(registration.getActivityId()), categoryMap))
                .collect(Collectors.toList());

        return new PageResult<>(registrationDTOs, registrationPage.getTotalElements(), page, size);
    }

    public UserProfileController.UserStatisticsDTO getUserStatistics(Long userId) {
        // 总报名数
        long totalRegistrations = registrationRepository.countByUserId(userId);

        // 已参加活动数
        long attendedActivities = registrationRepository.countByUserIdAndStatus(
                userId, ActivityRegistration.Status.attended);

        // 即将参加的活动数（已报名且活动未开始）
        List<ActivityRegistration> registeredActivities = registrationRepository
                .findByUserIdAndStatus(userId, ActivityRegistration.Status.registered);

        long upcomingActivities = registeredActivities.stream()
                .map(reg -> activityRepository.findById(reg.getActivityId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(activity -> activity.getStartTime().isAfter(LocalDateTime.now()))
                .count();

        // 已取消报名数
        long cancelledRegistrations = registrationRepository.countByUserIdAndStatus(
                userId, ActivityRegistration.Status.cancelled);

        return new UserProfileController.UserStatisticsDTO(
                totalRegistrations, attendedActivities, upcomingActivities, cancelledRegistrations);
    }

    private UserActivityRegistrationDTO convertToUserActivityRegistrationDTO(
            ActivityRegistration registration, Activity activity,
            Map<Integer, ActivityCategory> categoryMap) {

        UserActivityRegistrationDTO dto = new UserActivityRegistrationDTO();

        // 报名信息
        dto.setRegistrationId(registration.getId());
        dto.setRegistrationTime(registration.getRegistrationTime());
        dto.setRegistrationStatus(registration.getStatus());
        dto.setRegistrationNotes(registration.getNotes());

        if (activity != null) {
            // 活动信息
            dto.setActivityId(activity.getId());
            dto.setActivityTitle(activity.getTitle());
            dto.setActivityDescription(activity.getDescription());
            dto.setActivityLocation(activity.getLocation());
            dto.setActivityStartTime(activity.getStartTime());
            dto.setActivityEndTime(activity.getEndTime());
            dto.setActivityFee(activity.getFee());
            dto.setActivityCoverImage(activity.getCoverImage());
            dto.setActivityStatus(activity.getStatus().name());
            dto.setCategoryId(activity.getCategoryId());
            dto.setTags(activity.getTags());

            // 分类信息
            if (activity.getCategoryId() != null) {
                ActivityCategory category = categoryMap.get(activity.getCategoryId());
                if (category != null) {
                    dto.setCategoryName(category.getName());
                }
            }

            // 计算活动状态
            LocalDateTime now = LocalDateTime.now();
            dto.setIsActivityExpired(activity.getEndTime().isBefore(now));

            // 是否可以取消报名（活动未开始且报名状态为已报名）
            dto.setCanCancel(registration.getStatus() == ActivityRegistration.Status.registered &&
                    activity.getStartTime().isAfter(now));

            // 是否可以参加（活动进行中或即将开始）
            dto.setCanAttend(registration.getStatus() == ActivityRegistration.Status.registered &&
                    activity.getStartTime().isBefore(now.plusHours(1)) &&
                    activity.getEndTime().isAfter(now));

            // 距离活动开始的天数
            if (activity.getStartTime().isAfter(now)) {
                dto.setDaysUntilActivity(java.time.temporal.ChronoUnit.DAYS.between(
                        now.toLocalDate(), activity.getStartTime().toLocalDate()));
            } else {
                dto.setDaysUntilActivity(0L);
            }
        }

        return dto;
    }

    // 分页查找用户特定状态的报名记录
    private Page<ActivityRegistration> findByUserIdAndStatus(Long userId,
                                                             ActivityRegistration.Status status,
                                                             Pageable pageable) {
        return registrationRepository.findByUserIdAndStatus(userId, status, pageable);
    }
}