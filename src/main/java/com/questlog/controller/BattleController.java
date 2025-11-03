package com.questlog.controller;

import com.questlog.dto.*;
import com.questlog.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/battle")
@CrossOrigin(origins = "*")
public class BattleController {
    
    @Autowired
    private BattleService battleService;
    
    // 전투 시작
    @PostMapping("/start")
    public ResponseEntity<ApiResponse> startBattle(@RequestBody BattleStartRequest request) {
        try {
            BattleStartResponse response = battleService.startBattle(
                request.getUserId(), 
                request.getMonsterId()
            );
            return ResponseEntity.ok(ApiResponse.success("전투가 시작되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("전투 시작에 실패했습니다: " + e.getMessage()));
        }
    }
    
    // 전투 진행
    @PostMapping("/action")
    public ResponseEntity<ApiResponse> processBattleAction(@RequestBody BattleActionRequest request) {
        try {
            BattleActionResponse response = battleService.processBattleTurn(
                request.getBattleId(),
                request.getAction(),
                request.getUserItem()
            );
            
            if (response.isBattleEnded()) {
                // 전투 종료 응답
                Map<String, Object> turnResult = response.getTurnResult();
                BattleEndResponse endResponse = new BattleEndResponse(
                    response.getBattleResult(),
                    turnResult.get("exp") != null ? (Integer) turnResult.get("exp") : 0,
                    turnResult.get("gold") != null ? (Integer) turnResult.get("gold") : 0,
                    turnResult.get("drops") != null ? (java.util.List<String>) turnResult.get("drops") : new java.util.ArrayList<>()
                );
                return ResponseEntity.ok(ApiResponse.success("전투가 종료되었습니다.", endResponse));
            } else {
                // 전투 진행 중 응답
                return ResponseEntity.ok(ApiResponse.success("턴이 진행되었습니다.", response));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("전투 진행에 실패했습니다: " + e.getMessage()));
        }
    }
}

