package com.questlog.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

@Entity
@Table(name = "user_status")
public class UserStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
    
    @Column(name = "last_daily_reset", nullable = false)
    private LocalDate lastDailyReset;
    
    @Column(name = "daily_quests_completed", nullable = false)
    private Integer dailyQuestsCompleted = 0;
    
    @Column(name = "daily_login_count", nullable = false)
    private Integer dailyLoginCount = 0;
    
    @Column(name = "boss_ready", nullable = false)
    private Boolean bossReady = false;
    
    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    
    // 기본 생성자
    public UserStatus() {}
    
    // 생성자
    public UserStatus(User user) {
        this.user = user;
        this.lastDailyReset = LocalDate.now();
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDate getLastDailyReset() {
        return lastDailyReset;
    }
    
    public void setLastDailyReset(LocalDate lastDailyReset) {
        this.lastDailyReset = lastDailyReset;
    }
    
    public Integer getDailyQuestsCompleted() {
        return dailyQuestsCompleted;
    }
    
    public void setDailyQuestsCompleted(Integer dailyQuestsCompleted) {
        this.dailyQuestsCompleted = dailyQuestsCompleted;
    }
    
    public Integer getDailyLoginCount() {
        return dailyLoginCount;
    }
    
    public void setDailyLoginCount(Integer dailyLoginCount) {
        this.dailyLoginCount = dailyLoginCount;
    }
    
    public Boolean getBossReady() {
        return bossReady;
    }
    
    public void setBossReady(Boolean bossReady) {
        this.bossReady = bossReady;
    }
    
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // 하루가 바뀌었는지 확인하는 메서드
    public boolean isNewDay() {
        return !lastDailyReset.equals(LocalDate.now());
    }
    
    // 하루 리셋 메서드
    public void resetDaily() {
        this.lastDailyReset = LocalDate.now();
        this.dailyQuestsCompleted = 0;
        this.dailyLoginCount = 0;
        this.bossReady = false;
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
