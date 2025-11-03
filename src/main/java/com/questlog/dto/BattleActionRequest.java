package com.questlog.dto;

public class BattleActionRequest {
    private Long battleId;
    private String action; // "attack"
    private String userItem; // 선택적으로 사용할 아이템 (포션 등)
    
    public BattleActionRequest() {}
    
    public BattleActionRequest(Long battleId, String action) {
        this.battleId = battleId;
        this.action = action;
    }
    
    public Long getBattleId() {
        return battleId;
    }
    
    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getUserItem() {
        return userItem;
    }
    
    public void setUserItem(String userItem) {
        this.userItem = userItem;
    }
}

