package com.questlog.controller;

import com.questlog.dto.ApiResponse;
import com.questlog.entity.User;
import com.questlog.entity.Inventory;
import com.questlog.entity.UserStatus;
import com.questlog.entity.ShopItem;
import com.questlog.service.GameService;
import com.questlog.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private ShopService shopService;
    
    // 사용자 게임 정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserGameInfo(@PathVariable Long userId) {
        try {
            User user = gameService.getUserGameInfo(userId);
            Inventory inventory = gameService.getUserInventory(userId);
            UserStatus userStatus = gameService.getUserStatus(userId);
            
            Map<String, Object> gameData = new HashMap<>();
            
            // 사용자 기본 정보
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());
            userInfo.put("hp", user.getHp());
            userInfo.put("maxHp", user.getMaxHp());
            userInfo.put("exp", user.getExp());
            userInfo.put("nextExp", user.getNextExp());
            userInfo.put("level", user.getLevel());
            userInfo.put("gold", user.getGold());
            
            // 인벤토리 정보
            Map<String, Object> inventoryInfo = new HashMap<>();
            Map<String, Object> weapon = new HashMap<>();
            weapon.put("id", inventory.getWeaponId());
            weapon.put("name", inventory.getWeaponName());
            weapon.put("atk", inventory.getWeaponAtk());
            
            Map<String, Object> armor = new HashMap<>();
            armor.put("id", inventory.getArmorId());
            armor.put("name", inventory.getArmorName());
            armor.put("def", inventory.getArmorDef());
            
            inventoryInfo.put("weapon", weapon);
            inventoryInfo.put("armor", armor);
            inventoryInfo.put("potions", inventory.getPotions());
            inventoryInfo.put("pets", inventory.getPets());
            
            // 상태 정보
            Map<String, Object> statusInfo = new HashMap<>();
            statusInfo.put("lastDailyReset", userStatus.getLastDailyReset());
            statusInfo.put("dailyQuestsCompleted", userStatus.getDailyQuestsCompleted());
            statusInfo.put("dailyLoginCount", userStatus.getDailyLoginCount());
            
            gameData.put("user", userInfo);
            gameData.put("inventory", inventoryInfo);
            gameData.put("status", statusInfo);
            
            return ResponseEntity.ok(new ApiResponse(true, "게임 정보 조회 성공", gameData));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "게임 정보 조회 실패: " + e.getMessage(), null));
        }
    }
    
    // 경험치 추가
    @PostMapping("/user/{userId}/exp")
    public ResponseEntity<ApiResponse> addExperience(@PathVariable Long userId, 
                                                           @RequestParam Integer exp) {
        try {
            gameService.addExperience(userId, exp);
            return ResponseEntity.ok(new ApiResponse(true, "경험치 추가 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "경험치 추가 실패: " + e.getMessage(), null));
        }
    }
    
    // 골드 추가
    @PostMapping("/user/{userId}/gold")
    public ResponseEntity<ApiResponse> addGold(@PathVariable Long userId, 
                                                     @RequestParam Integer gold) {
        try {
            gameService.addGold(userId, gold);
            return ResponseEntity.ok(new ApiResponse(true, "골드 추가 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "골드 추가 실패: " + e.getMessage(), null));
        }
    }
    
    // HP 회복
    @PostMapping("/user/{userId}/heal")
    public ResponseEntity<ApiResponse> healHp(@PathVariable Long userId, 
                                                    @RequestParam Integer healAmount) {
        try {
            gameService.healHp(userId, healAmount);
            return ResponseEntity.ok(new ApiResponse(true, "HP 회복 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "HP 회복 실패: " + e.getMessage(), null));
        }
    }
    
    // 포션 사용
    @PostMapping("/user/{userId}/use-potion")
    public ResponseEntity<ApiResponse> usePotion(@PathVariable Long userId) {
        try {
            boolean success = gameService.usePotion(userId);
            if (success) {
                return ResponseEntity.ok(new ApiResponse(true, "포션 사용 성공", null));
            } else {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "포션이 부족합니다", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "포션 사용 실패: " + e.getMessage(), null));
        }
    }
    
    // 하루 리셋 체크
    @PostMapping("/user/{userId}/daily-reset")
    public ResponseEntity<ApiResponse> checkDailyReset(@PathVariable Long userId) {
        try {
            gameService.checkDailyReset(userId);
            return ResponseEntity.ok(new ApiResponse(true, "하루 리셋 체크 완료", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "하루 리셋 체크 실패: " + e.getMessage(), null));
        }
    }
    
    // 상점 아이템 목록 조회
    @GetMapping("/shop/items")
    public ResponseEntity<ApiResponse> getShopItems() {
        try {
            List<ShopItem> items = shopService.getAllShopItems();
            return ResponseEntity.ok(new ApiResponse(true, "상점 아이템 조회 성공", items));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "상점 아이템 조회 실패: " + e.getMessage(), null));
        }
    }
    
    // 타입별 상점 아이템 조회
    @GetMapping("/shop/items/type/{type}")
    public ResponseEntity<ApiResponse> getShopItemsByType(@PathVariable String type) {
        try {
            ShopItem.ItemType itemType = ShopItem.ItemType.valueOf(type.toUpperCase());
            List<ShopItem> items = shopService.getShopItemsByType(itemType);
            return ResponseEntity.ok(new ApiResponse(true, "상점 아이템 조회 성공", items));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "상점 아이템 조회 실패: " + e.getMessage(), null));
        }
    }
    
    // 아이템 구매
    @PostMapping("/shop/purchase")
    public ResponseEntity<ApiResponse> purchaseItem(@RequestParam Long userId, 
                                                          @RequestParam String itemId) {
        try {
            boolean success = shopService.purchaseItem(userId, itemId);
            if (success) {
                return ResponseEntity.ok(new ApiResponse(true, "아이템 구매 성공", null));
            } else {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "아이템 구매 실패", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "아이템 구매 실패: " + e.getMessage(), null));
        }
    }
}
