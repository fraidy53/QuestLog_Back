package com.questlog.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
    
    // 무기 정보
    @Column(name = "weapon_id", nullable = false)
    private String weaponId = "starting_weapon";
    
    @Column(name = "weapon_name", nullable = false)
    private String weaponName = "스타팅 무기";
    
    @Column(name = "weapon_atk", nullable = false)
    private Integer weaponAtk = 2;
    
    // 갑옷 정보
    @Column(name = "armor_id", nullable = false)
    private String armorId = "starting_armor";
    
    @Column(name = "armor_name", nullable = false)
    private String armorName = "스타팅 갑옷";
    
    @Column(name = "armor_def", nullable = false)
    private Integer armorDef = 2;
    
    // 포션 개수
    @Column(name = "potions", nullable = false)
    private Integer potions = 1;
    
    // 펫 정보 (JSON 형태로 저장)
    @Column(name = "pets", columnDefinition = "TEXT")
    private String pets = "[]"; // 빈 배열로 초기화
    
    // 기본 생성자
    public Inventory() {}
    
    // 생성자
    public Inventory(User user) {
        this.user = user;
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
    
    public String getWeaponId() {
        return weaponId;
    }
    
    public void setWeaponId(String weaponId) {
        this.weaponId = weaponId;
    }
    
    public String getWeaponName() {
        return weaponName;
    }
    
    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }
    
    public Integer getWeaponAtk() {
        return weaponAtk;
    }
    
    public void setWeaponAtk(Integer weaponAtk) {
        this.weaponAtk = weaponAtk;
    }
    
    public String getArmorId() {
        return armorId;
    }
    
    public void setArmorId(String armorId) {
        this.armorId = armorId;
    }
    
    public String getArmorName() {
        return armorName;
    }
    
    public void setArmorName(String armorName) {
        this.armorName = armorName;
    }
    
    public Integer getArmorDef() {
        return armorDef;
    }
    
    public void setArmorDef(Integer armorDef) {
        this.armorDef = armorDef;
    }
    
    public Integer getPotions() {
        return potions;
    }
    
    public void setPotions(Integer potions) {
        this.potions = potions;
    }
    
    public String getPets() {
        return pets;
    }
    
    public void setPets(String pets) {
        this.pets = pets;
    }
}
