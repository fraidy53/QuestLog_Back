package com.questlog.dto;

import java.util.List;

public class QuestStatusResponse {
    
    private List<TaskResponse> tasks;
    private double completionRate;
    private boolean bossReady;
    private int totalTasks;
    private int completedTasks;
    
    // 기본 생성자
    public QuestStatusResponse() {}
    
    // 생성자
    public QuestStatusResponse(List<TaskResponse> tasks, double completionRate, 
                              boolean bossReady, int totalTasks, int completedTasks) {
        this.tasks = tasks;
        this.completionRate = completionRate;
        this.bossReady = bossReady;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
    }
    
    // Getters and Setters
    public List<TaskResponse> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }
    
    public double getCompletionRate() {
        return completionRate;
    }
    
    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }
    
    public boolean isBossReady() {
        return bossReady;
    }
    
    public void setBossReady(boolean bossReady) {
        this.bossReady = bossReady;
    }
    
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public int getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
}
