package com.questlog.repository;

import com.questlog.entity.BattleLog;
import com.questlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BattleLogRepository extends JpaRepository<BattleLog, Long> {
    List<BattleLog> findByUser(User user);
}

