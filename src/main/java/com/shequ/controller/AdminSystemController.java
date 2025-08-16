package com.shequ.controller;

import com.shequ.common.ApiResponse;
import com.shequ.dto.SystemConfigDTO;
import com.shequ.entity.SystemConfig;
import com.shequ.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/system")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@Validated
public class AdminSystemController {

    @Autowired
    private SystemConfigService configService;

    @GetMapping("/configs")
    public ApiResponse<List<SystemConfig>> getAllConfigs() {
        try {
            List<SystemConfig> configs = configService.getAllConfigs();
            return ApiResponse.success("获取系统配置成功", configs);
        } catch (Exception e) {
            return ApiResponse.error("获取系统配置失败：" + e.getMessage());
        }
    }

    @GetMapping("/configs/{key}")
    public ApiResponse<SystemConfig> getConfig(@PathVariable String key) {
        try {
            SystemConfig config = configService.getConfigByKey(key)
                    .orElseThrow(() -> new RuntimeException("配置项不存在"));
            return ApiResponse.success("获取配置项成功", config);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取配置项失败");
        }
    }

    @PostMapping("/configs")
    public ApiResponse<SystemConfig> saveConfig(@Valid @RequestBody SystemConfigDTO configDTO) {
        try {
            // 转换DTO到实体
            SystemConfig config = new SystemConfig();
            config.setConfigKey(configDTO.getConfigKey());
            config.setConfigValue(configDTO.getConfigValue());
            config.setDescription(configDTO.getDescription());

            // 处理配置类型
            if (configDTO.getConfigType() != null && !configDTO.getConfigType().isEmpty()) {
                try {
                    SystemConfig.ConfigType configType = SystemConfig.ConfigType.fromValue(configDTO.getConfigType());
                    config.setConfigType(configType);
                } catch (IllegalArgumentException e) {
                    return ApiResponse.error("无效的配置类型：" + configDTO.getConfigType());
                }
            } else {
                config.setConfigType(SystemConfig.ConfigType.STRING);
            }

            SystemConfig result = configService.saveConfig(config);
            return ApiResponse.success("保存配置项成功", result);
        } catch (Exception e) {
            return ApiResponse.error("保存配置项失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/configs/{key}")
    public ApiResponse<String> deleteConfig(@PathVariable String key) {
        try {
            configService.deleteConfig(key);
            return ApiResponse.success("删除配置项成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除配置项失败");
        }
    }

    @PostMapping("/configs/init")
    public ApiResponse<String> initDefaultConfigs() {
        try {
            configService.initDefaultConfigs();
            return ApiResponse.success("初始化默认配置成功", null);
        } catch (Exception e) {
            return ApiResponse.error("初始化默认配置失败：" + e.getMessage());
        }
    }
}
