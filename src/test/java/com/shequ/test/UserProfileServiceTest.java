package com.shequ.test;

import com.shequ.dto.UserResponse;
import com.shequ.entity.User;
import com.shequ.repository.UserRepository;
import com.shequ.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {  // 修改类名为实际的服务类名

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;  // 修改为实际的服务类

    @Test
    void getUserProfile_Success() {
        // 准备测试数据
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testUser");

        // 设置mock行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // 执行测试
        UserResponse result = userProfileService.getUserProfile(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUserProfile_UserNotFound() {
        // 准备测试数据
        Long nonExistUserId = 999L;

        // 设置mock行为返回空
        when(userRepository.findById(nonExistUserId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userProfileService.getUserProfile(nonExistUserId));

        assertEquals("用户不存在", exception.getMessage());
    }
}