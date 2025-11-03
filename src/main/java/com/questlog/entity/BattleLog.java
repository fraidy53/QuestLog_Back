package com.questlog.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "battle_logs")
public class BattleLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "battle_id")
    private Long battleId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "monster_id", referencedColumnName = "id")
    @JsonIgnore
    private Monster monster;
    
    @Column(name = "final_user_hp", nullable = false)
    private Integer finalUserHp;
    
    @Column(name = "final_monster_hp", nullable = false)
    private Integer finalMonsterHp;
    
    @Column(name = "damage_log", columnDefinition = "TEXT")
    private String damageLog; // JSON 형태로 저장
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BattleStatus status;
    
    @Column(name = "reward", columnDefinition = "TEXT")
    private String reward; // JSON 형태로 저장 (exp, gold, drops)
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public enum BattleStatus {
        WIN, LOSE
    }
    
    // 기본 생성자
    public BattleLog() {}
    
    // 생성자
    public BattleLog(User user, Monster monster) {
        this.user = user;
        this.monster = monster;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getBattleId() {
        return battleId;
    }
    
    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Monster getMonster() {
        return monster;
    }
    
    public void setMonster(Monster monster) {
        this.monster = monster;
    }
    
    public Integer getFinalUserHp() {
        return finalUserHp;
    }
    
    public void setFinalUserHp(Integer finalUserHp) {
        this.finalUserHp = finalUserHp;
    }
    
    public Integer getFinalMonsterHp() {
        return finalMonsterHp;
    }
    
    public void setFinalMonsterHp(Integer finalMonsterHp) {
        this.finalMonsterHp = finalMonsterHp;
    }
    
    public String getDamageLog() {
        return damageLog;
    }
    
    public void setDamageLog(String damageLog) {
        this.damageLog = damageLog;
    }
    
    public BattleStatus getStatus() {
        return status;
    }
    
    public void setStatus(BattleStatus status) {
        this.status = status;
    }
    
    public String getReward() {
        return reward;
    }
    
    public void setReward(String reward) {
        this.reward = reward;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

