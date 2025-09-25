package com.questlog.repository;

import com.questlog.entity.UserStatus;
import com.questlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    
    // 사용자로 상태 찾기
    Optional<UserStatus> findByUser(User user);
    
    // 사용자로 상태 존재 확인
    boolean existsByUser(User user);
}
