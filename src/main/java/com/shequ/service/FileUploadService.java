package com.shequ.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FileUploadService {

    @Value("${file.upload.path:}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${file.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    ));

    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    ));

    public Map<String, String> uploadImage(MultipartFile file, Long userId) {
        validateFile(file);

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFileName = generateFileName(fileExtension);

            // 确保上传目录存在
            Path uploadDir = getUploadDirectory();
            Files.createDirectories(uploadDir);

            // 保存文件
            Path filePath = uploadDir.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 构建访问URL
            String imageUrl = buildImageUrl(newFileName);

            Map<String, String> result = new HashMap<>();
            result.put("fileName", newFileName);
            result.put("originalName", originalFilename);
            result.put("imageUrl", imageUrl);
            result.put("fileSize", String.valueOf(file.getSize()));
            result.put("uploadTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            return result;
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + e.getMessage());
        }
    }

    public Map<String, Object> uploadImages(MultipartFile[] files, Long userId) {
        if (files == null || files.length == 0) {
            throw new RuntimeException("请选择要上传的文件");
        }

        if (files.length > 10) {
            throw new RuntimeException("批量上传文件数量不能超过10个");
        }

        List<Map<String, String>> successList = new ArrayList<>();
        List<String> failureList = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map<String, String> result = uploadImage(file, userId);
                successList.add(result);
            } catch (Exception e) {
                failureList.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successList.size());
        result.put("failureCount", failureList.size());
        result.put("successList", successList);
        result.put("failureList", failureList);
        result.put("totalCount", files.length);

        return result;
    }

    public void deleteImage(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            throw new RuntimeException("图片URL不能为空");
        }

        try {
            // 从URL中提取文件名
            String fileName = extractFileNameFromUrl(imageUrl);
            if (!StringUtils.hasText(fileName)) {
                throw new RuntimeException("无效的图片URL");
            }

            // 构建文件路径
            Path uploadDir = getUploadDirectory();
            Path filePath = uploadDir.resolve(fileName);

            // 检查文件是否存在并删除
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new RuntimeException("文件不存在");
            }
        } catch (IOException e) {
            throw new RuntimeException("删除文件失败: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("文件大小不能超过 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new RuntimeException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new RuntimeException("不支持的文件格式，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new RuntimeException("不支持的文件类型");
        }
    }

    private String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new RuntimeException("文件名不能为空");
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new RuntimeException("文件必须有扩展名");
        }

        return fileName.substring(lastDotIndex + 1);
    }

    private String generateFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String randomString = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        return timestamp + "_" + randomString + "." + extension.toLowerCase();
    }

    private Path getUploadDirectory() {
        if (StringUtils.hasText(uploadPath)) {
            // 使用配置的上传路径
            return Paths.get(uploadPath);
        }

        // 优先使用用户目录下的上传文件夹（适用于生产环境）
        Path userHomeDir = Paths.get(System.getProperty("user.home"), "shequ-uploads", "images");
        if (Files.exists(userHomeDir.getParent()) || userHomeDir.getParent().toFile().mkdirs()) {
            return userHomeDir;
        }

        // 备选方案：使用项目目录下的uploads文件夹
        String projectRoot = System.getProperty("user.dir");
        Path projectUploadDir = Paths.get(projectRoot, "uploads", "images");

        try {
            Files.createDirectories(projectUploadDir);
            return projectUploadDir;
        } catch (IOException e) {
            // 最后备选：使用临时目录
            return Paths.get(System.getProperty("java.io.tmpdir"), "shequ-uploads", "images");
        }
    }

    private String buildImageUrl(String fileName) {
        return baseUrl + "/images/" + fileName;
    }

    private String extractFileNameFromUrl(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return null;
        }

        // 从URL中提取文件名 (/images/20240802_143022_abc12345.jpg -> 20240802_143022_abc12345.jpg)
        int lastSlashIndex = imageUrl.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < imageUrl.length() - 1) {
            return imageUrl.substring(lastSlashIndex + 1);
        }

        return null;
    }
}