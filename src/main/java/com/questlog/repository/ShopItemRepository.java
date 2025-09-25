package com.questlog.repository;

import com.questlog.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, String> {
    
    // 아이템 타입별로 조회
    List<ShopItem> findByType(ShopItem.ItemType type);
    
    // 희귀도별로 조회
    List<ShopItem> findByRarity(ShopItem.Rarity rarity);
    
    // 가격 범위로 조회
    List<ShopItem> findByPriceBetween(Integer minPrice, Integer maxPrice);
    
    // 아이템 타입과 희귀도로 조회
    List<ShopItem> findByTypeAndRarity(ShopItem.ItemType type, ShopItem.Rarity rarity);
}
