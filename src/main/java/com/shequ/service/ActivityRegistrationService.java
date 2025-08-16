package com.shequ.service;

import com.shequ.dto.ActivityDetailDTO;
import com.shequ.entity.Activity;
import com.shequ.entity.ActivityRegistration;
import com.shequ.entity.User;
import com.shequ.repository.ActivityRegistrationRepository;
import com.shequ.repository.ActivityRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityRegistrationService {

    @Autowired
    private ActivityRegistrationRepository registrationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ActivityRegistration registerForActivity(Long activityId, Long userId, String notes) {
        // 1. 检查活动是否存在
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        // 2. 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));

        // 3. 检查活动状态
        if (activity.getStatus() != Activity.Status.published) {
            throw new RuntimeException("The activity has not been published, so registration is not possible.");
        }

        // 4. 检查活动是否已结束
        LocalDateTime now = LocalDateTime.now();
        if (activity.getEndTime().isBefore(now)) {
            throw new RuntimeException("The activity has not been published, so registration is not possible.");
        }

        // 5. 检查报名时间
        if (activity.getRegistrationStartTime() != null && activity.getRegistrationStartTime().isAfter(now)) {
            throw new RuntimeException("Registration has not started yet");
        }
        if (activity.getRegistrationEndTime() != null && activity.getRegistrationEndTime().isBefore(now)) {
            throw new RuntimeException("Registration has been closed");
        }

        // 6. 检查是否已经有报名记录（任何状态）
        Optional<ActivityRegistration> existingRegistration = registrationRepository
                .findByActivityIdAndUserId(activityId, userId);

        if (existingRegistration.isPresent()) {
            ActivityRegistration registration = existingRegistration.get();

            // 如果已经是注册状态，抛出异常
            if (registration.getStatus() == ActivityRegistration.Status.registered) {
                throw new RuntimeException("You have already registered for this activity");
            }

            // 如果是取消或其他状态，重新激活报名
            registration.setStatus(ActivityRegistration.Status.registered);
            registration.setNotes(notes);
            registration.setRegistrationTime(LocalDateTime.now());
            registration.setUpdatedAt(LocalDateTime.now());

            // 7. 检查名额限制
            if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
                long currentRegistrations = registrationRepository.countByActivityIdAndStatus(
                        activityId, ActivityRegistration.Status.registered);
                if (currentRegistrations >= activity.getMaxParticipants()) {
                    throw new RuntimeException("The activity slots are full");
                }
            }

            ActivityRegistration savedRegistration = registrationRepository.save(registration);

            // 8. 更新活动当前参与人数
            updateActivityParticipantCount(activityId);

            return savedRegistration;
        } else {
            // 7. 检查名额限制（新用户报名）
            if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
                long currentRegistrations = registrationRepository.countByActivityIdAndStatus(
                        activityId, ActivityRegistration.Status.registered);
                if (currentRegistrations >= activity.getMaxParticipants()) {
                    throw new RuntimeException("The activity slots are full");
                }
            }

            // 8. 创建新的报名记录
            ActivityRegistration registration = new ActivityRegistration(activityId, userId, notes);
            ActivityRegistration savedRegistration = registrationRepository.save(registration);

            // 9. 更新活动当前参与人数
            updateActivityParticipantCount(activityId);

            return savedRegistration;
        }
    }

    @Transactional
    public void cancelRegistration(Long activityId, Long userId) {
        // 1. 查找报名记录
        Optional<ActivityRegistration> registrationOpt = registrationRepository
                .findByActivityIdAndUserIdAndStatus(activityId, userId, ActivityRegistration.Status.registered);

        if (!registrationOpt.isPresent()) {
            throw new RuntimeException("No registration record was found");
        }

        ActivityRegistration registration = registrationOpt.get();

        // 2. 检查活动是否已开始
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        LocalDateTime now = LocalDateTime.now();
        if (activity.getStartTime().isBefore(now)) {
            throw new RuntimeException("The activity has started, and it is not possible to cancel the registration.");
        }

        // 3. 更新报名状态为已取消
        registration.setStatus(ActivityRegistration.Status.cancelled);
        registration.setUpdatedAt(LocalDateTime.now());
        registrationRepository.save(registration);

        // 4. 更新活动当前参与人数
        updateActivityParticipantCount(activityId);
    }

    @Transactional
    public void updateActivityParticipantCount(Long activityId) {
        long currentCount = registrationRepository.countByActivityIdAndStatus(
                activityId, ActivityRegistration.Status.registered);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("The activity does not exist"));

        activity.setCurrentParticipants((int) currentCount);
        activity.setUpdatedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }

    public boolean isUserRegistered(Long activityId, Long userId) {
        return registrationRepository.existsByActivityIdAndUserIdAndStatus(
                activityId, userId, ActivityRegistration.Status.registered);
    }

    public Optional<ActivityRegistration> getUserRegistration(Long activityId, Long userId) {
        return registrationRepository.findByActivityIdAndUserId(activityId, userId);
    }

    public List<ActivityDetailDTO.ParticipantDTO> getActivityParticipants(Long activityId) {
        List<ActivityRegistration> registrations = registrationRepository
                .findRegisteredUsersByActivity(activityId, ActivityRegistration.Status.registered);

        return registrations.stream()
                .map(registration -> {
                    Optional<User> userOpt = userRepository.findById(registration.getUserId());
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        return new ActivityDetailDTO.ParticipantDTO(
                                user.getId(),
                                user.getNickname(),
                                user.getAvatar(),
                                registration.getRegistrationTime(),
                                registration.getStatus()
                        );
                    }
                    return null;
                })
                .filter(participant -> participant != null)
                .collect(Collectors.toList());
    }

    public List<ActivityRegistration> getUserRegistrations(Long userId) {
        return registrationRepository.findByUserIdAndStatus(userId, ActivityRegistration.Status.registered);
    }

    public long getActivityRegistrationCount(Long activityId) {
        return registrationRepository.countByActivityIdAndStatus(activityId, ActivityRegistration.Status.registered);
    }

    @Transactional
    public void markAsAttended(Long activityId, Long userId) {
        Optional<ActivityRegistration> registrationOpt = registrationRepository
                .findByActivityIdAndUserIdAndStatus(activityId, userId, ActivityRegistration.Status.registered);

        if (!registrationOpt.isPresent()) {
            throw new RuntimeException("No valid registration record was found");
        }

        ActivityRegistration registration = registrationOpt.get();
        registration.setStatus(ActivityRegistration.Status.attended);
        registration.setUpdatedAt(LocalDateTime.now());
        registrationRepository.save(registration);
    }

    public String getRegistrationStatusReason(Activity activity, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 检查活动状态
        if (activity.getStatus() != Activity.Status.published) {
            return "The activity has not been published";
        }

        // 检查活动是否已结束
        if (activity.getEndTime().isBefore(now)) {
            return "The activity has ended";
        }

        // 检查报名时间
        if (activity.getRegistrationStartTime() != null && activity.getRegistrationStartTime().isAfter(now)) {
            return "Registration has not started yet";
        }
        if (activity.getRegistrationEndTime() != null && activity.getRegistrationEndTime().isBefore(now)) {
            return "Registration has been closed";
        }

        // 检查是否已经报名
        if (userId != null && isUserRegistered(activity.getId(), userId)) {
            return "You have already registered";
        }

        // 检查名额限制
        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
            long currentRegistrations = getActivityRegistrationCount(activity.getId());
            if (currentRegistrations >= activity.getMaxParticipants()) {
                return "The activity slots are full";
            }
        }

        return null; // 可以报名
    }
}