package com.shequ.repository;

import com.shequ.entity.SystemMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemMessageRepository extends JpaRepository<SystemMessage, Long> {

    Page<SystemMessage> findByStatus(Integer status, Pageable pageable);

    Page<SystemMessage> findByType(SystemMessage.MessageType type, Pageable pageable);

    Page<SystemMessage> findByTargetType(SystemMessage.TargetType targetType, Pageable pageable);

    Page<SystemMessage> findBySenderId(Long senderId, Pageable pageable);

    @Query("SELECT m FROM SystemMessage m WHERE " +
            "(:title IS NULL OR m.title LIKE %:title%) AND " +
            "(:type IS NULL OR m.type = :type) AND " +
            "(:targetType IS NULL OR m.targetType = :targetType) AND " +
            "(:status IS NULL OR m.status = :status)")
    Page<SystemMessage> findByConditions(@Param("title") String title,
                                         @Param("type") SystemMessage.MessageType type,
                                         @Param("targetType") SystemMessage.TargetType targetType,
                                         @Param("status") Integer status,
                                         Pageable pageable);

    @Query("SELECT m FROM SystemMessage m WHERE m.status = 1 AND " +
            "(m.targetType = 'all' OR " +
            "(m.targetType = 'user' AND (m.targetUserId IS NULL OR m.targetUserId = :userId)) OR " +
            "(m.targetType = 'admin' AND :isAdmin = true)) " +
            "ORDER BY m.createdAt DESC")
    List<SystemMessage> findUserMessages(@Param("userId") Long userId, @Param("isAdmin") boolean isAdmin);

    @Query("SELECT m FROM SystemMessage m WHERE m.status = 1 AND " +
            "(m.targetType = 'all' OR " +
            "(m.targetType = 'user' AND (m.targetUserId IS NULL OR m.targetUserId = :userId)) OR " +
            "(m.targetType = 'admin' AND :isAdmin = true)) " +
            "ORDER BY m.createdAt DESC")
    Page<SystemMessage> findUserMessagesPage(@Param("userId") Long userId,
                                             @Param("isAdmin") boolean isAdmin,
                                             Pageable pageable);

    long countByStatus(Integer status);

    long countByType(SystemMessage.MessageType type);

    @Query("SELECT COUNT(m) FROM SystemMessage m WHERE m.createdAt >= :startDate")
    long countByCreatedAtAfter(@Param("startDate") LocalDateTime startDate);
}