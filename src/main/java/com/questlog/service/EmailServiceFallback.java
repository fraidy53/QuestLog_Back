package com.questlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "spring.data.redis.host", havingValue = "none", matchIfMissing = true)
public class EmailServiceFallback {
    
    @Autowired
    private JavaMailSender mailSender;
    
    // 인증 코드 저장소 (메모리 기반 - Redis 대체)
    private final ConcurrentHashMap<String, CodeInfo> codeStorage = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // 인증 코드 정보 클래스
    private static class CodeInfo {
        private final String code;
        private final long expiryTime;
        
        public CodeInfo(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
        
        public String getCode() { return code; }
        public boolean isExpired() { return System.currentTimeMillis() > expiryTime; }
    }
    
    // 6자리 랜덤 인증 코드 생성
    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    // 인증 코드 저장 (5분 만료)
    public void storeVerificationCode(String email, String code) {
        long expiryTime = System.currentTimeMillis() + (5 * 60 * 1000); // 5분
        codeStorage.put(email, new CodeInfo(code, expiryTime));
        
        // 만료된 코드 자동 삭제 (5분 후)
        scheduler.schedule(() -> {
            codeStorage.remove(email);
        }, 5, TimeUnit.MINUTES);
    }
    
    // 인증 코드 검증
    public boolean verifyCode(String email, String inputCode) {
        CodeInfo codeInfo = codeStorage.get(email);
        if (codeInfo == null || codeInfo.isExpired()) {
            return false;
        }
        return codeInfo.getCode().equals(inputCode);
    }
    
    // 인증된 이메일인지 확인
    public boolean isEmailVerified(String email) {
        CodeInfo codeInfo = codeStorage.get(email);
        return codeInfo != null && !codeInfo.isExpired();
    }
    
    // 인증 코드 삭제
    public void removeVerificationCode(String email) {
        codeStorage.remove(email);
    }
    
    // 이메일 전송
    public void sendVerificationEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("QuestLog 인증번호");
            message.setText("QuestLog 아이디 찾기 인증번호입니다.\n\n인증번호: " + code + "\n\n이 인증번호는 5분 후에 만료됩니다.\n\nQuestLog 팀");
            
            mailSender.send(message);
            
            // 성공 로그
            System.out.println("=== 이메일 전송 성공 (메모리 저장) ===");
            System.out.println("받는 사람: " + email);
            System.out.println("인증번호: " + code);
            System.out.println("메모리 저장 완료 (5분 만료)");
            System.out.println("================================");
            
        } catch (Exception e) {
            // 이메일 전송 실패 시 콘솔에 출력 (개발용)
            System.err.println("이메일 전송 실패: " + e.getMessage());
            System.out.println("=== 이메일 전송 (개발용 콘솔 출력) ===");
            System.out.println("받는 사람: " + email);
            System.out.println("인증번호: " + code);
            System.out.println("메모리 저장 완료 (5분 만료)");
            System.out.println("=====================================");
        }
    }
}
