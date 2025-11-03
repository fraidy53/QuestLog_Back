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
    
    // 보너스 보상 (하루 일정 모두 완료)
    private static final int BONUS_EXP = 50;
    private static final int BONUS_GOLD = 15;
    
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
    public Task completeTask(User user, Long taskId, boolean isSuccess) {
        checkAndResetDaily(user);
        
        Optional<Task> taskOpt = taskRepository.findByTaskIdAndUser(taskId, user);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("일정을 찾을 수 없습니다.");
        }
        
        Task task = taskOpt.get();
        
        // 이미 완료된 일정인지 체크
        if (task.getStatus() == Task.TaskStatus.DONE || task.getStatus() == Task.TaskStatus.FAIL) {
            throw new IllegalArgumentException("이미 처리된 일정입니다.");
        }
        
        Task savedTask;
        
        if (isSuccess) {
            // 일정 성공 처리
            task.setStatus(Task.TaskStatus.DONE);
            savedTask = taskRepository.save(task);
            
            // 레벨별 보상 계산
            int expGain = gameService.calculateTaskExp(user.getLevel());
            int goldGain = gameService.calculateTaskGold(user.getLevel());
            
            // 경험치와 골드 지급
            gameService.addExpAndGold(user, expGain, goldGain);
            
            // 오늘의 모든 일정 완료 여부 체크
            checkAllTasksCompleted(user);
        } else {
            // 일정 실패 처리
            task.setStatus(Task.TaskStatus.FAIL);
            savedTask = taskRepository.save(task);
            
            // 체력 감소 및 아이템 손실 처리
            gameService.handleTaskFailure(user);
        }
        
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
        
        // 오늘 날짜의 모든 일정 조회
        List<Task> tasks = taskRepository.findTodayTasksByUser(user, today);
        
        // 성공한 일정만 카운트 (FAIL은 제외)
        long completedTasks = tasks.stream()
            .filter(task -> task.getStatus() == Task.TaskStatus.DONE)
            .count();
        
        if (totalTasks > 0 && totalTasks == completedTasks && tasks.size() == totalTasks) {
            // 모든 일정 성공 완료 - 보너스 지급 (50exp, 15G)
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
    
    // 일정 완료 처리 (보상 수치 반환)
    public RewardResult completeTaskWithReward(User user, Long taskId) {
        checkAndResetDaily(user);
        
        Optional<Task> taskOpt = taskRepository.findByTaskIdAndUser(taskId, user);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("일정을 찾을 수 없습니다.");
        }
        
        Task task = taskOpt.get();
        
        // 이미 완료된 일정인지 체크
        if (task.getStatus() == Task.TaskStatus.DONE || task.getStatus() == Task.TaskStatus.FAIL) {
            throw new IllegalArgumentException("이미 처리된 일정입니다.");
        }
        
        // 일정 성공 처리
        task.setStatus(Task.TaskStatus.DONE);
        taskRepository.save(task);
        
        // 레벨별 보상 계산
        int expGain = gameService.calculateTaskExp(user.getLevel());
        int goldGain = gameService.calculateTaskGold(user.getLevel());
        
        // 경험치와 골드 지급
        gameService.addExpAndGold(user, expGain, goldGain);
        
        // 오늘의 모든 일정 완료 여부 체크
        checkAllTasksCompleted(user);
        
        return new RewardResult(expGain, goldGain);
    }
    
    // 일정 실패 처리
    public Task failTask(User user, Long taskId) {
        checkAndResetDaily(user);
        
        Optional<Task> taskOpt = taskRepository.findByTaskIdAndUser(taskId, user);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("일정을 찾을 수 없습니다.");
        }
        
        Task task = taskOpt.get();
        
        // 이미 완료된 일정인지 체크
        if (task.getStatus() == Task.TaskStatus.DONE || task.getStatus() == Task.TaskStatus.FAIL) {
            throw new IllegalArgumentException("이미 처리된 일정입니다.");
        }
        
        // 일정 실패 처리
        task.setStatus(Task.TaskStatus.FAIL);
        
        // 보상 없음 (패널티는 필요시 추가 가능)
        
        return taskRepository.save(task);
    }
    
    // 오늘 일정 최대 6개 조회
    public List<Task> getTodayTasksLimit(User user, int limit) {
        checkAndResetDaily(user);
        List<Task> tasks = taskRepository.findTodayTasksByUser(user, LocalDate.now());
        return tasks.stream()
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }
    
    // 보상 결과 클래스
    public static class RewardResult {
        private int exp;
        private int gold;
        
        public RewardResult(int exp, int gold) {
            this.exp = exp;
            this.gold = gold;
        }
        
        public int getExp() {
            return exp;
        }
        
        public int getGold() {
            return gold;
        }
    }
}
