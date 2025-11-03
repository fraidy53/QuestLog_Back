package com.questlog.dto;

import java.util.List;

public class BattleEndResponse {
    private String result; // "win" or "lose"
    private Integer exp;
    private Integer gold;
    private List<String> drops;
    
    public BattleEndResponse() {}
    
    public BattleEndResponse(String result, Integer exp, Integer gold, List<String> drops) {
        this.result = result;
        this.exp = exp;
        this.gold = gold;
        this.drops = drops;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public Integer getExp() {
        return exp;
    }
    
    public void setExp(Integer exp) {
        this.exp = exp;
    }
    
    public Integer getGold() {
        return gold;
    }
    
    public void setGold(Integer gold) {
        this.gold = gold;
    }
    
    public List<String> getDrops() {
        return drops;
    }
    
    public void setDrops(List<String> drops) {
        this.drops = drops;
    }
}

