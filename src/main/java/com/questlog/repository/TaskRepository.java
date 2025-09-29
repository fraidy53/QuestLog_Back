package com.questlog.repository;

import com.questlog.entity.Task;
import com.questlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // 사용자별 모든 일정 조회
    List<Task> findByUserOrderByDateAscDueTimeAsc(User user);
    
    // 사용자별 특정 날짜의 일정 조회
    List<Task> findByUserAndDateOrderByDueTimeAsc(User user, LocalDate date);
    
    // 사용자별 특정 날짜의 완료된 일정 조회
    List<Task> findByUserAndDateAndStatusOrderByDueTimeAsc(User user, LocalDate date, Task.TaskStatus status);
    
    // 사용자별 특정 날짜의 완료되지 않은 일정 조회
    List<Task> findByUserAndDateAndStatusNotOrderByDueTimeAsc(User user, LocalDate date, Task.TaskStatus status);
    
    // 사용자별 오늘 날짜의 모든 일정 조회
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.date = :today ORDER BY t.dueTime ASC")
    List<Task> findTodayTasksByUser(@Param("user") User user, @Param("today") LocalDate today);
    
    // 사용자별 오늘 날짜의 완료된 일정 개수 조회
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.date = :today AND t.status = 'DONE'")
    Long countCompletedTasksToday(@Param("user") User user, @Param("today") LocalDate date);
    
    // 사용자별 오늘 날짜의 전체 일정 개수 조회
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.date = :today")
    Long countTotalTasksToday(@Param("user") User user, @Param("today") LocalDate date);
    
    // 사용자별 특정 날짜 이전의 모든 일정 삭제 (일일 리셋용)
    void deleteByUserAndDateBefore(User user, LocalDate date);
    
    // 특정 일정 조회 (사용자와 함께)
    Optional<Task> findByTaskIdAndUser(Long taskId, User user);
}
