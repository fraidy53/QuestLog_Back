package com.questlog.dto;

public class BattleStartRequest {
    private Long userId;
    private Long monsterId;
    
    public BattleStartRequest() {}
    
    public BattleStartRequest(Long userId, Long monsterId) {
        this.userId = userId;
        this.monsterId = monsterId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getMonsterId() {
        return monsterId;
    }
    
    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }
}

