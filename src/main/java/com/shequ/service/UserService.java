package com.shequ.service;

import com.shequ.dto.PageResult;
import com.shequ.dto.UserResponse;
import com.shequ.entity.User;
import com.shequ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public PageResult<UserResponse> getUsers(int page, int size, String keyword, String role, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> pageResult;

        if (keyword != null && !keyword.trim().isEmpty()) {
            pageResult = userRepository.findByUsernameContainingOrNicknameContaining(
                    keyword.trim(), keyword.trim(), pageable);
        } else if (role != null && !role.isEmpty()) {
            try {
                User.Role roleEnum = User.Role.valueOf(role);
                pageResult = userRepository.findByRole(roleEnum, pageable);
            } catch (IllegalArgumentException e) {
                pageResult = userRepository.findAll(pageable);
            }
        } else if (status != null) {
            pageResult = userRepository.findByStatus(status, pageable);
        } else {
            pageResult = userRepository.findAll(pageable);
        }

        List<UserResponse> userResponses = pageResult.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return new PageResult<>(userResponses, pageResult.getTotalElements(), page, size);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));

        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));

        try {
            User.Role roleEnum = User.Role.valueOf(role);
            user.setRole(roleEnum);
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            return new UserResponse(savedUser);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user role");
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));

        // 检查是否为管理员账号
        if (user.getRole() == User.Role.admin) {
            throw new RuntimeException("The administrator account cannot be deleted");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse resetUserPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The user does not exist"));

        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }
}
