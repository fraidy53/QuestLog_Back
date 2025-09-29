package com.questlog.dto;

import com.questlog.entity.Task;
import java.time.format.DateTimeFormatter;

public class TaskResponse {
    
    private Long taskId;
    private String title;
    private String memo;
    private String category;
    private String status;
    private String date;
    private String time;
    private String createdAt;
    private String updatedAt;
    
    // 기본 생성자
    public TaskResponse() {}
    
    // Task 엔티티로부터 생성
    public TaskResponse(Task task) {
        this.taskId = task.getTaskId();
        this.title = task.getTitle();
        this.memo = task.getMemo();
        this.category = task.getCategory().getValue();
        this.status = task.getStatus().getValue();
        this.date = task.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.time = task.getDueTime() != null ? 
            task.getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null;
        this.createdAt = task.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updatedAt = task.getUpdatedAt() != null ? 
            task.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
    
    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
