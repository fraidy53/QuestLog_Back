package com.questlog.dto;

import java.util.Map;

public class BattleActionResponse {
    private Integer userHp;
    private Integer monsterHp;
    private Map<String, Object> turnResult; // 이번 턴의 데미지 및 행동 정보
    private boolean battleEnded;
    private String battleResult; // "win" or "lose" (전투 종료 시만)
    
    public BattleActionResponse() {}
    
    public BattleActionResponse(Integer userHp, Integer monsterHp, Map<String, Object> turnResult) {
        this.userHp = userHp;
        this.monsterHp = monsterHp;
        this.turnResult = turnResult;
        this.battleEnded = false;
    }
    
    public Integer getUserHp() {
        return userHp;
    }
    
    public void setUserHp(Integer userHp) {
        this.userHp = userHp;
    }
    
    public Integer getMonsterHp() {
        return monsterHp;
    }
    
    public void setMonsterHp(Integer monsterHp) {
        this.monsterHp = monsterHp;
    }
    
    public Map<String, Object> getTurnResult() {
        return turnResult;
    }
    
    public void setTurnResult(Map<String, Object> turnResult) {
        this.turnResult = turnResult;
    }
    
    public boolean isBattleEnded() {
        return battleEnded;
    }
    
    public void setBattleEnded(boolean battleEnded) {
        this.battleEnded = battleEnded;
    }
    
    public String getBattleResult() {
        return battleResult;
    }
    
    public void setBattleResult(String battleResult) {
        this.battleResult = battleResult;
    }
}

