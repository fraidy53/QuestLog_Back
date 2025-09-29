package com.questlog.dto;

import jakarta.validation.constraints.NotBlank;

public class EquipRequest {
    
    @NotBlank(message = "아이템 ID는 필수입니다")
    private String itemId;
    
    // 기본 생성자
    public EquipRequest() {}
    
    // 생성자
    public EquipRequest(String itemId) {
        this.itemId = itemId;
    }
    
    // Getters and Setters
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
