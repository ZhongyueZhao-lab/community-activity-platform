package com.shequ.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "The current password cannot be empty.")
    private String currentPassword;

    @NotBlank(message = "The new password cannot be empty.")
    @Size(min = 6, max = 100, message = "The length of the new password must be between 6 and 100 characters.")
    private String newPassword;

    @NotBlank(message = "The confirm password cannot be empty.")
    private String confirmPassword;

    public ChangePasswordRequest() {}

    // Getter 和 Setter 方法
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}