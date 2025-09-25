package com.questlog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "사용자 이름은 필수입니다")
    @Size(min = 2, max = 50, message = "사용자 이름은 2-50자 사이여야 합니다")
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 4, max = 20, message = "아이디는 4-20자 사이여야 합니다")
    @Column(name = "user_id", nullable = false, unique = true, length = 20)
    private String userId;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    @Column(name = "password", nullable = false)
    private String password;
    
    // 게임 상태 필드들
    @Column(name = "hp", nullable = false)
    private Integer hp = 100;
    
    @Column(name = "max_hp", nullable = false)
    private Integer maxHp = 100;
    
    @Column(name = "exp", nullable = false)
    private Integer exp = 0;
    
    @Column(name = "next_exp", nullable = false)
    private Integer nextExp = 100;
    
    @Column(name = "level", nullable = false)
    private Integer level = 1;
    
    @Column(name = "gold", nullable = false)
    private Integer gold = 0;
    
    // 기본 생성자
    public User() {}
    
    // 생성자
    public User(String username, String email, String userId, String password) {
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.password = password;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // 게임 상태 필드들의 Getters and Setters
    public Integer getHp() {
        return hp;
    }
    
    public void setHp(Integer hp) {
        this.hp = hp;
    }
    
    public Integer getMaxHp() {
        return maxHp;
    }
    
    public void setMaxHp(Integer maxHp) {
        this.maxHp = maxHp;
    }
    
    public Integer getExp() {
        return exp;
    }
    
    public void setExp(Integer exp) {
        this.exp = exp;
    }
    
    public Integer getNextExp() {
        return nextExp;
    }
    
    public void setNextExp(Integer nextExp) {
        this.nextExp = nextExp;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public Integer getGold() {
        return gold;
    }
    
    public void setGold(Integer gold) {
        this.gold = gold;
    }
}
