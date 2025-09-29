package com.questlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class TaskRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    private String memo;
    
    @NotBlank(message = "카테고리는 필수입니다")
    private String category;
    
    @NotNull(message = "날짜는 필수입니다")
    private LocalDate date;
    
    private LocalTime time;
    
    // 기본 생성자
    public TaskRequest() {}
    
    // 생성자
    public TaskRequest(String title, String memo, String category, LocalDate date, LocalTime time) {
        this.title = title;
        this.memo = memo;
        this.category = category;
        this.date = date;
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
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalTime getTime() {
        return time;
    }
    
    public void setTime(LocalTime time) {
        this.time = time;
    }
}
