package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.MessageDTO;
import com.shequ.dto.MessageStatsDTO;
import com.shequ.dto.PageResult;
import com.shequ.dto.UserResponse;
import com.shequ.service.AuthService;
import com.shequ.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/messages")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@Validated
public class AdminMessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ApiResponse<PageResult<MessageDTO>> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<MessageDTO> result = messageService.getMessages(page, size, title, type, targetType, status);
            return ApiResponse.success("Successfully obtained the message list", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the message list：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<MessageDTO> getMessage(@PathVariable Long id) {
        try {
            MessageDTO message = messageService.getMessageById(id);
            return ApiResponse.success("Successfully obtained the message details", message);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the message details");
        }
    }

    @PostMapping
    public ApiResponse<MessageDTO> createMessage(@Valid @RequestBody MessageDTO messageDTO,
                                                 HttpServletRequest request) {
        try {
            // 获取当前用户
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            MessageDTO result = messageService.createMessage(messageDTO, currentUser.getId());
            return ApiResponse.success("Message created successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to create message");
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<MessageDTO> updateMessage(@PathVariable Long id,
                                                 @Valid @RequestBody MessageDTO messageDTO) {
        try {
            MessageDTO result = messageService.updateMessage(id, messageDTO);
            return ApiResponse.success("The message was updated successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update the message");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMessage(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return ApiResponse.success("The message was deleted successfully", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete the message");
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<MessageDTO> updateMessageStatus(@PathVariable Long id,
                                                       @RequestParam Integer status) {
        try {
            MessageDTO result = messageService.updateMessageStatus(id, status);
            return ApiResponse.success("The message status was updated successfully", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update the message status");
        }
    }

    @GetMapping("/{id}/stats")
    public ApiResponse<MessageStatsDTO> getMessageStats(@PathVariable Long id) {
        try {
            MessageStatsDTO stats = messageService.getMessageStats(id);
            return ApiResponse.success("Successfully obtained the message statistics", stats);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to obtain the message statistics");
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