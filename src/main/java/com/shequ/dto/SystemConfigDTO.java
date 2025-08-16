package com.shequ.dto;

import javax.validation.constraints.NotBlank;

public class SystemConfigDTO {

    @NotBlank(message = "The configuration key cannot be empty")
    private String configKey;

    private String configValue;

    private String configType = "string";

    private String description;

    public SystemConfigDTO() {}

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

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
