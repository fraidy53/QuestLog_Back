package com.questlog.dto;

import java.util.Map;

public class EquipResponse {
    
    private boolean success;
    private String message;
    private Map<String, String> equipped;
    private Map<String, Integer> stats;
    
    // 기본 생성자
    public EquipResponse() {}
    
    // 생성자
    public EquipResponse(boolean success, String message, Map<String, String> equipped, Map<String, Integer> stats) {
        this.success = success;
        this.message = message;
        this.equipped = equipped;
        this.stats = stats;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Map<String, String> getEquipped() {
        return equipped;
    }
    
    public void setEquipped(Map<String, String> equipped) {
        this.equipped = equipped;
    }
    
    public Map<String, Integer> getStats() {
        return stats;
    }
    
    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
    }
}
