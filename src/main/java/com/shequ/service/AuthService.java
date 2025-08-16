package com.shequ.service;

import com.shequ.dto.*;
import com.shequ.entity.User;
import com.shequ.repository.UserRepository;
import com.shequ.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // 1. 查找用户
        User user = findUserByUsernameOrPhoneOrEmail(request.getUsername());
        if (user == null) {
            throw new RuntimeException("The user does not exist");
        }

        // 2. 验证密码
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("The password is incorrect");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("The account has been frozen");
        }

        // 4. 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(getClientIp(httpRequest));
        userRepository.save(user);

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // 6. 返回登录响应
        UserResponse userResponse = new UserResponse(user);
        return new LoginResponse(token, userResponse);
    }

    public UserResponse register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("The username already exists");
        }

        // 2. 检查手机号是否已存在
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("The mobile phone number has been registered");
            }
        }

        // 3. 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("The email has been registered");
            }
        }

        // 4. 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // 存储密码
        user.setNickname(request.getNickname());
        user.setRole(User.Role.user); // 默认为普通用户
        user.setStatus(1); // 默认启用
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 5. 保存用户
        User savedUser = userRepository.save(user);

        // 6. 返回用户信息
        return new UserResponse(savedUser);
    }

    public UserResponse getCurrentUser(String token) {
        // 1. 验证token
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        // 2. 从token中获取用户名
        String username = jwtUtil.getUsernameFromToken(token);

        // 3. 查找用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("The user does not exist");
        }

        User user = userOptional.get();

        // 4. 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("The account has been frozen");
        }

        // 5. 返回用户信息
        return new UserResponse(user);
    }

    private User findUserByUsernameOrPhoneOrEmail(String identifier) {
        // 尝试通过用户名查找
        Optional<User> userOptional = userRepository.findByUsername(identifier);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        // 尝试通过手机号查找
        userOptional = userRepository.findByPhone(identifier);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        // 尝试通过邮箱查找
        userOptional = userRepository.findByEmail(identifier);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xFor = request.getHeader("X-Forwarded-For");
        if (xFor != null && !xFor.isEmpty() && !"unKnown".equalsIgnoreCase(xFor)) {
            // 如果有多个反向代理，取第一个
            int index = xFor.indexOf(",");
            if (index != -1) {
                return xFor.substring(0, index);
            } else {
                return xFor;
            }
        }
        xFor = xip;
        if (xFor != null && !xFor.isEmpty() && !"unKnown".equalsIgnoreCase(xFor)) {
            return xFor;
        }
        if (xFor == null || xFor.isEmpty() || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("Proxy-Client-IP");
        }
        if (xFor == null || xFor.isEmpty() || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (xFor == null || xFor.isEmpty() || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (xFor == null || xFor.isEmpty() || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (xFor == null || xFor.isEmpty() || "unknown".equalsIgnoreCase(xFor)) {
            xFor = request.getRemoteAddr();
        }
        return xFor;
    }
}