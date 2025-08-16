package com.shequ.test;

import com.shequ.dto.ActivityDTO;
import com.shequ.dto.PageResult;
import com.shequ.entity.Activity;
import com.shequ.repository.ActivityRepository;
import com.shequ.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void getActivities_Success() {
        // 准备测试数据
        Page<Activity> mockPage = new PageImpl<>(Collections.singletonList(new Activity()));

        // 设置mock行为
        doReturn(mockPage).when(activityRepository).findByConditions(any(), any(), any(), any());

        // 执行测试
        PageResult<ActivityDTO> result = activityService.getActivities(1, 10, "测试", "published", 1);

        // 验证结果
        assertNotNull(result);
    }

    @Test
    void getActivities_Failure() {
        // 设置mock行为抛出异常
        doThrow(new RuntimeException("数据库错误")).when(activityRepository).findByConditions(any(), any(), any(), any());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.getActivities(1, 10, "测试", "published", 1));

        assertEquals("数据库错误", exception.getMessage());
    }
}