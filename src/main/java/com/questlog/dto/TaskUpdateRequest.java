package com.questlog.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

public class TaskUpdateRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    private String memo;
    
    @NotBlank(message = "카테고리는 필수입니다")
    private String category;
    
    private LocalTime time;
    
    // 기본 생성자
    public TaskUpdateRequest() {}
    
    // 생성자
    public TaskUpdateRequest(String title, String memo, String category, LocalTime time) {
        this.title = title;
        this.memo = memo;
        this.category = category;
        this.time = time;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalTime getTime() {
        return time;
    }
    
    public void setTime(LocalTime time) {
        this.time = time;
    }
}
