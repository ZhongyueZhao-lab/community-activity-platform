package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.MessageDTO;
import com.shequ.dto.PageResult;
import com.shequ.dto.UserResponse;
import com.shequ.service.AuthService;
import com.shequ.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user/messages")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class UserMessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ApiResponse<PageResult<MessageDTO>> getUserMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            boolean isAdmin = "admin".equals(currentUser.getRole());
            PageResult<MessageDTO> result = messageService.getUserMessages(currentUser.getId(), isAdmin, page, size);
            return ApiResponse.success("Successfully obtained the message list", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the message list");
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<MessageDTO> getMessage(@PathVariable Long id,
                                              HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            MessageDTO message = messageService.getMessageById(id);

            // 自动标记为已读
            messageService.markAsRead(id, currentUser.getId());

            return ApiResponse.success("Successfully obtained the message details", message);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the message details");
        }
    }

    @PostMapping("/{id}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long id,
                                          HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            messageService.markAsRead(id, currentUser.getId());
            return ApiResponse.success("Marked as read successfully", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to mark as read");
        }
    }

    @PostMapping("/read-all")
    public ApiResponse<String> markAllAsRead(HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            boolean isAdmin = "admin".equals(currentUser.getRole());
            messageService.markAllAsRead(currentUser.getId(), isAdmin);
            return ApiResponse.success("Marked all as read successfully", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to mark all as read");
        }
    }

    @PostMapping("/{id}/delete")
    public ApiResponse<String> markAsDeleted(@PathVariable Long id,
                                             HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            messageService.markAsDeleted(id, currentUser.getId());
            return ApiResponse.success("The message was successfully deleted", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete the message");
        }
    }

    @GetMapping("/unread/count")
    public ApiResponse<Long> getUnreadCount(HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            boolean isAdmin = "admin".equals(currentUser.getRole());
            long count = messageService.getUnreadMessageCount(currentUser.getId(), isAdmin);
            return ApiResponse.success("Successfully obtained the number of unread messages", count);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the number of unread messages");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("No authentication information provided");
    }
}