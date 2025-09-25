package com.questlog.service;

import com.questlog.entity.ShopItem;
import com.questlog.entity.User;
import com.questlog.entity.Inventory;
import com.questlog.repository.ShopItemRepository;
import com.questlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShopService {
    
    @Autowired
    private ShopItemRepository shopItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
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
    
    // 아이템 구매
    public boolean purchaseItem(Long userId, String itemId) {
        ShopItem shopItem = getShopItem(itemId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 골드 확인
        if (user.getGold() < shopItem.getPrice()) {
            throw new IllegalArgumentException("골드가 부족합니다.");
        }
        
        // 골드 차감
        gameService.spendGold(userId, shopItem.getPrice());
        
        // 아이템 타입에 따른 처리
        switch (shopItem.getType()) {
            case WEAPON:
                gameService.equipWeapon(userId, shopItem.getItemId(), 
                    shopItem.getName(), shopItem.getStatValue());
                break;
            case ARMOR:
                gameService.equipArmor(userId, shopItem.getItemId(), 
                    shopItem.getName(), shopItem.getStatValue());
                break;
            case POTION:
                if ("heal_potion".equals(itemId)) {
                    gameService.addPotion(userId, 1);
                }
                break;
            case PET:
                // 펫은 나중에 구현
                break;
        }
        
        return true;
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
