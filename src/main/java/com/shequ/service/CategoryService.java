package com.shequ.service;

import com.shequ.entity.ActivityCategory;
import com.shequ.repository.ActivityCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private ActivityCategoryRepository categoryRepository;

    public List<ActivityCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<ActivityCategory> getEnabledCategories() {
        return categoryRepository.findByStatusOrderBySortOrder(1);
    }

    public ActivityCategory getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The category does not exist"));
    }

    @Transactional
    public ActivityCategory createCategory(ActivityCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("The category name already exists");
        }

        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    @Transactional
    public ActivityCategory updateCategory(Integer id, ActivityCategory category) {
        ActivityCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The category does not exist"));

        if (categoryRepository.existsByNameAndIdNot(category.getName(), id)) {
            throw new RuntimeException("The category name already exists");
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setSortOrder(category.getSortOrder());
        existingCategory.setStatus(category.getStatus());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("The category does not exist");
        }
        categoryRepository.deleteById(id);
    }
}


