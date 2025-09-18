package com.questlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyCodeRequest {
    
    @NotBlank(message = "이메일은 필수입니다")
    private String email;
    
    @NotBlank(message = "인증번호는 필수입니다")
    @Pattern(regexp = "^\\d{6}$", message = "인증번호는 6자리 숫자여야 합니다")
    private String code;
    
    // 기본 생성자
    public VerifyCodeRequest() {}
    
    // 생성자
    public VerifyCodeRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
}
