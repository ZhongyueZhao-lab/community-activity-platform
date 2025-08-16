package com.shequ.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_config")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "The configuration key cannot be empty")
    @Column(name = "config_key", unique = true, nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "config_type")
    private ConfigType configType = ConfigType.STRING;

    @Column(length = 255)
    private String description;

    @Column(name = "is_editable")
    private Integer isEditable = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 修复枚举定义 - 让value值与枚举常量名保持一致
    public enum ConfigType {
        STRING("STRING"),
        NUMBER("NUMBER"),
        BOOLEAN("BOOLEAN"),
        JSON("JSON");

        private final String value;

        ConfigType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        // 根据字符串值获取枚举 - 支持大小写不敏感
        public static ConfigType fromValue(String value) {
            if (value == null) {
                return STRING;
            }

            // 转换为大写进行匹配
            String upperValue = value.toUpperCase();

            for (ConfigType type : ConfigType.values()) {
                if (type.getValue().equals(upperValue)) {
                    return type;
                }
            }

            // 如果没有找到匹配的，尝试根据枚举名称匹配
            try {
                return ConfigType.valueOf(upperValue);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid config type: " + value);
            }
        }
    }

    // 构造函数
    public SystemConfig() {}

    public SystemConfig(String configKey, String configValue, ConfigType configType, String description) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.description = description;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigType(ConfigType configType) {
        this.configType = configType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Integer isEditable) {
        this.isEditable = isEditable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}