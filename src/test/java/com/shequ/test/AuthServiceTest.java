package com.shequ.test;

import com.shequ.dto.RegisterRequest;
import com.shequ.dto.UserResponse;
import com.shequ.entity.User;
import com.shequ.repository.UserRepository;
import com.shequ.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest createRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Test@1234");
        request.setEmail("test@example.com");
        request.setPhone("13800138000");
        return request;
    }

    @Test
    void registerSuccess() {
        RegisterRequest request = createRequest();
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(new User());

        UserResponse response = authService.register(request);

        assertNotNull(response);
        verify(userRepository).save(any());
    }

    @Test
    void registerFail_UsernameExists() {
        RegisterRequest request = createRequest();
        when(userRepository.existsByUsername(any())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void registerFail_EmailExists() {
        RegisterRequest request = createRequest();
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("邮箱已被注册", exception.getMessage());
    }

    @Test
    void registerFail_PhoneExists() {
        RegisterRequest request = createRequest();
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByPhone(any())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("手机号已被注册", exception.getMessage());
    }
}