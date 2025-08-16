package com.shequ.test;

import com.shequ.entity.ActivityCategory;
import com.shequ.repository.ActivityCategoryRepository;
import com.shequ.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ActivityCategoryServiceTest {

    @Mock
    private ActivityCategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService activityCategoryService;

    private ActivityCategory createCategory(Integer id, String name) {
        ActivityCategory category = new ActivityCategory();
        category.setId(id);
        category.setName(name);
        category.setDescription(name + "分类描述");
        category.setIcon("/icons/" + name + ".png");
        category.setSortOrder(id);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    @Test
    void getAllCategories_Success() {
        // 准备测试数据
        ActivityCategory sport = createCategory(1, "运动");
        ActivityCategory music = createCategory(2, "音乐");
        List<ActivityCategory> mockCategories = Arrays.asList(sport, music);

        // 设置mock行为
        doReturn(mockCategories).when(categoryRepository).findAll();

        // 执行测试
        List<ActivityCategory> result = activityCategoryService.getAllCategories();

        // 验证结果
        assertEquals(2, result.size());
        assertEquals("运动", result.get(0).getName());
        assertEquals("/icons/运动.png", result.get(0).getIcon());
        assertEquals(1, result.get(0).getSortOrder());
    }

    @Test
    void getAllCategories_Empty() {
        // 设置mock行为返回空列表
        doReturn(Collections.emptyList()).when(categoryRepository).findAll();

        // 执行测试
        List<ActivityCategory> result = activityCategoryService.getAllCategories();

        // 验证结果
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCategories_Failure() {
        // 设置mock行为抛出异常
        doThrow(new RuntimeException("数据库连接失败")).when(categoryRepository).findAll();

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityCategoryService.getAllCategories());

        assertEquals("数据库连接失败", exception.getMessage());
    }
}