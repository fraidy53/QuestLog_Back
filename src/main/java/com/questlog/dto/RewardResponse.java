package com.questlog.dto;

public class RewardResponse {
    private int exp;
    private int gold;
    
    public RewardResponse() {}
    
    public RewardResponse(int exp, int gold) {
        this.exp = exp;
        this.gold = gold;
    }
    
    public int getExp() {
        return exp;
    }
    
    public void setExp(int exp) {
        this.exp = exp;
    }
    
    public int getGold() {
        return gold;
    }
    
    public void setGold(int gold) {
        this.gold = gold;
    }
}

