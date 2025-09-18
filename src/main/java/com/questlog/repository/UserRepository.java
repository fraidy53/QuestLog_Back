package com.questlog.repository;

import com.questlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);
    
    // 아이디로 사용자 찾기
    Optional<User> findByUserId(String userId);
    
    // 이메일 중복 확인
    boolean existsByEmail(String email);
    
    // 아이디 중복 확인
    boolean existsByUserId(String userId);
    
    // 사용자 이름과 이메일로 사용자 찾기 (아이디 찾기용)
    Optional<User> findByUsernameAndEmail(String username, String email);
    
    // 사용자 이름, 아이디, 이메일로 사용자 찾기 (비밀번호 찾기용)
    Optional<User> findByUsernameAndUserIdAndEmail(String username, String userId, String email);
}
