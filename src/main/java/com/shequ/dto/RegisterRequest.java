package com.shequ.dto;

import javax.validation.constraints.*;

public class RegisterRequest {

    @NotBlank(message = "The username cannot be empty")
    @Size(min = 3, max = 50, message = "The length of the username must be between 3 and 50 characters.")
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "The mobile phone number format is incorrect")
    private String phone;

    @Email(message = "The email format is incorrect")
    private String email;

    @NotBlank(message = "The password cannot be empty")
    @Size(min = 6, max = 100, message = "The length of the password must be between 6 and 100 characters.")
    private String password;

    @NotBlank(message = "The nickname cannot be empty")
    @Size(max = 50, message = "The length of the nickname cannot exceed 50 characters.")
    private String nickname;

    public RegisterRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}