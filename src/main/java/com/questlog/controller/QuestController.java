package com.questlog.controller;

import com.questlog.dto.ApiResponse;
import com.questlog.dto.QuestStatusResponse;
import com.questlog.dto.TaskRequest;
import com.questlog.dto.TaskResponse;
import com.questlog.entity.Task;
import com.questlog.entity.User;
import com.questlog.repository.UserRepository;
import com.questlog.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/quests")
@CrossOrigin(origins = "*")
public class QuestController {
    
    @Autowired
    private QuestService questService;
    
    @Autowired
    private UserRepository userRepository;
    
    // 일정 생성
    @PostMapping
    public ResponseEntity<ApiResponse> createTask(
            @RequestParam Long userId,
            @Valid @RequestBody TaskRequest taskRequest) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            // 카테고리 변환
            Task.TaskCategory category = Task.TaskCategory.valueOf(taskRequest.getCategory().toUpperCase());
            
            Task task = questService.createTask(
                user,
                taskRequest.getTitle(),
                taskRequest.getMemo(),
                category,
                taskRequest.getDate(),
                taskRequest.getTime()
            );
            
            TaskResponse response = new TaskResponse(task);
            return ResponseEntity.ok(ApiResponse.success("일정이 생성되었습니다.", response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 생성에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 사용자별 모든 일정 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getUserTasks(@RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            List<Task> tasks = questService.getUserTasks(user);
            List<TaskResponse> responses = tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("일정 목록을 조회했습니다.", responses));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 특정 날짜의 일정 조회
    @GetMapping("/date")
    public ResponseEntity<ApiResponse> getTasksByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            List<Task> tasks = questService.getTasksByDate(user, date);
            List<TaskResponse> responses = tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("해당 날짜의 일정을 조회했습니다.", responses));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 오늘의 일정 조회
    @GetMapping("/today")
    public ResponseEntity<ApiResponse> getTodayTasks(@RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            List<Task> tasks = questService.getTodayTasks(user);
            List<TaskResponse> taskResponses = tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
            
            double completionRate = questService.getTodayCompletionRate(user);
            boolean bossReady = questService.isBossReady(user);
            
            int totalTasks = tasks.size();
            int completedTasks = (int) tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.DONE)
                .count();
            
            QuestStatusResponse response = new QuestStatusResponse(
                taskResponses, completionRate, bossReady, totalTasks, completedTasks
            );
            
            return ResponseEntity.ok(ApiResponse.success("오늘의 일정을 조회했습니다.", response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 일정 완료 처리
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse> completeTask(
            @PathVariable Long taskId,
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "true") boolean isSuccess) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            Task task = questService.completeTask(user, taskId, isSuccess);
            TaskResponse response = new TaskResponse(task);
            
            String message = isSuccess ? "일정이 완료되었습니다." : "일정이 실패했습니다.";
            return ResponseEntity.ok(ApiResponse.success(message, response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 처리에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 일정 수정
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse> updateTask(
            @PathVariable Long taskId,
            @RequestParam Long userId,
            @Valid @RequestBody TaskRequest taskRequest) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            Task.TaskCategory category = Task.TaskCategory.valueOf(taskRequest.getCategory().toUpperCase());
            
            Task task = questService.updateTask(
                user,
                taskId,
                taskRequest.getTitle(),
                taskRequest.getMemo(),
                category,
                taskRequest.getTime()
            );
            
            TaskResponse response = new TaskResponse(task);
            return ResponseEntity.ok(ApiResponse.success("일정이 수정되었습니다.", response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 수정에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 일정 삭제
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable Long taskId,
            @RequestParam Long userId) {
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            questService.deleteTask(user, taskId);
            
            return ResponseEntity.ok(ApiResponse.success("일정이 삭제되었습니다.", null));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일정 삭제에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 보스전 가능 여부 확인
    @GetMapping("/boss-ready")
    public ResponseEntity<ApiResponse> isBossReady(@RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            boolean bossReady = questService.isBossReady(user);
            
            return ResponseEntity.ok(ApiResponse.success("보스전 가능 여부를 확인했습니다.", bossReady));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("보스전 가능 여부 확인에 실패했습니다: " + e.getMessage()));
        }
    }
}
