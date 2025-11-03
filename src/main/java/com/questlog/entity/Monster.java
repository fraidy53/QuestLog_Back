package com.questlog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "monsters")
public class Monster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "hp", nullable = false)
    private Integer hp;
    
    @Column(name = "def", nullable = false)
    private Integer def;
    
    @Column(name = "drop_item", length = 50)
    private String dropItem; // 강아지, 고양이, 토끼 중 하나
    
    // 기본 생성자
    public Monster() {}
    
    // 생성자
    public Monster(Integer hp, Integer def, String dropItem) {
        this.hp = hp;
        this.def = def;
        this.dropItem = dropItem;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getHp() {
        return hp;
    }
    
    public void setHp(Integer hp) {
        this.hp = hp;
    }
    
    public Integer getDef() {
        return def;
    }
    
    public void setDef(Integer def) {
        this.def = def;
    }
    
    public String getDropItem() {
        return dropItem;
    }
    
    public void setDropItem(String dropItem) {
        this.dropItem = dropItem;
    }
}

