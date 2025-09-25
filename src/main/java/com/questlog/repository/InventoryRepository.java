package com.questlog.repository;

import com.questlog.entity.Inventory;
import com.questlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    // 사용자 ID로 인벤토리 찾기
    Optional<Inventory> findByUser(User user);
    
    // 사용자 ID로 인벤토리 존재 확인
    boolean existsByUser(User user);
}
