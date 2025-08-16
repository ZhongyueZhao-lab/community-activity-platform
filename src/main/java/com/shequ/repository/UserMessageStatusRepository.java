package com.shequ.repository;

import com.shequ.entity.UserMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMessageStatusRepository extends JpaRepository<UserMessageStatus, Long> {

    Optional<UserMessageStatus> findByUserIdAndMessageId(Long userId, Long messageId);

    List<UserMessageStatus> findByUserId(Long userId);

    List<UserMessageStatus> findByMessageId(Long messageId);

    List<UserMessageStatus> findByUserIdAndIsRead(Long userId, Integer isRead);

    List<UserMessageStatus> findByUserIdAndIsDeleted(Long userId, Integer isDeleted);

    @Query("SELECT COUNT(ums) FROM UserMessageStatus ums WHERE ums.messageId = :messageId AND ums.isRead = 1")
    long countReadByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT COUNT(ums) FROM UserMessageStatus ums WHERE ums.messageId = :messageId")
    long countTotalByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT COUNT(ums) FROM UserMessageStatus ums WHERE ums.userId = :userId AND ums.isRead = 0 AND ums.isDeleted = 0")
    long countUnreadByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndMessageId(Long userId, Long messageId);

    void deleteByUserIdAndMessageId(Long userId, Long messageId);
}