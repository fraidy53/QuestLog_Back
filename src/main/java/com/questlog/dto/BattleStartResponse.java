package com.questlog.dto;

import java.util.Map;

public class BattleStartResponse {
    private Long battleId;
    private Map<String, Integer> userStats;
    private Map<String, Integer> monsterStats;
    private Map<String, Object> equippedItems;
    
    public BattleStartResponse() {}
    
    public BattleStartResponse(Long battleId, Map<String, Integer> userStats, 
                               Map<String, Integer> monsterStats, Map<String, Object> equippedItems) {
        this.battleId = battleId;
        this.userStats = userStats;
        this.monsterStats = monsterStats;
        this.equippedItems = equippedItems;
    }
    
    public Long getBattleId() {
        return battleId;
    }
    
    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }
    
    public Map<String, Integer> getUserStats() {
        return userStats;
    }
    
    public void setUserStats(Map<String, Integer> userStats) {
        this.userStats = userStats;
    }
    
    public Map<String, Integer> getMonsterStats() {
        return monsterStats;
    }
    
    public void setMonsterStats(Map<String, Integer> monsterStats) {
        this.monsterStats = monsterStats;
    }
    
    public Map<String, Object> getEquippedItems() {
        return equippedItems;
    }
    
    public void setEquippedItems(Map<String, Object> equippedItems) {
        this.equippedItems = equippedItems;
    }
}

