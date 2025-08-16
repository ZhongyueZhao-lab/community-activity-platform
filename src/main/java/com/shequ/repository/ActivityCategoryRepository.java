package com.shequ.repository;

import com.shequ.entity.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {

    List<ActivityCategory> findByStatusOrderBySortOrder(Integer status);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
