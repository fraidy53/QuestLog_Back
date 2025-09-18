package com.questlog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class FindPasswordRequest {
    
    @NotBlank(message = "사용자 이름은 필수입니다")
    private String username;
    
    @NotBlank(message = "아이디는 필수입니다")
    private String userId;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    // 기본 생성자
    public FindPasswordRequest() {}
    
    // 생성자
    public FindPasswordRequest(String username, String userId, String email) {
        this.username = username;
        this.userId = userId;
        this.email = email;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
