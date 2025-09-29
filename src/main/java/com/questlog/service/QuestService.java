package com.questlog.service;

import com.questlog.entity.Task;
import com.questlog.entity.User;
import com.questlog.entity.UserStatus;
import com.questlog.repository.TaskRepository;
import com.questlog.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserStatusRepository userStatusRepository;
    
    @Autowired
    private GameService gameService;
    
    // 일정당 경험치와 골드
    private static final int TASK_EXP = 10;
    private static final int TASK_GOLD = 5;
    private static final int BONUS_EXP = 50;
    private static final int BONUS_GOLD = 25;
    
    // 일정 생성
    public Task createTask(User user, String title, String memo, Task.TaskCategory category, 
                          LocalDate date, LocalTime dueTime) {
        // 일일 리셋 체크
        checkAndResetDaily(user);
        
        Task task = new Task(user, title, memo, category, date, dueTime);
        return taskRepository.save(task);
    }
    
    // 사용자별 일정 조회
    public List<Task> getUserTasks(User user) {
        checkAndResetDaily(user);
        return taskRepository.findByUserOrderByDateAscDueTimeAsc(user);
    }
    
    // 특정 날짜의 일정 조회
    public List<Task> getTasksByDate(User user, LocalDate date) {
        checkAndResetDaily(user);
        return taskRepository.findByUserAndDateOrderByDueTimeAsc(user, date);
    }
    
    // 오늘의 일정 조회
    public List<Task> getTodayTasks(User user) {
        checkAndResetDaily(user);
        return taskRepository.findTodayTasksByUser(user, LocalDate.now());
    }
    
    // 일정 완료 처리
    public Task completeTask(User user, Long taskId) {
        checkAndResetDaily(user);
        
        Optional<Task> taskOpt = taskRepository.findByTaskIdAndUser(taskId, user);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("일정을 찾을 수 없습니다.");
        }
        
        Task task = taskOpt.get();
        if (task.getStatus() == Task.TaskStatus.DONE) {
            throw new IllegalArgumentException("이미 완료된 일정입니다.");
        }
        
        // 일정 완료 처리
        task.setStatus(Task.TaskStatus.DONE);
        Task savedTask = taskRepository.save(task);
        
        // 경험치와 골드 지급
        gameService.addExpAndGold(user, TASK_EXP, TASK_GOLD);
        
        // 오늘의 모든 일정 완료 여부 체크
        checkAllTasksCompleted(user);
        
        return savedTask;
    }
    
    // 일정 삭제
    public void deleteTask(User user, Long taskId) {
        checkAndResetDaily(user);
        
        Optional<Task> taskOpt = taskRepository.findByTaskIdAndUser(taskId, user);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("일정을 찾을 수 없습니다.");
        }
        
        taskRepository.delete(taskOpt.get());
    }
    
    // 일정 수정
    public Task updateTask(User user, Long taskId, String title, String memo, 
                          Task.TaskCategory category, LocalTime dueTime) {
        checkAndResetDaily(user);
        
        Optional<Task> taskOpt = taskRepository.findByTaskIdAndUser(taskId, user);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("일정을 찾을 수 없습니다.");
        }
        
        Task task = taskOpt.get();
        task.setTitle(title);
        task.setMemo(memo);
        task.setCategory(category);
        task.setDueTime(dueTime);
        
        return taskRepository.save(task);
    }
    
    // 일일 리셋 체크 및 실행
    private void checkAndResetDaily(User user) {
        UserStatus userStatus = userStatusRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("사용자 상태를 찾을 수 없습니다."));
        
        if (userStatus.isNewDay()) {
            // 이전 날짜의 모든 일정 삭제
            taskRepository.deleteByUserAndDateBefore(user, LocalDate.now());
            
            // 일일 상태 리셋
            userStatus.resetDaily();
            userStatusRepository.save(userStatus);
        }
    }
    
    // 모든 일정 완료 여부 체크 및 보너스 지급
    private void checkAllTasksCompleted(User user) {
        LocalDate today = LocalDate.now();
        Long totalTasks = taskRepository.countTotalTasksToday(user, today);
        Long completedTasks = taskRepository.countCompletedTasksToday(user, today);
        
        if (totalTasks > 0 && totalTasks.equals(completedTasks)) {
            // 모든 일정 완료 - 보너스 지급
            gameService.addExpAndGold(user, BONUS_EXP, BONUS_GOLD);
            
            // 보스전 가능 플래그 설정
            UserStatus userStatus = userStatusRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("사용자 상태를 찾을 수 없습니다."));
            userStatus.setBossReady(true);
            userStatusRepository.save(userStatus);
        }
    }
    
    // 보스전 가능 여부 확인
    public boolean isBossReady(User user) {
        checkAndResetDaily(user);
        
        UserStatus userStatus = userStatusRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("사용자 상태를 찾을 수 없습니다."));
        
        return userStatus.getBossReady();
    }
    
    // 오늘의 일정 완료율 조회
    public double getTodayCompletionRate(User user) {
        checkAndResetDaily(user);
        
        LocalDate today = LocalDate.now();
        Long totalTasks = taskRepository.countTotalTasksToday(user, today);
        Long completedTasks = taskRepository.countCompletedTasksToday(user, today);
        
        if (totalTasks == 0) {
            return 0.0;
        }
        
        return (double) completedTasks / totalTasks * 100;
    }
}
