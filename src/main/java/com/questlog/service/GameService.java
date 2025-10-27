package com.questlog.service;

import com.questlog.entity.User;
import com.questlog.entity.Inventory;
import com.questlog.entity.UserStatus;
import com.questlog.entity.ShopItem;
import com.questlog.repository.UserRepository;
import com.questlog.repository.InventoryRepository;
import com.questlog.repository.UserStatusRepository;
import com.questlog.repository.ShopItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class GameService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private UserStatusRepository userStatusRepository;
    
    @Autowired
    private ShopItemRepository shopItemRepository;
    
    // 사용자 게임 정보 초기화 (회원가입 시 호출)
    public void initializeGameData(User user) {
        // 인벤토리 초기화
        Inventory inventory = new Inventory(user);
        inventoryRepository.save(inventory);
        
        // 사용자 상태 초기화
        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
    }
    
    // 사용자 게임 정보 조회
    public User getUserGameInfo(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
    
    // 인벤토리 정보 조회
    public Inventory getUserInventory(Long userId) {
        User user = getUserGameInfo(userId);
        return inventoryRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("인벤토리를 찾을 수 없습니다."));
    }
    
    // 사용자 상태 조회
    public UserStatus getUserStatus(Long userId) {
        User user = getUserGameInfo(userId);
        return userStatusRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("사용자 상태를 찾을 수 없습니다."));
    }
    
    // 경험치 추가 및 레벨업 처리
    public void addExperience(Long userId, Integer expGain) {
        User user = getUserGameInfo(userId);
        user.setExp(user.getExp() + expGain);
        
        // 레벨업 체크
        checkLevelUp(user);
        
        userRepository.save(user);
    }
    
    // 레벨업 체크 및 처리
    private void checkLevelUp(User user) {
        while (user.getExp() >= user.getNextExp()) {
            user.setExp(user.getExp() - user.getNextExp());
            user.setLevel(user.getLevel() + 1);
            
            // 다음 레벨업에 필요한 경험치 계산 (레벨당 +50씩 증가)
            int nextExp = 100 + (user.getLevel() - 1) * 50;
            user.setNextExp(nextExp);
            
            // 레벨업 시 최대 HP와 공격력 증가
            // 최대 HP: +10
            // 공격력: 레벨당 +5 (기본 스탯에 반영됨)
            user.setMaxHp(user.getMaxHp() + 10);
            user.setHp(user.getMaxHp()); // 레벨업 시 HP 풀회복
        }
    }
    
    // 레벨에 따른 기본 공격력 계산
    public int calculateBaseAttack(Integer level) {
        return 5 + (level - 1) * 5;
    }
    
    // 골드 추가
    public void addGold(Long userId, Integer goldGain) {
        User user = getUserGameInfo(userId);
        user.setGold(user.getGold() + goldGain);
        userRepository.save(user);
    }
    
    // 경험치와 골드 함께 추가 (일정 완료 시 사용)
    public void addExpAndGold(User user, Integer expGain, Integer goldGain) {
        user.setExp(user.getExp() + expGain);
        user.setGold(user.getGold() + goldGain);
        
        // 레벨업 체크
        checkLevelUp(user);
        
        userRepository.save(user);
    }
    
    // 레벨별 보상 계산 (일정 완료 시)
    public int calculateTaskExp(Integer level) {
        // 레벨에 따라 경험치 1씩 증가
        if (level <= 5) {
            // 1레벨: 10exp, 5레벨: 14exp
            return 10 + (level - 1);
        } else if (level <= 10) {
            // 6레벨: 16exp, 10레벨: 20exp
            return 16 + (level - 6);
        } else if (level <= 15) {
            // 11레벨: 20exp, 15레벨: 24exp
            return 20 + (level - 11);
        } else {
            // 16레벨 이상: 25exp부터 시작
            return 25 + (level - 16);
        }
    }
    
    public int calculateTaskGold(Integer level) {
        // 1~5레벨: 5G, 6~10레벨: 10G, 11~15레벨: 15G
        // 5레벨 단위로 골드 5G씩 증가
        if (level <= 5) {
            return 5;
        } else if (level <= 10) {
            return 10;
        } else if (level <= 15) {
            return 15;
        } else {
            // 16레벨 이상
            return 20 + ((level - 16) / 5) * 5;
        }
    }
    
    // 일정 실패 처리 (체력 감소, 아이템 손실)
    public void handleTaskFailure(User user) {
        int damage = 30;
        
        // 먼저 데미지를 받음
        int newHp = Math.max(user.getHp() - damage, 0);
        user.setHp(newHp);
        userRepository.save(user);
        
        // 체력이 0 이하가 되면 아이템 하나 잃고 체력 회복
        if (newHp <= 0) {
            loseRandomItem(user.getId());
            
            // 체력 회복
            user.setHp(user.getMaxHp());
            userRepository.save(user);
        }
    }
    
    // 랜덤 아이템 손실
    private void loseRandomItem(Long userId) {
        Inventory inventory = getUserInventory(userId);
        
        // 인벤토리에서 무기나 갑옷 중 하나 제거
        if (inventory.getWeaponAtk() > 2 && Math.random() < 0.5) {
            // 무기 초기화
            inventory.setWeaponId("starting_weapon");
            inventory.setWeaponName("스타팅 무기");
            inventory.setWeaponAtk(2);
        } else if (inventory.getArmorDef() > 2) {
            // 갑옷 초기화
            inventory.setArmorId("starting_armor");
            inventory.setArmorName("스타팅 갑옷");
            inventory.setArmorDef(2);
        }
        
        inventoryRepository.save(inventory);
    }
    
    // 골드 소모
    public boolean spendGold(Long userId, Integer goldCost) {
        User user = getUserGameInfo(userId);
        if (user.getGold() >= goldCost) {
            user.setGold(user.getGold() - goldCost);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    // HP 회복
    public void healHp(Long userId, Integer healAmount) {
        User user = getUserGameInfo(userId);
        int newHp = Math.min(user.getHp() + healAmount, user.getMaxHp());
        user.setHp(newHp);
        userRepository.save(user);
    }
    
    // 데미지 받기
    public void takeDamage(Long userId, Integer damage) {
        User user = getUserGameInfo(userId);
        int newHp = Math.max(user.getHp() - damage, 0);
        user.setHp(newHp);
        userRepository.save(user);
    }
    
    // 포션 사용
    public boolean usePotion(Long userId) {
        Inventory inventory = getUserInventory(userId);
        if (inventory.getPotions() > 0) {
            inventory.setPotions(inventory.getPotions() - 1);
            inventoryRepository.save(inventory);
            
            // HP 30 회복
            healHp(userId, 30);
            return true;
        }
        return false;
    }
    
    // 포션 추가
    public void addPotion(Long userId, Integer amount) {
        Inventory inventory = getUserInventory(userId);
        inventory.setPotions(inventory.getPotions() + amount);
        inventoryRepository.save(inventory);
    }
    
    // 하루 리셋 체크 및 처리
    public void checkDailyReset(Long userId) {
        UserStatus userStatus = getUserStatus(userId);
        
        if (userStatus.isNewDay()) {
            userStatus.resetDaily();
            userStatusRepository.save(userStatus);
            
            // 하루 보너스 (골드 50, 포션 1개)
            addGold(userId, 50);
            addPotion(userId, 1);
        }
    }
    
    // 무기 장착
    public void equipWeapon(Long userId, String weaponId, String weaponName, Integer weaponAtk) {
        Inventory inventory = getUserInventory(userId);
        inventory.setWeaponId(weaponId);
        inventory.setWeaponName(weaponName);
        inventory.setWeaponAtk(weaponAtk);
        inventoryRepository.save(inventory);
    }
    
    // 갑옷 장착
    public void equipArmor(Long userId, String armorId, String armorName, Integer armorDef) {
        Inventory inventory = getUserInventory(userId);
        inventory.setArmorId(armorId);
        inventory.setArmorName(armorName);
        inventory.setArmorDef(armorDef);
        inventoryRepository.save(inventory);
    }
    
    // 아이템 장착 처리 (새로운 통합 장착 시스템)
    public Map<String, Object> equipItem(Long userId, String itemId) {
        // 1. 아이템 정보 조회
        ShopItem shopItem = shopItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));
        
        // 2. 플레이어 인벤토리 확인 (현재는 구매한 아이템만 장착 가능하도록 간단히 처리)
        // 실제로는 별도의 인벤토리 테이블이 있어야 하지만, 현재 구조에서는 구매한 아이템을 바로 장착
        
        // 3. 장착 슬롯 업데이트
        Inventory inventory = getUserInventory(userId);
        User user = getUserGameInfo(userId);
        
        // 아이템 타입에 따른 장착 처리
        switch (shopItem.getType()) {
            case WEAPON:
                inventory.setWeaponId(shopItem.getItemId());
                inventory.setWeaponName(shopItem.getName());
                inventory.setWeaponAtk(shopItem.getStatValue());
                break;
            case ARMOR:
                inventory.setArmorId(shopItem.getItemId());
                inventory.setArmorName(shopItem.getName());
                inventory.setArmorDef(shopItem.getStatValue());
                // 펫 착용 시 최대 HP 증가 (갑옷이 펫이면)
                increaseMaxHpForPet(user, shopItem);
                break;
            case PET:
                // 펫 착용 시 최대 HP +50
                increaseMaxHpForPet(user, shopItem);
                break;
            default:
                throw new IllegalArgumentException("장착할 수 없는 아이템 타입입니다.");
        }
        
        inventoryRepository.save(inventory);
        
        // 4. 플레이어 스탯 계산
        Map<String, Object> result = new HashMap<>();
        Map<String, String> equipped = new HashMap<>();
        Map<String, Integer> stats = new HashMap<>();
        
        // 장착된 아이템 정보
        equipped.put("weapon", inventory.getWeaponId());
        equipped.put("armor", inventory.getArmorId());
        
        // 기본 스탯 + 장착 아이템 스탯
        int baseAtk = calculateBaseAttack(user.getLevel()); // 레벨별 기본 공격력
        int baseDef = 3; // 기본 방어력
        int baseHp = 100; // 기본 HP
        
        stats.put("atk", baseAtk + inventory.getWeaponAtk());
        stats.put("def", baseDef + inventory.getArmorDef());
        stats.put("hp", user.getHp());
        stats.put("maxHp", user.getMaxHp());
        
        result.put("success", true);
        result.put("equipped", equipped);
        result.put("stats", stats);
        
        return result;
    }
    
    // 현재 장착 상태와 스탯 조회
    public Map<String, Object> getEquippedStatus(Long userId) {
        Inventory inventory = getUserInventory(userId);
        User user = getUserGameInfo(userId);
        
        Map<String, Object> result = new HashMap<>();
        Map<String, String> equipped = new HashMap<>();
        Map<String, Integer> stats = new HashMap<>();
        
        // 장착된 아이템 정보
        equipped.put("weapon", inventory.getWeaponId());
        equipped.put("armor", inventory.getArmorId());
        
        // 기본 스탯 + 장착 아이템 스탯
        int baseAtk = calculateBaseAttack(user.getLevel()); // 레벨별 기본 공격력
        int baseDef = 3; // 기본 방어력
        
        stats.put("atk", baseAtk + inventory.getWeaponAtk());
        stats.put("def", baseDef + inventory.getArmorDef());
        stats.put("hp", user.getHp());
        stats.put("maxHp", user.getMaxHp());
        
        result.put("success", true);
        result.put("equipped", equipped);
        result.put("stats", stats);
        
        return result;
    }
    
    // 펫 장착 시 최대 HP 증가 (+50)
    private void increaseMaxHpForPet(User user, ShopItem petItem) {
        // 펫은 statType이 "hp_boost"로 되어 있을 것으로 가정
        if ("hp_boost".equals(petItem.getStatType()) || petItem.getType() == ShopItem.ItemType.PET) {
            // 최대 HP +50 증가
            user.setMaxHp(user.getMaxHp() + 50);
            user.setHp(user.getHp() + 50); // 현재 HP도 함께 증가
            userRepository.save(user);
        }
    }
}
