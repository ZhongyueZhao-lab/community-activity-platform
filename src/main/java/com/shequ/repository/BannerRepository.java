package com.shequ.repository;

import com.shequ.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByStatusOrderBySortOrderAsc(Integer status);

    Page<Banner> findByStatus(Integer status, Pageable pageable);

    Page<Banner> findByCreatorId(Long creatorId, Pageable pageable);

    @Query("SELECT b FROM Banner b WHERE " +
            "(:title IS NULL OR b.title LIKE %:title%) AND " +
            "(:status IS NULL OR b.status = :status)")
    Page<Banner> findByConditions(@Param("title") String title,
                                  @Param("status") Integer status,
                                  Pageable pageable);

    @Query("SELECT b FROM Banner b WHERE b.status = 1 AND " +
            "(b.startTime IS NULL OR b.startTime <= :now) AND " +
            "(b.endTime IS NULL OR b.endTime >= :now) " +
            "ORDER BY b.sortOrder ASC")
    List<Banner> findActiveBanners(@Param("now") LocalDateTime now);

    long countByStatus(Integer status);

    @Query("SELECT COUNT(b) FROM Banner b WHERE b.createdAt >= :startDate")
    long countByCreatedAtAfter(@Param("startDate") LocalDateTime startDate);
}