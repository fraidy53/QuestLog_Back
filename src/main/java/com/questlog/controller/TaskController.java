package com.questlog.controller;

import com.questlog.dto.ApiResponse;
import com.questlog.dto.RewardResponse;
import com.questlog.dto.TaskResponse;
import com.questlog.entity.Task;
import com.questlog.entity.User;
import com.questlog.repository.UserRepository;
import com.questlog.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private QuestService questService;
    
    @Autowired
    private UserRepository userRepository;
    
    // 오늘 일정 불러오기 (최대 6개)
    // GET /tasks?date=today&userId=1
    @GetMapping
    public ResponseEntity<ApiResponse> getTasks(
            @RequestParam Long userId,
            @RequestParam String date) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            List<Task> tasks;
            
            if ("today".equals(date)) {
                // 오늘 일정 최대 6개
                tasks = questService.getTodayTasksLimit(user, 6);
            } else {
                // 특정 날짜 일정 조회 (YYYY-MM-DD 형식)
                LocalDate targetDate = LocalDate.parse(date);
                tasks = questService.getTasksByDate(user, targetDate);
            }
            
            List<TaskResponse> responses = tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("일정을 조회했습니다.", responses));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 일정 완료 처리
    // PATCH /tasks/{id}/complete?userId=1
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse> completeTask(
            @PathVariable("id") Long taskId,
            @RequestParam Long userId) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            QuestService.RewardResult rewardResult = questService.completeTaskWithReward(user, taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("reward", new RewardResponse(rewardResult.getExp(), rewardResult.getGold()));
            
            return ResponseEntity.ok(ApiResponse.success("일정이 완료되었습니다.", response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 완료 처리에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 일정 실패 처리
    // PATCH /tasks/{id}/fail?userId=1
    @PatchMapping("/{id}/fail")
    public ResponseEntity<ApiResponse> failTask(
            @PathVariable("id") Long taskId,
            @RequestParam Long userId) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            Task task = questService.failTask(user, taskId);
            TaskResponse response = new TaskResponse(task);
            
            return ResponseEntity.ok(ApiResponse.success("일정이 실패 처리되었습니다.", response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 실패 처리에 실패했습니다: " + e.getMessage()));
        }
    }
}

