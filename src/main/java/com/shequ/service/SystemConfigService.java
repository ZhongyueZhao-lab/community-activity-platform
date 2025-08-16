package com.shequ.service;

import com.shequ.entity.SystemConfig;
import com.shequ.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository configRepository;

    public List<SystemConfig> getAllConfigs() {
        return configRepository.findAll();
    }

    public Optional<SystemConfig> getConfigByKey(String key) {
        return configRepository.findByConfigKey(key);
    }

    public String getConfigValue(String key, String defaultValue) {
        Optional<SystemConfig> config = configRepository.findByConfigKey(key);
        return config.map(SystemConfig::getConfigValue).orElse(defaultValue);
    }

    @Transactional
    public SystemConfig saveConfig(SystemConfig config) {
        Optional<SystemConfig> existingConfig = configRepository.findByConfigKey(config.getConfigKey());

        if (existingConfig.isPresent()) {
            SystemConfig existing = existingConfig.get();
            existing.setConfigValue(config.getConfigValue());
            existing.setDescription(config.getDescription());
            existing.setConfigType(config.getConfigType());
            existing.setUpdatedAt(LocalDateTime.now());
            return configRepository.save(existing);
        } else {
            config.setCreatedAt(LocalDateTime.now());
            config.setUpdatedAt(LocalDateTime.now());
            return configRepository.save(config);
        }
    }

    @Transactional
    public void deleteConfig(String key) {
        Optional<SystemConfig> config = configRepository.findByConfigKey(key);
        if (config.isPresent()) {
            if (config.get().getIsEditable() == 0) {
                throw new RuntimeException("该配置项不允许删除");
            }
            configRepository.delete(config.get());
        } else {
            throw new RuntimeException("配置项不存在");
        }
    }

    // 初始化默认配置的方法
    @Transactional
    public void initDefaultConfigs() {
        // 检查是否已经初始化过
        if (configRepository.count() > 0) {
            return;
        }

        // 创建默认配置
        SystemConfig[] defaultConfigs = {
                new SystemConfig("site_name", "Community Activity Platform", SystemConfig.ConfigType.STRING, "Website name"),
                new SystemConfig("site_description", "A platform dedicated to community activity organization", SystemConfig.ConfigType.STRING, "Website description"),
                new SystemConfig("registration_enabled", "1", SystemConfig.ConfigType.BOOLEAN, "Whether to open user registration"),
                new SystemConfig("activity_auto_publish", "0", SystemConfig.ConfigType.BOOLEAN, "Whether the activity is automatically published (skipping review)"),
                new SystemConfig("max_upload_size", "10485760", SystemConfig.ConfigType.NUMBER, "Maximum file upload size (bytes)"),
                new SystemConfig("contact_email", "contact@example.com", SystemConfig.ConfigType.STRING, "Contact Email"),
                new SystemConfig("contact_phone", "400-000-0000", SystemConfig.ConfigType.STRING, "Contact phone number"),
                new SystemConfig("sms_enabled", "0", SystemConfig.ConfigType.BOOLEAN, "Whether to enable the SMS function"),
                new SystemConfig("email_enabled", "1", SystemConfig.ConfigType.BOOLEAN, "Whether to enable the email function")
        };

        for (SystemConfig config : defaultConfigs) {
            config.setIsEditable(1);
            configRepository.save(config);
        }
    }
}
