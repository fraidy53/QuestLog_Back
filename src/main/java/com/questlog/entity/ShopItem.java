package com.questlog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shop_items")
public class ShopItem {
    
    @Id
    @Column(name = "item_id")
    private String itemId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ItemType type;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "price", nullable = false)
    private Integer price;
    
    @Column(name = "stat_type", nullable = false)
    private String statType; // atk, def, heal 등
    
    @Column(name = "stat_value", nullable = false)
    private Integer statValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rarity", nullable = false)
    private Rarity rarity;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // 아이템 타입 열거형
    public enum ItemType {
        WEAPON, ARMOR, POTION, PET
    }
    
    // 희귀도 열거형
    public enum Rarity {
        COMMON, NORMAL, RARE, EPIC, LEGENDARY
    }
    
    // 기본 생성자
    public ShopItem() {}
    
    // 생성자
    public ShopItem(String itemId, ItemType type, String name, Integer price, 
                   String statType, Integer statValue, Rarity rarity) {
        this.itemId = itemId;
        this.type = type;
        this.name = name;
        this.price = price;
        this.statType = statType;
        this.statValue = statValue;
        this.rarity = rarity;
    }
    
    // Getters and Setters
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getPrice() {
        return price;
    }
    
    public void setPrice(Integer price) {
        this.price = price;
    }
    
    public String getStatType() {
        return statType;
    }
    
    public void setStatType(String statType) {
        this.statType = statType;
    }
    
    public Integer getStatValue() {
        return statValue;
    }
    
    public void setStatValue(Integer statValue) {
        this.statValue = statValue;
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
