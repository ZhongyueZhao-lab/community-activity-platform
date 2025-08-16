package com.shequ.repository;

import com.shequ.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findByStatus(Activity.Status status, Pageable pageable);

    Page<Activity> findByCategoryId(Integer categoryId, Pageable pageable);

    Page<Activity> findByCreatorId(Long creatorId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE " +
            "(:title IS NULL OR a.title LIKE %:title%) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:categoryId IS NULL OR a.categoryId = :categoryId)")
    Page<Activity> findByConditions(@Param("title") String title,
                                    @Param("status") Activity.Status status,
                                    @Param("categoryId") Integer categoryId,
                                    Pageable pageable);

    long countByStatus(Activity.Status status);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.createdAt >= :startDate")
    long countByCreatedAtAfter(@Param("startDate") java.time.LocalDateTime startDate);
}
