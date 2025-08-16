package com.shequ.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片静态资源映射
        if (uploadPath != null && !uploadPath.isEmpty()) {
            // 使用配置的上传路径
            registry.addResourceHandler("/images/**")
                    .addResourceLocations("file:" + uploadPath + "/");
        } else {
            // 使用默认路径
            String userHomeDir = "file:" + System.getProperty("user.home") + "/shequ-uploads/images/";
            String projectUploadDir = "file:" + System.getProperty("user.dir") + "/uploads/images/";
            String tempDir = "file:" + System.getProperty("java.io.tmpdir") + "/shequ-uploads/images/";

            registry.addResourceHandler("/images/**")
                    .addResourceLocations(userHomeDir)
                    .addResourceLocations(projectUploadDir)
                    .addResourceLocations(tempDir)
                    .addResourceLocations("classpath:/images/");
        }

        // 添加其他静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        registry.addMapping("/images/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}