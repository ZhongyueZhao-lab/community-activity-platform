package com.shequ.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(max = 50, message = "The length of the nickname cannot exceed 50 characters")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "The mobile phone number format is incorrect")
    private String phone;

    @Email(message = "The email format is incorrect")
    private String email;

    private String avatar;

    private Integer gender; // 0-未设置，1-男，2-女

    private Integer age;

    @Size(max = 500, message = "The length of the personal profile cannot exceed 500 characters")
    private String bio;

    public UpdateProfileRequest() {}

    // Getter 和 Setter 方法
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}