package com.questlog.service;

import com.questlog.entity.ShopItem;
import com.questlog.entity.User;
import com.questlog.entity.Inventory;
import com.questlog.repository.ShopItemRepository;
import com.questlog.repository.UserRepository;
import com.questlog.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class ShopService {
    
    @Autowired
    private ShopItemRepository shopItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private GameService gameService;
    
    // 모든 상점 아이템 조회
    public List<ShopItem> getAllShopItems() {
        return shopItemRepository.findAll();
    }
    
    // 타입별 상점 아이템 조회
    public List<ShopItem> getShopItemsByType(ShopItem.ItemType type) {
        return shopItemRepository.findByType(type);
    }
    
    // 희귀도별 상점 아이템 조회
    public List<ShopItem> getShopItemsByRarity(ShopItem.Rarity rarity) {
        return shopItemRepository.findByRarity(rarity);
    }
    
    // 특정 아이템 조회
    public ShopItem getShopItem(String itemId) {
        return shopItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("상점에 없는 아이템입니다."));
    }
    
    // 아이템 구매 (통합 구매 시스템)
    public Map<String, Object> buyItem(Long userId, String itemId) {
        ShopItem shopItem = getShopItem(itemId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 골드 확인
        if (user.getGold() < shopItem.getPrice()) {
            throw new IllegalArgumentException("골드가 부족합니다.");
        }
        
        // 골드 차감
        user.setGold(user.getGold() - shopItem.getPrice());
        userRepository.save(user);
        
        Inventory inventory = inventoryRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("인벤토리를 찾을 수 없습니다."));
        
        // 아이템 타입에 따른 처리
        switch (shopItem.getType()) {
            case WEAPON:
                // 무기 장착 및 상점 업그레이드
                inventory.setWeaponId(shopItem.getItemId());
                inventory.setWeaponName(shopItem.getName());
                inventory.setWeaponAtk(shopItem.getStatValue());
                inventoryRepository.save(inventory);
                upgradeShopItem(shopItem);
                break;
                
            case ARMOR:
                // 갑옷 장착 및 상점 업그레이드
                inventory.setArmorId(shopItem.getItemId());
                inventory.setArmorName(shopItem.getName());
                inventory.setArmorDef(shopItem.getStatValue());
                inventoryRepository.save(inventory);
                upgradeShopItem(shopItem);
                break;
                
            case POTION:
                // 포션 추가 (물약은 업그레이드 안 함)
                inventory.setPotions(inventory.getPotions() + 1);
                inventoryRepository.save(inventory);
                break;
                
            case PET:
                // 펫 추가 (JSON 배열)
                addPetToInventory(inventory, shopItem);
                // 최대 HP 증가
                user.setMaxHp(user.getMaxHp() + 50);
                user.setHp(user.getHp() + 50); // 현재 HP도 함께 증가
                userRepository.save(user);
                break;
        }
        
        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("playerGold", user.getGold());
        result.put("purchasedItem", shopItem);
        
        return result;
    }
    
    // 상점 아이템 업그레이드 (더 비싼 아이템으로 교체)
    private void upgradeShopItem(ShopItem purchasedItem) {
        // 현재 가격보다 더 비싼 같은 타입의 아이템 찾기
        List<ShopItem> availableUpgrades = shopItemRepository.findByType(purchasedItem.getType());
        
        ShopItem nextUpgrade = null;
        for (ShopItem item : availableUpgrades) {
            if (item.getPrice() > purchasedItem.getPrice()) {
                if (nextUpgrade == null || item.getPrice() < nextUpgrade.getPrice()) {
                    nextUpgrade = item;
                }
            }
        }
        
        // 다음 단계 아이템이 없으면 가장 비싼 아이템으로 유지
        if (nextUpgrade == null) {
            // 현재 상점에서 가장 비싼 아이템 찾기
            nextUpgrade = availableUpgrades.stream()
                .max((i1, i2) -> i1.getPrice().compareTo(i2.getPrice()))
                .orElse(null);
        }
        
        // 상점 아이템 업데이트 (물약 제외)
        if (nextUpgrade != null) {
            purchasedItem.setItemId(nextUpgrade.getItemId());
            purchasedItem.setName(nextUpgrade.getName());
            purchasedItem.setPrice(nextUpgrade.getPrice());
            purchasedItem.setStatValue(nextUpgrade.getStatValue());
            purchasedItem.setDescription(nextUpgrade.getDescription());
            purchasedItem.setRarity(nextUpgrade.getRarity());
            shopItemRepository.save(purchasedItem);
        }
    }
    
    // 펫을 인벤토리에 추가
    private void addPetToInventory(Inventory inventory, ShopItem petItem) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String petsJson = inventory.getPets();
            
            // JSON 파싱
            List<Map<String, Object>> petsList;
            if (petsJson == null || petsJson.trim().isEmpty()) {
                petsList = new ArrayList<>();
            } else {
                petsList = mapper.readValue(petsJson, new TypeReference<List<Map<String, Object>>>() {});
            }
            
            // 새로운 펫 추가
            Map<String, Object> newPet = new HashMap<>();
            newPet.put("itemId", petItem.getItemId());
            newPet.put("name", petItem.getName());
            newPet.put("rarity", petItem.getRarity().toString());
            newPet.put("statValue", petItem.getStatValue());
            
            petsList.add(newPet);
            
            // 다시 JSON으로 변환하여 저장
            String updatedPetsJson = mapper.writeValueAsString(petsList);
            inventory.setPets(updatedPetsJson);
            inventoryRepository.save(inventory);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("펫 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 아이템 구매 (레거시 - 호환성 유지)
    public boolean purchaseItem(Long userId, String itemId) {
        try {
            Map<String, Object> result = buyItem(userId, itemId);
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 상점 아이템 초기 데이터 생성 (애플리케이션 시작 시 호출)
    public void initializeShopItems() {
        // 이미 데이터가 있는지 확인
        if (shopItemRepository.count() > 0) {
            return;
        }
        
        // 가죽 갑옷
        ShopItem leatherArmor = new ShopItem(
            "leather_armor",
            ShopItem.ItemType.ARMOR,
            "가죽 갑옷",
            10,
            "def",
            5,
            ShopItem.Rarity.COMMON
        );
        leatherArmor.setDescription("기본적인 방어력을 제공하는 가죽 갑옷입니다.");
        
        // 나무 검
        ShopItem woodenSword = new ShopItem(
            "wooden_sword",
            ShopItem.ItemType.WEAPON,
            "나무 검",
            10,
            "atk",
            5,
            ShopItem.Rarity.COMMON
        );
        woodenSword.setDescription("단순하지만 실용적인 나무 검입니다.");
        
        // 체력 물약
        ShopItem healPotion = new ShopItem(
            "heal_potion",
            ShopItem.ItemType.POTION,
            "체력 물약",
            40,
            "heal",
            30,
            ShopItem.Rarity.NORMAL
        );
        healPotion.setDescription("HP를 30 회복시켜주는 물약입니다.");
        
        // 데이터베이스에 저장
        shopItemRepository.save(leatherArmor);
        shopItemRepository.save(woodenSword);
        shopItemRepository.save(healPotion);
    }
}
