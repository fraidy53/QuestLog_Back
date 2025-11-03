package com.questlog.service;

import com.questlog.dto.*;
import com.questlog.entity.*;
import com.questlog.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class BattleService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MonsterRepository monsterRepository;
    
    @Autowired
    private BattleLogRepository battleLogRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private GameService gameService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 전투 상태 저장 (임시 - 실제로는 Redis 등에 저장해야 하지만 현재는 메모리에 저장)
    private final Map<Long, BattleState> battleStates = new HashMap<>();
    
    // 몬스터 초기 데이터 생성
    public void initializeMonsters() {
        // 이미 데이터가 있는지 확인
        if (monsterRepository.count() > 0) {
            return;
        }
        
        // 기본 몬스터 3마리 생성 (실제 능력치는 사용자 레벨에 따라 계산됨)
        Monster monster1 = new Monster(200, 20, "강아지");
        Monster monster2 = new Monster(250, 35, "고양이");
        Monster monster3 = new Monster(300, 50, "토끼");
        
        monsterRepository.save(monster1);
        monsterRepository.save(monster2);
        monsterRepository.save(monster3);
    }
    
    // 전투 상태 클래스
    private static class BattleState {
        Long userId;
        Long monsterId;
        Integer userHp;
        Integer monsterHp;
        Integer userAtk;
        Integer userDef;
        Integer monsterAtk; // 몬스터 공격력
        List<Map<String, Object>> damageLog;
        BattleLog battleLog;
        
        BattleState(Long userId, Long monsterId, Integer userHp, Integer monsterHp, 
                   Integer userAtk, Integer userDef, Integer monsterAtk, BattleLog battleLog) {
            this.userId = userId;
            this.monsterId = monsterId;
            this.userHp = userHp;
            this.monsterHp = monsterHp;
            this.userAtk = userAtk;
            this.userDef = userDef;
            this.monsterAtk = monsterAtk;
            this.damageLog = new ArrayList<>();
            this.battleLog = battleLog;
        }
    }
    
    // 전투 시작
    public BattleStartResponse startBattle(Long userId, Long monsterId) {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 몬스터 정보 조회
        Monster monster = monsterRepository.findById(monsterId)
            .orElseThrow(() -> new IllegalArgumentException("몬스터를 찾을 수 없습니다."));
        
        // 사용자 능력치 계산
        Inventory inventory = inventoryRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("인벤토리를 찾을 수 없습니다."));
        
        int baseAtk = 5 + (user.getLevel() - 1) * 5; // 기본 공격력
        int userAtk = baseAtk + inventory.getWeaponAtk(); // 무기 공격력 추가
        
        int baseDef = 3; // 기본 방어력
        int userDef = baseDef + inventory.getArmorDef(); // 갑옷 방어력 추가
        
        // 몬스터 능력치는 레벨에 따라 계산 (DB에는 저장되지 않고 계산)
        int[] monsterStats = calculateMonsterStats(user.getLevel());
        int monsterHp = monsterStats[0];
        int monsterAtk = monsterStats[1];
        
        // 몬스터 정보 업데이트
        monster.setHp(monsterHp);
        monster.setDef(monsterAtk); // DB 필드명이 def이지만 실제로는 공격력을 저장
        monsterRepository.save(monster);
        
        // 전투 로그 생성 (전투 종료 시에만 DB에 저장되도록 임시 생성)
        BattleLog battleLog = new BattleLog(user, monster);
        battleLog.setFinalUserHp(user.getHp());
        battleLog.setFinalMonsterHp(monsterHp);
        // status는 전투 종료 시 설정됨
        // battleLog는 아직 DB에 저장하지 않음
        
        // battleId는 전투 상태에서 사용하기 위해 임시로 생성
        // 실제 DB 저장은 전투 종료 시 수행
        Long tempBattleId = System.currentTimeMillis(); // 임시 ID
        
        // 전투 상태 저장
        BattleState battleState = new BattleState(
            userId, monsterId, user.getHp(), monsterHp, 
            userAtk, userDef, monsterAtk, battleLog
        );
        battleStates.put(tempBattleId, battleState);
        
        // 응답 생성
        Map<String, Integer> userStats = new HashMap<>();
        userStats.put("hp", user.getHp());
        userStats.put("maxHp", user.getMaxHp());
        userStats.put("atk", userAtk);
        userStats.put("def", userDef);
        
        Map<String, Integer> monsterStatsMap = new HashMap<>();
        monsterStatsMap.put("hp", monsterHp);
        monsterStatsMap.put("atk", monsterAtk);
        
        Map<String, Object> equippedItems = new HashMap<>();
        equippedItems.put("weapon", inventory.getWeaponName());
        equippedItems.put("armor", inventory.getArmorName());
        
        return new BattleStartResponse(
            tempBattleId, 
            userStats, 
            monsterStatsMap, 
            equippedItems
        );
    }
    
    // 전투 진행
    public BattleActionResponse processBattleTurn(Long battleId, String action, String userItem) {
        BattleState battleState = battleStates.get(battleId);
        if (battleState == null) {
            throw new IllegalArgumentException("전투를 찾을 수 없습니다.");
        }
        
        // 사용자와 몬스터 정보 조회
        User user = userRepository.findById(battleState.userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Monster monster = monsterRepository.findById(battleState.monsterId)
            .orElseThrow(() -> new IllegalArgumentException("몬스터를 찾을 수 없습니다."));
        
        Map<String, Object> turnResult = new HashMap<>();
        List<Map<String, Object>> turnLogs = new ArrayList<>();
        
        // 사용자 행동 처리
        if ("attack".equals(action)) {
            // 사용자 공격 (사용자 공격력 vs 몬스터 방어력 0으로 가정)
            int damage = calculateDamage(battleState.userAtk, 0);
            int hpBefore = battleState.monsterHp;
            battleState.monsterHp = Math.max(0, battleState.monsterHp - damage);
            int hpAfter = battleState.monsterHp;
            
            Map<String, Object> userAction = new HashMap<>();
            userAction.put("actor", "user");
            userAction.put("action", "attack");
            userAction.put("damage", damage);
            userAction.put("hpBefore", hpBefore);
            userAction.put("hpAfter", hpAfter);
            turnLogs.add(userAction);
            
            // 몬스터 HP 업데이트
            monster.setHp(battleState.monsterHp);
            monsterRepository.save(monster);
        }
        
        // 포션 사용 처리
        if (userItem != null && userItem.equals("potion")) {
            Inventory inventory = inventoryRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("인벤토리를 찾을 수 없습니다."));
            
            if (inventory.getPotions() > 0) {
                inventory.setPotions(inventory.getPotions() - 1);
                inventoryRepository.save(inventory);
                
                int healAmount = 30;
                int hpBefore = battleState.userHp;
                battleState.userHp = Math.min(user.getMaxHp(), battleState.userHp + healAmount);
                int hpAfter = battleState.userHp;
                
                // 사용자 HP 업데이트
                user.setHp(battleState.userHp);
                userRepository.save(user);
                
                Map<String, Object> potionAction = new HashMap<>();
                potionAction.put("actor", "user");
                potionAction.put("action", "use_potion");
                potionAction.put("heal", healAmount);
                potionAction.put("hpBefore", hpBefore);
                potionAction.put("hpAfter", hpAfter);
                turnLogs.add(potionAction);
            }
        }
        
        // 전투 종료 체크 (사용자 승리)
        if (battleState.monsterHp <= 0) {
            return endBattle(battleId, "win");
        }
        
        // 몬스터 행동 (공격)
        int monsterDamage = calculateDamage(battleState.monsterAtk, battleState.userDef);
        int userHpBefore = battleState.userHp;
        battleState.userHp = Math.max(0, battleState.userHp - monsterDamage);
        int userHpAfter = battleState.userHp;
        
        Map<String, Object> monsterAction = new HashMap<>();
        monsterAction.put("actor", "monster");
        monsterAction.put("action", "attack");
        monsterAction.put("damage", monsterDamage);
        monsterAction.put("hpBefore", userHpBefore);
        monsterAction.put("hpAfter", userHpAfter);
        turnLogs.add(monsterAction);
        
        // 사용자 HP 업데이트
        user.setHp(battleState.userHp);
        userRepository.save(user);
        
        // 전투 종료 체크 (사용자 패배)
        if (battleState.userHp <= 0) {
            return endBattle(battleId, "lose");
        }
        
        // 데미지 로그에 추가
        battleState.damageLog.addAll(turnLogs);
        
        turnResult.put("logs", turnLogs);
        
        BattleActionResponse response = new BattleActionResponse(
            battleState.userHp, 
            battleState.monsterHp, 
            turnResult
        );
        
        return response;
    }
    
    // 전투 종료 처리
    private BattleActionResponse endBattle(Long battleId, String result) {
        BattleState battleState = battleStates.get(battleId);
        if (battleState == null) {
            throw new IllegalArgumentException("전투를 찾을 수 없습니다.");
        }
        
        User user = userRepository.findById(battleState.userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Monster monster = monsterRepository.findById(battleState.monsterId)
            .orElseThrow(() -> new IllegalArgumentException("몬스터를 찾을 수 없습니다."));
        
        List<String> drops = new ArrayList<>();
        int expGain = 0;
        int goldGain = 0;
        
        if ("win".equals(result)) {
            // 승리 시 보상
            int[] rewards = calculateRewards(user.getLevel());
            expGain = rewards[0];
            goldGain = rewards[1];
            
            // 경험치와 골드 추가
            gameService.addExpAndGold(user, expGain, goldGain);
            
            // 펫 드랍 (레벨 3 이상, 40% 확률)
            if (user.getLevel() >= 3 && Math.random() < 0.4) {
                String[] pets = {"강아지", "고양이", "토끼"};
                String droppedPet = pets[(int)(Math.random() * pets.length)];
                drops.add(droppedPet);
                
                // 인벤토리에 펫 추가
                addPetToInventory(user, droppedPet);
            }
        } else {
            // 패배 시 30G 빼앗김
            goldGain = -30;
            if (user.getGold() >= 30) {
                gameService.spendGold(battleState.userId, 30);
            } else {
                user.setGold(0);
                userRepository.save(user);
            }
        }
        
        // BattleLog 생성 및 저장 (전투 종료 시에만 DB에 저장)
        BattleLog battleLog = new BattleLog(user, monster);
        battleLog.setFinalUserHp(battleState.userHp);
        battleLog.setFinalMonsterHp(battleState.monsterHp);
        battleLog.setStatus("win".equals(result) ? BattleLog.BattleStatus.WIN : BattleLog.BattleStatus.LOSE);
        
        // 데미지 로그 저장
        try {
            battleLog.setDamageLog(objectMapper.writeValueAsString(battleState.damageLog));
        } catch (JsonProcessingException e) {
            battleLog.setDamageLog("[]");
        }
        
        // 보상 정보 저장
        Map<String, Object> rewardInfo = new HashMap<>();
        rewardInfo.put("exp", expGain);
        rewardInfo.put("gold", goldGain);
        rewardInfo.put("drops", drops);
        
        try {
            battleLog.setReward(objectMapper.writeValueAsString(rewardInfo));
        } catch (JsonProcessingException e) {
            battleLog.setReward("{}");
        }
        
        // DB에 저장
        battleLogRepository.save(battleLog);
        
        // 전투 상태 제거
        battleStates.remove(battleId);
        
        // 응답 생성
        Map<String, Object> turnResult = new HashMap<>();
        turnResult.put("result", result);
        turnResult.put("exp", expGain);
        turnResult.put("gold", goldGain);
        turnResult.put("drops", drops);
        
        BattleActionResponse response = new BattleActionResponse(
            battleState.userHp, 
            battleState.monsterHp, 
            turnResult
        );
        response.setBattleEnded(true);
        response.setBattleResult(result);
        
        return response;
    }
    
    // 데미지 계산
    private int calculateDamage(int attack, int defense) {
        int baseDamage = Math.max(1, attack - defense);
        // 랜덤 요소 추가 (80% ~ 120%)
        double multiplier = 0.8 + (Math.random() * 0.4);
        return (int) Math.max(1, baseDamage * multiplier);
    }
    
    // 레벨에 따른 몬스터 능력치 계산 (hp, atk 반환)
    private int[] calculateMonsterStats(int userLevel) {
        int baseHp = 200;
        int baseAtk = 20;
        
        if (userLevel <= 5) {
            return new int[]{baseHp, baseAtk};
        } else if (userLevel <= 10) {
            return new int[]{baseHp + 50, baseAtk + 15};
        } else if (userLevel <= 15) {
            return new int[]{baseHp + 100, baseAtk + 30};
        } else {
            // 5레벨마다 +50hp, +15atk
            int levelGroup = (userLevel - 1) / 5;
            return new int[]{baseHp + (levelGroup * 50), baseAtk + (levelGroup * 15)};
        }
    }
    
    // 레벨에 따른 보상 계산
    private int[] calculateRewards(int userLevel) {
        int baseExp = 30;
        int baseGold = 30;
        
        if (userLevel <= 5) {
            return new int[]{baseExp, baseGold};
        } else if (userLevel <= 10) {
            return new int[]{baseExp + 10, baseGold + 10};
        } else if (userLevel <= 15) {
            return new int[]{baseExp + 20, baseGold + 20};
        } else {
            // 5레벨마다 +10exp, +10G
            int levelGroup = (userLevel - 1) / 5;
            return new int[]{baseExp + (levelGroup * 10), baseGold + (levelGroup * 10)};
        }
    }
    
    // 인벤토리에 펫 추가
    private void addPetToInventory(User user, String petName) {
        Inventory inventory = inventoryRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("인벤토리를 찾을 수 없습니다."));
        
        try {
            List<String> pets = new ArrayList<>();
            if (inventory.getPets() != null && !inventory.getPets().isEmpty()) {
                pets = Arrays.asList(objectMapper.readValue(inventory.getPets(), String[].class));
                pets = new ArrayList<>(pets);
            }
            pets.add(petName);
            inventory.setPets(objectMapper.writeValueAsString(pets));
            inventoryRepository.save(inventory);
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패 시 무시
        }
    }
}

