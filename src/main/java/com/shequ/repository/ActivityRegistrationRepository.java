package com.shequ.repository;

import com.shequ.entity.ActivityRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Long>, JpaSpecificationExecutor<ActivityRegistration> {

    // 查找用户在特定活动的报名记录
    Optional<ActivityRegistration> findByActivityIdAndUserId(Long activityId, Long userId);

    // 查找用户在特定活动的有效报名记录（未取消的）
    Optional<ActivityRegistration> findByActivityIdAndUserIdAndStatus(Long activityId, Long userId, ActivityRegistration.Status status);

    // 检查用户是否已报名某活动（未取消的）
    boolean existsByActivityIdAndUserIdAndStatus(Long activityId, Long userId, ActivityRegistration.Status status);

    // 获取活动的所有报名记录
    List<ActivityRegistration> findByActivityId(Long activityId);

    // 获取活动的有效报名记录（未取消的）
    List<ActivityRegistration> findByActivityIdAndStatus(Long activityId, ActivityRegistration.Status status);

    // 分页获取活动的报名记录
    Page<ActivityRegistration> findByActivityId(Long activityId, Pageable pageable);

    // 获取用户的所有报名记录
    List<ActivityRegistration> findByUserId(Long userId);

    // 分页获取用户的报名记录
    Page<ActivityRegistration> findByUserId(Long userId, Pageable pageable);

    // 获取用户的有效报名记录
    List<ActivityRegistration> findByUserIdAndStatus(Long userId, ActivityRegistration.Status status);

    // 分页获取用户特定状态的报名记录
    Page<ActivityRegistration> findByUserIdAndStatus(Long userId, ActivityRegistration.Status status, Pageable pageable);

    // 统计活动的报名人数
    long countByActivityIdAndStatus(Long activityId, ActivityRegistration.Status status);

    // 统计用户的报名数量
    long countByUserIdAndStatus(Long userId, ActivityRegistration.Status status);

    // 查询活动的报名用户信息
    @Query("SELECT ar FROM ActivityRegistration ar WHERE ar.activityId = :activityId AND ar.status = :status ORDER BY ar.registrationTime ASC")
    List<ActivityRegistration> findRegisteredUsersByActivity(@Param("activityId") Long activityId, @Param("status") ActivityRegistration.Status status);

    // 查询用户参与的活动
    @Query("SELECT ar FROM ActivityRegistration ar WHERE ar.userId = :userId AND ar.status = :status ORDER BY ar.registrationTime DESC")
    List<ActivityRegistration> findUserRegisteredActivities(@Param("userId") Long userId, @Param("status") ActivityRegistration.Status status);

    // 删除用户在特定活动的报名记录
    void deleteByActivityIdAndUserId(Long activityId, Long userId);

    // 统计用户总报名数量
    long countByUserId(Long userId);

    // 自定义查询方法：根据活动标题模糊查询报名记录
    @Query("SELECT ar FROM ActivityRegistration ar " +
            "JOIN Activity a ON ar.activityId = a.id " +
            "WHERE (:activityTitle IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :activityTitle, '%'))) " +
            "AND (:status IS NULL OR ar.status = :status) " +
            "ORDER BY ar.registrationTime DESC")
    Page<ActivityRegistration> findByActivityTitleContainingAndStatus(
            @Param("activityTitle") String activityTitle,
            @Param("status") ActivityRegistration.Status status,
            Pageable pageable);

    // 自定义查询方法：根据用户名模糊查询报名记录
    @Query("SELECT ar FROM ActivityRegistration ar " +
            "JOIN User u ON ar.userId = u.id " +
            "WHERE (:userName IS NULL OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :userName, '%')) " +
            "       OR LOWER(u.username) LIKE LOWER(CONCAT('%', :userName, '%'))) " +
            "AND (:status IS NULL OR ar.status = :status) " +
            "ORDER BY ar.registrationTime DESC")
    Page<ActivityRegistration> findByUserNameContainingAndStatus(
            @Param("userName") String userName,
            @Param("status") ActivityRegistration.Status status,
            Pageable pageable);

    // 综合查询方法：支持多条件查询
    @Query("SELECT ar FROM ActivityRegistration ar " +
            "LEFT JOIN Activity a ON ar.activityId = a.id " +
            "LEFT JOIN User u ON ar.userId = u.id " +
            "WHERE (:activityId IS NULL OR ar.activityId = :activityId) " +
            "AND (:activityTitle IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :activityTitle, '%'))) " +
            "AND (:userName IS NULL OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :userName, '%')) " +
            "     OR LOWER(u.username) LIKE LOWER(CONCAT('%', :userName, '%'))) " +
            "AND (:status IS NULL OR ar.status = :status) " +
            "ORDER BY ar.registrationTime DESC")
    Page<ActivityRegistration> findByMultipleConditions(
            @Param("activityId") Long activityId,
            @Param("activityTitle") String activityTitle,
            @Param("userName") String userName,
            @Param("status") ActivityRegistration.Status status,
            Pageable pageable);

    // 统计查询：按状态统计报名数量
    @Query("SELECT ar.status, COUNT(ar) FROM ActivityRegistration ar GROUP BY ar.status")
    List<Object[]> countByStatusGrouped();

    // 统计查询：按活动统计报名数量
    @Query("SELECT ar.activityId, a.title, COUNT(ar) FROM ActivityRegistration ar " +
            "LEFT JOIN Activity a ON ar.activityId = a.id " +
            "WHERE (:activityId IS NULL OR ar.activityId = :activityId) " +
            "GROUP BY ar.activityId, a.title " +
            "ORDER BY COUNT(ar) DESC")
    List<Object[]> countByActivityGrouped(@Param("activityId") Long activityId);

    // 查询指定时间范围内的报名记录
    @Query("SELECT ar FROM ActivityRegistration ar " +
            "WHERE ar.registrationTime >= :startTime " +
            "AND ar.registrationTime <= :endTime " +
            "ORDER BY ar.registrationTime DESC")
    List<ActivityRegistration> findByRegistrationTimeBetween(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime);

    // 查询即将开始的活动的报名记录
    @Query("SELECT ar FROM ActivityRegistration ar " +
            "JOIN Activity a ON ar.activityId = a.id " +
            "WHERE a.startTime > :currentTime " +
            "AND a.startTime <= :futureTime " +
            "AND ar.status = :status " +
            "ORDER BY a.startTime ASC")
    List<ActivityRegistration> findUpcomingActivityRegistrations(
            @Param("currentTime") java.time.LocalDateTime currentTime,
            @Param("futureTime") java.time.LocalDateTime futureTime,
            @Param("status") ActivityRegistration.Status status);

    // 查询用户最近的报名记录
    @Query("SELECT ar FROM ActivityRegistration ar " +
            "WHERE ar.userId = :userId " +
            "ORDER BY ar.registrationTime DESC")
    List<ActivityRegistration> findRecentRegistrationsByUser(
            @Param("userId") Long userId,
            Pageable pageable);

    // 统计用户在指定时间范围内的报名数量
    @Query("SELECT COUNT(ar) FROM ActivityRegistration ar " +
            "WHERE ar.userId = :userId " +
            "AND ar.registrationTime >= :startTime " +
            "AND ar.registrationTime <= :endTime")
    long countUserRegistrationsInPeriod(
            @Param("userId") Long userId,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime);

    // 查询热门活动（报名人数最多的活动）
    @Query("SELECT ar.activityId, a.title, COUNT(ar) as registrationCount " +
            "FROM ActivityRegistration ar " +
            "JOIN Activity a ON ar.activityId = a.id " +
            "WHERE ar.status = 'registered' " +
            "GROUP BY ar.activityId, a.title " +
            "ORDER BY registrationCount DESC")
    List<Object[]> findPopularActivities(Pageable pageable);
}