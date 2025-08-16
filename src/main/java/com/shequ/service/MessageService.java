package com.shequ.service;

import com.shequ.dto.MessageDTO;
import com.shequ.dto.MessageStatsDTO;
import com.shequ.dto.PageResult;
import com.shequ.entity.SystemMessage;
import com.shequ.entity.User;
import com.shequ.entity.UserMessageStatus;
import com.shequ.repository.SystemMessageRepository;
import com.shequ.repository.UserMessageStatusRepository;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private SystemMessageRepository messageRepository;

    @Autowired
    private UserMessageStatusRepository messageStatusRepository;

    @Autowired
    private UserRepository userRepository;

    public PageResult<MessageDTO> getMessages(int page, int size, String title, String type, String targetType, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        SystemMessage.MessageType typeEnum = null;
        if (type != null && !type.isEmpty()) {
            try {
                typeEnum = SystemMessage.MessageType.valueOf(type);
            } catch (IllegalArgumentException e) {
                // 忽略无效的类型值
            }
        }

        SystemMessage.TargetType targetTypeEnum = null;
        if (targetType != null && !targetType.isEmpty()) {
            try {
                targetTypeEnum = SystemMessage.TargetType.valueOf(targetType);
            } catch (IllegalArgumentException e) {
                // 忽略无效的目标类型值
            }
        }

        Page<SystemMessage> pageResult = messageRepository.findByConditions(title, typeEnum, targetTypeEnum, status, pageable);

        List<MessageDTO> messageDTOs = pageResult.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(messageDTOs, pageResult.getTotalElements(), page, size);
    }

    public MessageDTO getMessageById(Long id) {
        SystemMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The message does not exist"));
        return convertToDTO(message);
    }

    public PageResult<MessageDTO> getUserMessages(Long userId, boolean isAdmin, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SystemMessage> pageResult = messageRepository.findUserMessagesPage(userId, isAdmin, pageable);

        List<MessageDTO> messageDTOs = pageResult.getContent().stream()
                .map(message -> {
                    MessageDTO dto = convertToDTO(message);
                    // 设置用户的阅读状态
                    Optional<UserMessageStatus> status = messageStatusRepository.findByUserIdAndMessageId(userId, message.getId());
                    if (status.isPresent()) {
                        dto.setIsRead(status.get().getIsRead());
                        dto.setReadTime(status.get().getReadTime());
                    } else {
                        dto.setIsRead(0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageResult<>(messageDTOs, pageResult.getTotalElements(), page, size);
    }

    @Transactional
    public MessageDTO createMessage(MessageDTO messageDTO, Long senderId) {
        SystemMessage message = new SystemMessage();
        copyDTOToEntity(messageDTO, message);
        message.setSenderId(senderId);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        SystemMessage savedMessage = messageRepository.save(message);

        // 为目标用户创建消息状态记录
        createMessageStatusForTargetUsers(savedMessage);

        return convertToDTO(savedMessage);
    }

    @Transactional
    public MessageDTO updateMessage(Long id, MessageDTO messageDTO) {
        SystemMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The message does not exist"));

        copyDTOToEntity(messageDTO, message);
        message.setUpdatedAt(LocalDateTime.now());

        SystemMessage savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    @Transactional
    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new RuntimeException("The message does not exist");
        }

        // 删除相关的用户消息状态记录
        List<UserMessageStatus> statusList = messageStatusRepository.findByMessageId(id);
        messageStatusRepository.deleteAll(statusList);

        // 删除消息
        messageRepository.deleteById(id);
    }

    @Transactional
    public MessageDTO updateMessageStatus(Long id, Integer status) {
        SystemMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The message does not exist"));

        message.setStatus(status);
        message.setUpdatedAt(LocalDateTime.now());

        SystemMessage savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        Optional<UserMessageStatus> existingStatus = messageStatusRepository.findByUserIdAndMessageId(userId, messageId);

        if (existingStatus.isPresent()) {
            UserMessageStatus status = existingStatus.get();
            if (status.getIsRead() == 0) {
                status.setIsRead(1);
                status.setReadTime(LocalDateTime.now());
                status.setUpdatedAt(LocalDateTime.now());
                messageStatusRepository.save(status);
            }
        } else {
            // 如果不存在状态记录，创建一个新的并标记为已读
            UserMessageStatus status = new UserMessageStatus(userId, messageId);
            status.setIsRead(1);
            status.setReadTime(LocalDateTime.now());
            messageStatusRepository.save(status);
        }
    }

    @Transactional
    public void markAllAsRead(Long userId, boolean isAdmin) {
        // 获取用户的所有消息
        List<SystemMessage> userMessages = messageRepository.findUserMessages(userId, isAdmin);

        List<UserMessageStatus> statusesToUpdate = new ArrayList<>();
        List<UserMessageStatus> statusesToCreate = new ArrayList<>();

        for (SystemMessage message : userMessages) {
            Optional<UserMessageStatus> existingStatus = messageStatusRepository.findByUserIdAndMessageId(userId, message.getId());

            if (existingStatus.isPresent()) {
                UserMessageStatus status = existingStatus.get();
                if (status.getIsRead() == 0) {
                    status.setIsRead(1);
                    status.setReadTime(LocalDateTime.now());
                    status.setUpdatedAt(LocalDateTime.now());
                    statusesToUpdate.add(status);
                }
            } else {
                // 如果不存在状态记录，创建一个新的并标记为已读
                UserMessageStatus status = new UserMessageStatus(userId, message.getId());
                status.setIsRead(1);
                status.setReadTime(LocalDateTime.now());
                statusesToCreate.add(status);
            }
        }

        // 批量更新和创建
        if (!statusesToUpdate.isEmpty()) {
            messageStatusRepository.saveAll(statusesToUpdate);
        }
        if (!statusesToCreate.isEmpty()) {
            messageStatusRepository.saveAll(statusesToCreate);
        }
    }

    @Transactional
    public void markAsDeleted(Long messageId, Long userId) {
        Optional<UserMessageStatus> existingStatus = messageStatusRepository.findByUserIdAndMessageId(userId, messageId);

        if (existingStatus.isPresent()) {
            UserMessageStatus status = existingStatus.get();
            status.setIsDeleted(1);
            status.setUpdatedAt(LocalDateTime.now());
            messageStatusRepository.save(status);
        } else {
            // 如果不存在状态记录，创建一个新的并标记为已删除
            UserMessageStatus status = new UserMessageStatus(userId, messageId);
            status.setIsDeleted(1);
            messageStatusRepository.save(status);
        }
    }

    public MessageStatsDTO getMessageStats(Long messageId) {
        SystemMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("The message does not exist"));

        // 计算目标用户总数
        long totalUsers = calculateTargetUserCount(message);

        // 计算已读用户数
        long readCount = messageStatusRepository.countReadByMessageId(messageId);

        MessageStatsDTO stats = new MessageStatsDTO(messageId, message.getTitle(), totalUsers, readCount);

        // 获取阅读详情
        List<MessageStatsDTO.ReadDetailDTO> readDetails = getReadDetails(messageId, message);
        stats.setReadDetails(readDetails);

        return stats;
    }

    public long getUnreadMessageCount(Long userId, boolean isAdmin) {
        // 获取用户的所有消息
        List<SystemMessage> userMessages = messageRepository.findUserMessages(userId, isAdmin);

        long unreadCount = 0;
        for (SystemMessage message : userMessages) {
            Optional<UserMessageStatus> status = messageStatusRepository.findByUserIdAndMessageId(userId, message.getId());
            if (!status.isPresent() || (status.get().getIsRead() == 0 && status.get().getIsDeleted() == 0)) {
                unreadCount++;
            }
        }

        return unreadCount;
    }

    private MessageDTO convertToDTO(SystemMessage message) {
        MessageDTO dto = new MessageDTO(message);

        // 设置发送者名称
        if (message.getSenderId() != null) {
            Optional<User> sender = userRepository.findById(message.getSenderId());
            sender.ifPresent(u -> dto.setSenderName(u.getNickname()));
        }

        return dto;
    }

    private void copyDTOToEntity(MessageDTO dto, SystemMessage entity) {
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());

        if (dto.getType() != null && !dto.getType().isEmpty()) {
            try {
                entity.setType(SystemMessage.MessageType.valueOf(dto.getType()));
            } catch (IllegalArgumentException e) {
                entity.setType(SystemMessage.MessageType.notice);
            }
        }

        if (dto.getTargetType() != null && !dto.getTargetType().isEmpty()) {
            try {
                entity.setTargetType(SystemMessage.TargetType.valueOf(dto.getTargetType()));
            } catch (IllegalArgumentException e) {
                entity.setTargetType(SystemMessage.TargetType.all);
            }
        }

        entity.setTargetUserId(dto.getTargetUserId());
        entity.setRelatedActivityId(dto.getRelatedActivityId());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }

    private void createMessageStatusForTargetUsers(SystemMessage message) {
        List<User> targetUsers = getTargetUsers(message);

        List<UserMessageStatus> statusList = new ArrayList<>();
        for (User user : targetUsers) {
            UserMessageStatus status = new UserMessageStatus(user.getId(), message.getId());
            statusList.add(status);
        }

        if (!statusList.isEmpty()) {
            messageStatusRepository.saveAll(statusList);
        }
    }

    private List<User> getTargetUsers(SystemMessage message) {
        List<User> targetUsers = new ArrayList<>();

        switch (message.getTargetType()) {
            case all:
                targetUsers = userRepository.findAll();
                break;
            case user:
                if (message.getTargetUserId() != null) {
                    Optional<User> user = userRepository.findById(message.getTargetUserId());
                    user.ifPresent(targetUsers::add);
                } else {
                    // 获取所有普通用户
                    Page<User> users = userRepository.findByRole(User.Role.user, PageRequest.of(0, Integer.MAX_VALUE));
                    targetUsers = users.getContent();
                }
                break;
            case admin:
                // 获取所有管理员用户
                Page<User> admins = userRepository.findByRole(User.Role.admin, PageRequest.of(0, Integer.MAX_VALUE));
                targetUsers = admins.getContent();
                break;
        }

        return targetUsers;
    }

    private long calculateTargetUserCount(SystemMessage message) {
        return getTargetUsers(message).size();
    }

    private List<MessageStatsDTO.ReadDetailDTO> getReadDetails(Long messageId, SystemMessage message) {
        List<User> targetUsers = getTargetUsers(message);
        List<MessageStatsDTO.ReadDetailDTO> details = new ArrayList<>();

        for (User user : targetUsers) {
            Optional<UserMessageStatus> status = messageStatusRepository.findByUserIdAndMessageId(user.getId(), messageId);

            MessageStatsDTO.ReadDetailDTO detail = new MessageStatsDTO.ReadDetailDTO();
            detail.setUserId(user.getId());
            detail.setUserNickname(user.getNickname());

            if (status.isPresent()) {
                detail.setIsRead(status.get().getIsRead());
                detail.setReadTime(status.get().getReadTime());
            } else {
                detail.setIsRead(0);
                detail.setReadTime(null);
            }

            details.add(detail);
        }

        return details;
    }
}