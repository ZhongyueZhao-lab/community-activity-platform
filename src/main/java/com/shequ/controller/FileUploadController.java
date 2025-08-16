package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.UserResponse;
import com.shequ.service.AuthService;
import com.shequ.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private AuthService authService;

    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file,
                                                        HttpServletRequest request) {
        try {
            logger.info("开始上传图片，文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());

            // 验证用户权限
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            if (currentUser == null) {
                logger.warn("User authentication failed");
                return ApiResponse.error("User authentication failed");
            }

            // 检查是否为管理员
            if (!"admin".equals(currentUser.getRole())) {
                logger.warn("非管理员用户尝试上传图片，用户ID: {}", currentUser.getId());
                return ApiResponse.error("权限不足，仅管理员可上传图片");
            }

            Map<String, String> result = fileUploadService.uploadImage(file, currentUser.getId());
            logger.info("图片上传成功，文件名: {}, URL: {}", result.get("fileName"), result.get("imageUrl"));

            return ApiResponse.success("上传图片成功", result);
        } catch (RuntimeException e) {
            logger.error("上传图片失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("上传图片时发生未知错误", e);
            return ApiResponse.error("上传图片失败，请重试");
        }
    }

    @PostMapping("/images/batch")
    public ApiResponse<Map<String, Object>> uploadImages(@RequestParam("files") MultipartFile[] files,
                                                         HttpServletRequest request) {
        try {
            logger.info("开始批量上传图片，文件数量: {}", files != null ? files.length : 0);

            // 验证用户权限
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            if (currentUser == null) {
                logger.warn("用户认证失败");
                return ApiResponse.error("用户认证失败");
            }

            // 检查是否为管理员
            if (!"admin".equals(currentUser.getRole())) {
                logger.warn("非管理员用户尝试批量上传图片，用户ID: {}", currentUser.getId());
                return ApiResponse.error("权限不足，仅管理员可上传图片");
            }

            Map<String, Object> result = fileUploadService.uploadImages(files, currentUser.getId());
            logger.info("批量上传图片完成，成功: {}, 失败: {}",
                    result.get("successCount"), result.get("failureCount"));

            return ApiResponse.success("批量上传图片完成", result);
        } catch (RuntimeException e) {
            logger.error("批量上传图片失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("批量上传图片时发生未知错误", e);
            return ApiResponse.error("批量上传图片失败，请重试");
        }
    }

    @DeleteMapping("/image")
    public ApiResponse<String> deleteImage(@RequestParam("imageUrl") String imageUrl,
                                           HttpServletRequest request) {
        try {
            logger.info("开始删除图片，URL: {}", imageUrl);

            // 验证用户权限
            String token = extractToken(request);
            UserResponse currentUser = authService.getCurrentUser(token);

            if (currentUser == null) {
                logger.warn("用户认证失败");
                return ApiResponse.error("用户认证失败");
            }

            // 检查是否为管理员
            if (!"admin".equals(currentUser.getRole())) {
                logger.warn("非管理员用户尝试删除图片，用户ID: {}", currentUser.getId());
                return ApiResponse.error("权限不足，仅管理员可删除图片");
            }

            fileUploadService.deleteImage(imageUrl);
            logger.info("图片删除成功，URL: {}", imageUrl);

            return ApiResponse.success("删除图片成功", null);
        } catch (RuntimeException e) {
            logger.error("删除图片失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("删除图片时发生未知错误", e);
            return ApiResponse.error("删除图片失败，请重试");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("未提供认证信息");
    }
}