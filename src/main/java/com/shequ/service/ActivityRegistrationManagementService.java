package com.shequ.service;

import com.shequ.controller.AdminActivityRegistrationController.RegistrationStatistics;
import com.shequ.dto.ActivityRegistrationManagementDTO;
import com.shequ.dto.PageResult;
import com.shequ.entity.Activity;
import com.shequ.entity.ActivityRegistration;
import com.shequ.entity.User;
import com.shequ.repository.ActivityRegistrationRepository;
import com.shequ.repository.ActivityRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityRegistrationManagementService {

    @Autowired
    private ActivityRegistrationRepository registrationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取活动报名列表（分页和条件查询）
     */
    public PageResult<ActivityRegistrationManagementDTO> getActivityRegistrations(
            int page, int size, Long activityId, String activityTitle, String userName, String status) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "registrationTime"));

        Specification<ActivityRegistration> spec = createSpecification(activityId, activityTitle, userName, status);

        Page<ActivityRegistration> pageResult = registrationRepository.findAll(spec, pageable);

        List<ActivityRegistrationManagementDTO> dtoList = pageResult.getContent().stream()
                .map(this::convertToManagementDTO)
                .collect(Collectors.toList());

        return new PageResult<>(dtoList, pageResult.getTotalElements(), page, size);
    }

    /**
     * 获取特定活动的报名列表
     */
    public PageResult<ActivityRegistrationManagementDTO> getRegistrationsByActivity(
            Long activityId, int page, int size, String userName, String status) {

        // 验证活动是否存在
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "registrationTime"));

        Specification<ActivityRegistration> spec = createSpecification(activityId, null, userName, status);

        Page<ActivityRegistration> pageResult = registrationRepository.findAll(spec, pageable);

        List<ActivityRegistrationManagementDTO> dtoList = pageResult.getContent().stream()
                .map(this::convertToManagementDTO)
                .collect(Collectors.toList());

        return new PageResult<>(dtoList, pageResult.getTotalElements(), page, size);
    }

    /**
     * 获取报名详情
     */
    public ActivityRegistrationManagementDTO getRegistrationDetail(Long registrationId) {
        ActivityRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("The registration record does not exist"));

        return convertToManagementDTO(registration);
    }

    /**
     * 更新报名状态
     */
    @Transactional
    public ActivityRegistrationManagementDTO updateRegistrationStatus(Long registrationId, String status) {
        ActivityRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("The registration record does not exist"));

        try {
            ActivityRegistration.Status statusEnum = ActivityRegistration.Status.valueOf(status);
            ActivityRegistration.Status oldStatus = registration.getStatus();

            registration.setStatus(statusEnum);
            registration.setUpdatedAt(LocalDateTime.now());

            ActivityRegistration savedRegistration = registrationRepository.save(registration);

            // 如果状态发生了变化，更新活动的参与人数
            if (oldStatus != statusEnum) {
                updateActivityParticipantCount(registration.getActivityId());
            }

            return convertToManagementDTO(savedRegistration);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid registration status：" + status);
        }
    }

    /**
     * 批量更新报名状态
     */
    @Transactional
    public void batchUpdateRegistrationStatus(List<Long> registrationIds, String status) {
        if (registrationIds == null || registrationIds.isEmpty()) {
            throw new RuntimeException("The list of registration IDs cannot be empty");
        }

        try {
            ActivityRegistration.Status statusEnum = ActivityRegistration.Status.valueOf(status);

            List<ActivityRegistration> registrations = registrationRepository.findAllById(registrationIds);

            if (registrations.size() != registrationIds.size()) {
                throw new RuntimeException("Some registration records do not exist");
            }

            // 记录需要更新参与人数的活动
            List<Long> affectedActivityIds = new ArrayList<>();

            for (ActivityRegistration registration : registrations) {
                if (registration.getStatus() != statusEnum) {
                    registration.setStatus(statusEnum);
                    registration.setUpdatedAt(LocalDateTime.now());

                    if (!affectedActivityIds.contains(registration.getActivityId())) {
                        affectedActivityIds.add(registration.getActivityId());
                    }
                }
            }

            registrationRepository.saveAll(registrations);

            // 更新受影响活动的参与人数
            for (Long activityId : affectedActivityIds) {
                updateActivityParticipantCount(activityId);
            }

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid registration status：" + status);
        }
    }

    /**
     * 取消用户报名（管理员操作）
     */
    @Transactional
    public void cancelRegistrationByAdmin(Long registrationId) {
        ActivityRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("The registration record does not exist"));

        if (registration.getStatus() == ActivityRegistration.Status.cancelled) {
            throw new RuntimeException("The registration is already in a cancelled state");
        }

        registration.setStatus(ActivityRegistration.Status.cancelled);
        registration.setUpdatedAt(LocalDateTime.now());

        registrationRepository.save(registration);

        // 更新活动参与人数
        updateActivityParticipantCount(registration.getActivityId());
    }

    /**
     * 批量取消报名
     */
    @Transactional
    public void batchCancelRegistrations(List<Long> registrationIds) {
        if (registrationIds == null || registrationIds.isEmpty()) {
            throw new RuntimeException("The list of registration IDs cannot be empty");
        }

        List<ActivityRegistration> registrations = registrationRepository.findAllById(registrationIds);

        if (registrations.size() != registrationIds.size()) {
            throw new RuntimeException("Some registration records do not exist");
        }

        List<Long> affectedActivityIds = new ArrayList<>();

        for (ActivityRegistration registration : registrations) {
            if (registration.getStatus() != ActivityRegistration.Status.cancelled) {
                registration.setStatus(ActivityRegistration.Status.cancelled);
                registration.setUpdatedAt(LocalDateTime.now());

                if (!affectedActivityIds.contains(registration.getActivityId())) {
                    affectedActivityIds.add(registration.getActivityId());
                }
            }
        }

        registrationRepository.saveAll(registrations);

        // 更新受影响活动的参与人数
        for (Long activityId : affectedActivityIds) {
            updateActivityParticipantCount(activityId);
        }
    }

    /**
     * 手动添加报名记录
     */
    @Transactional
    public ActivityRegistrationManagementDTO manualAddRegistration(Long activityId, Long userId, String notes) {
        // 验证活动存在
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        // 验证用户存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        // 检查是否已经报名
        Optional<ActivityRegistration> existingRegistration = registrationRepository
                .findByActivityIdAndUserId(activityId, userId);

        if (existingRegistration.isPresent()) {
            throw new RuntimeException("The user has already registered for this activity");
        }

        // 检查名额限制
        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
            long currentRegistrations = registrationRepository.countByActivityIdAndStatus(
                    activityId, ActivityRegistration.Status.registered);
            if (currentRegistrations >= activity.getMaxParticipants()) {
                throw new RuntimeException("The activity quota is full");
            }
        }

        // 创建报名记录
        ActivityRegistration registration = new ActivityRegistration(activityId, userId, notes);
        ActivityRegistration savedRegistration = registrationRepository.save(registration);

        // 更新活动参与人数
        updateActivityParticipantCount(activityId);

        return convertToManagementDTO(savedRegistration);
    }

    /**
     * 导出报名数据
     */
    public String exportRegistrations(Long activityId, String activityTitle, String userName, String status) {
        try {
            Specification<ActivityRegistration> spec = createSpecification(activityId, activityTitle, userName, status);
            List<ActivityRegistration> registrations = registrationRepository.findAll(spec,
                    Sort.by(Sort.Direction.DESC, "registrationTime"));

            // 生成CSV数据
            StringBuilder csvData = new StringBuilder();
            csvData.append("Registration ID, Activity ID, Activity Title, User ID, User Nickname, User Email, Registration Time, Status, Remarks\n");

            for (ActivityRegistration registration : registrations) {
                ActivityRegistrationManagementDTO dto = convertToManagementDTO(registration);
                csvData.append(String.format("%d,%d,%s,%d,%s,%s,%s,%s,%s\n",
                        dto.getRegistrationId(),
                        dto.getActivityId(),
                        escapeCsv(dto.getActivityTitle()),
                        dto.getUserId(),
                        escapeCsv(dto.getUserNickname()),
                        escapeCsv(dto.getUserEmail()),
                        dto.getRegistrationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        dto.getRegistrationStatus(),
                        escapeCsv(dto.getRegistrationNotes())
                ));
            }


            String fileName = "registrations_export_" + System.currentTimeMillis() + ".csv";
            return "/api/admin/files/download/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("导出数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取报名统计信息
     */
    public RegistrationStatistics getRegistrationStatistics(Long activityId) {
        Specification<ActivityRegistration> spec;

        if (activityId != null) {
            spec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("activityId"), activityId);
        } else {
            spec = null;
        }

        List<ActivityRegistration> allRegistrations = registrationRepository.findAll(spec);

        long totalRegistrations = allRegistrations.size();
        long registeredCount = allRegistrations.stream()
                .mapToLong(r -> r.getStatus() == ActivityRegistration.Status.registered ? 1 : 0)
                .sum();
        long cancelledCount = allRegistrations.stream()
                .mapToLong(r -> r.getStatus() == ActivityRegistration.Status.cancelled ? 1 : 0)
                .sum();
        long attendedCount = allRegistrations.stream()
                .mapToLong(r -> r.getStatus() == ActivityRegistration.Status.attended ? 1 : 0)
                .sum();

        return new RegistrationStatistics(totalRegistrations, registeredCount, cancelledCount, attendedCount);
    }

    /**
     * 更新活动参与人数
     */
    @Transactional
    public void updateActivityParticipantCount(Long activityId) {
        long currentCount = registrationRepository.countByActivityIdAndStatus(
                activityId, ActivityRegistration.Status.registered);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("活动不存在"));

        activity.setCurrentParticipants((int) currentCount);
        activity.setUpdatedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }

    /**
     * 创建查询条件
     */
    private Specification<ActivityRegistration> createSpecification(
            Long activityId, String activityTitle, String userName, String status) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 活动ID条件
            if (activityId != null) {
                predicates.add(criteriaBuilder.equal(root.get("activityId"), activityId));
            }

            // 活动标题条件
            if (activityTitle != null && !activityTitle.trim().isEmpty()) {
                Join<ActivityRegistration, Activity> activityJoin = root.join("activity", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(activityJoin.get("title")),
                        "%" + activityTitle.toLowerCase() + "%"));
            }

            // 用户名条件
            if (userName != null && !userName.trim().isEmpty()) {
                Join<ActivityRegistration, User> userJoin = root.join("user", JoinType.LEFT);
                Predicate nicknamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(userJoin.get("nickname")),
                        "%" + userName.toLowerCase() + "%");
                Predicate usernamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(userJoin.get("username")),
                        "%" + userName.toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(nicknamePredicate, usernamePredicate));
            }

            // 状态条件
            if (status != null && !status.trim().isEmpty()) {
                try {
                    ActivityRegistration.Status statusEnum = ActivityRegistration.Status.valueOf(status);
                    predicates.add(criteriaBuilder.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效状态
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 转换为管理DTO
     */
    private ActivityRegistrationManagementDTO convertToManagementDTO(ActivityRegistration registration) {
        ActivityRegistrationManagementDTO dto = new ActivityRegistrationManagementDTO();

        dto.setRegistrationId(registration.getId());
        dto.setActivityId(registration.getActivityId());
        dto.setUserId(registration.getUserId());
        dto.setRegistrationTime(registration.getRegistrationTime());
        dto.setRegistrationStatus(registration.getStatus());
        dto.setRegistrationNotes(registration.getNotes());
        dto.setCreatedAt(registration.getCreatedAt());
        dto.setUpdatedAt(registration.getUpdatedAt());

        // 获取活动信息
        Optional<Activity> activityOpt = activityRepository.findById(registration.getActivityId());
        if (activityOpt.isPresent()) {
            Activity activity = activityOpt.get();
            dto.setActivityTitle(activity.getTitle());
            dto.setActivityDescription(activity.getDescription());
            dto.setActivityLocation(activity.getLocation());
            dto.setActivityStartTime(activity.getStartTime());
            dto.setActivityEndTime(activity.getEndTime());
            dto.setActivityFee(activity.getFee());
            dto.setActivityCoverImage(activity.getCoverImage());
            dto.setActivityStatus(activity.getStatus().name());
            dto.setActivityMaxParticipants(activity.getMaxParticipants());
            dto.setActivityCurrentParticipants(activity.getCurrentParticipants());
        }

        // 获取用户信息
        Optional<User> userOpt = userRepository.findById(registration.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            dto.setUserNickname(user.getNickname());
            dto.setUserEmail(user.getEmail());
            dto.setUserPhone(user.getPhone());
            dto.setUserAvatar(user.getAvatar());
        }

        // 计算相关状态
        LocalDateTime now = LocalDateTime.now();
        if (dto.getActivityStartTime() != null) {
            dto.setCanCancel(dto.getActivityStartTime().isAfter(now) &&
                    dto.getRegistrationStatus() == ActivityRegistration.Status.registered);

            dto.setCanAttend(dto.getActivityStartTime().isBefore(now) &&
                    dto.getActivityEndTime() != null &&
                    dto.getActivityEndTime().isAfter(now) &&
                    dto.getRegistrationStatus() == ActivityRegistration.Status.registered);
        }

        return dto;
    }

    /**
     * 转义CSV字段中的特殊字符
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}