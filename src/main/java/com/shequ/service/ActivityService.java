package com.shequ.service;

import com.shequ.dto.ActivityDTO;
import com.shequ.dto.ActivityDetailDTO;
import com.shequ.dto.PageResult;
import com.shequ.entity.Activity;
import com.shequ.entity.ActivityCategory;
import com.shequ.entity.ActivityRegistration;
import com.shequ.entity.User;
import com.shequ.repository.ActivityCategoryRepository;
import com.shequ.repository.ActivityRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRegistrationService registrationService;

    public PageResult<ActivityDTO> getActivities(int page, int size, String title, String status, Integer categoryId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Activity.Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Activity.Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }

        Page<Activity> pageResult = activityRepository.findByConditions(title, statusEnum, categoryId, pageable);

        List<ActivityDTO> activityDTOs = pageResult.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(activityDTOs, pageResult.getTotalElements(), page, size);
    }

    public ActivityDTO getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));
        return convertToDTO(activity);
    }

    public ActivityDetailDTO getActivityDetail(Long id, Long currentUserId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        return convertToDetailDTO(activity, currentUserId);
    }

    @Transactional
    public ActivityDTO createActivity(ActivityDTO activityDTO, Long creatorId) {
        Activity activity = new Activity();
        copyDTOToEntity(activityDTO, activity);
        activity.setCreatorId(creatorId);
        activity.setStatus(Activity.Status.pending);
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());

        Activity savedActivity = activityRepository.save(activity);
        return convertToDTO(savedActivity);
    }

    @Transactional
    public ActivityDTO updateActivity(Long id, ActivityDTO activityDTO) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        copyDTOToEntity(activityDTO, activity);
        activity.setUpdatedAt(LocalDateTime.now());

        Activity savedActivity = activityRepository.save(activity);
        return convertToDTO(savedActivity);
    }

    @Transactional
    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new RuntimeException("The activity does not exist");
        }
        activityRepository.deleteById(id);
    }

    @Transactional
    public ActivityDTO updateActivityStatus(Long id, String status) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        try {
            Activity.Status statusEnum = Activity.Status.valueOf(status);
            activity.setStatus(statusEnum);
            activity.setUpdatedAt(LocalDateTime.now());

            Activity savedActivity = activityRepository.save(activity);
            return convertToDTO(savedActivity);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid activity status");
        }
    }

    private ActivityDTO convertToDTO(Activity activity) {
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

    private ActivityDetailDTO convertToDetailDTO(Activity activity, Long currentUserId) {
        ActivityDetailDTO dto = new ActivityDetailDTO(activity);

        // 设置分类名称
        if (activity.getCategoryId() != null) {
            Optional<ActivityCategory> category = categoryRepository.findById(activity.getCategoryId());
            category.ifPresent(c -> dto.setCategoryName(c.getName()));
        }

        // 设置创建者信息
        if (activity.getCreatorId() != null) {
            Optional<User> creator = userRepository.findById(activity.getCreatorId());
            if (creator.isPresent()) {
                User user = creator.get();
                dto.setCreatorName(user.getNickname());
                dto.setCreatorAvatar(user.getAvatar());
            }
        }

        // 解析图片和标签
        dto.setImageList(parseImages(activity.getImages()));
        dto.setTagList(parseTags(activity.getTags()));

        // 设置用户报名相关信息
        if (currentUserId != null) {
            Optional<ActivityRegistration> userRegistration = registrationService
                    .getUserRegistration(activity.getId(), currentUserId);

            if (userRegistration.isPresent()) {
                ActivityRegistration registration = userRegistration.get();
                dto.setIsRegistered(registration.getStatus() == ActivityRegistration.Status.registered);
                dto.setRegistrationStatus(registration.getStatus());
                dto.setUserRegistrationTime(registration.getRegistrationTime());
                dto.setUserRegistrationNotes(registration.getNotes());
            }

            // 设置是否可以报名
            String cannotRegisterReason = registrationService.getRegistrationStatusReason(activity, currentUserId);
            dto.setCanRegister(cannotRegisterReason == null);
            dto.setCannotRegisterReason(cannotRegisterReason);
        } else {
            // 未登录用户的报名状态检查
            String cannotRegisterReason = registrationService.getRegistrationStatusReason(activity, null);
            dto.setCanRegister(cannotRegisterReason == null);
            dto.setCannotRegisterReason(cannotRegisterReason);
        }

        // 获取参与者列表
        dto.setParticipants(registrationService.getActivityParticipants(activity.getId()));

        return dto;
    }

    private List<String> parseImages(String images) {
        if (images == null || images.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 如果是JSON格式
            if (images.startsWith("[") && images.endsWith("]")) {
                // 简单的JSON解析，去掉方括号和引号
                String content = images.substring(1, images.length() - 1);
                return Arrays.stream(content.split(","))
                        .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            } else {
                // 逗号分隔格式
                return Arrays.stream(images.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void copyDTOToEntity(ActivityDTO dto, Activity entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCategoryId(dto.getCategoryId());
        entity.setLocation(dto.getLocation());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRegistrationStartTime(dto.getRegistrationStartTime());
        entity.setRegistrationEndTime(dto.getRegistrationEndTime());
        entity.setMaxParticipants(dto.getMaxParticipants());
        entity.setFee(dto.getFee());
        entity.setImages(dto.getImages());
        entity.setCoverImage(dto.getCoverImage()); // 添加封面图片字段
        entity.setTags(dto.getTags());
        entity.setContactInfo(dto.getContactInfo());

        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            try {
                entity.setStatus(Activity.Status.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                // 忽略无效状态
            }
        }
    }
}