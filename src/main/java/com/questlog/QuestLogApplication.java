package com.questlog;

import com.questlog.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuestLogApplication implements CommandLineRunner {

    @Autowired
    private ShopService shopService;

    public static void main(String[] args) {
        SpringApplication.run(QuestLogApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션 시작 시 상점 데이터 초기화
        shopService.initializeShopItems();
    }
}
