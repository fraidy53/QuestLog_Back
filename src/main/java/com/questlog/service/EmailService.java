package com.questlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "spring.data.redis.host", havingValue = "localhost")
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis 키 접두사
    private static final String VERIFICATION_CODE_PREFIX = "verification_code:";
    private static final String VERIFIED_EMAIL_PREFIX = "verified_email:";
    
    // 인증 코드 만료 시간 (5분)
    private static final long CODE_EXPIRY_MINUTES = 5;
    
    // 6자리 랜덤 인증 코드 생성
    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    // 인증 코드 저장 (Redis에 5분 만료)
    public void storeVerificationCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRY_MINUTES, TimeUnit.MINUTES);
    }
    
    // 인증 코드 검증
    public boolean verifyCode(String email, String inputCode) {
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            return false; // 코드가 없거나 만료됨
        }
        
        boolean isValid = storedCode.equals(inputCode);
        
        if (isValid) {
            // 인증 성공 시 코드 삭제하고 인증 상태 저장
            redisTemplate.delete(key);
            String verifiedKey = VERIFIED_EMAIL_PREFIX + email;
            redisTemplate.opsForValue().set(verifiedKey, "verified", CODE_EXPIRY_MINUTES, TimeUnit.MINUTES);
        }
        
        return isValid;
    }
    
    // 인증된 이메일인지 확인
    public boolean isEmailVerified(String email) {
        String key = VERIFIED_EMAIL_PREFIX + email;
        return redisTemplate.hasKey(key);
    }
    
    // 인증 코드 삭제
    public void removeVerificationCode(String email) {
        String codeKey = VERIFICATION_CODE_PREFIX + email;
        String verifiedKey = VERIFIED_EMAIL_PREFIX + email;
        redisTemplate.delete(codeKey);
        redisTemplate.delete(verifiedKey);
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
            System.out.println("=== 이메일 전송 성공 ===");
            System.out.println("받는 사람: " + email);
            System.out.println("인증번호: " + code);
            System.out.println("Redis 저장 완료 (5분 만료)");
            System.out.println("========================");
            
        } catch (Exception e) {
            // 이메일 전송 실패 시 콘솔에 출력 (개발용)
            System.err.println("이메일 전송 실패: " + e.getMessage());
            System.out.println("=== 이메일 전송 (개발용 콘솔 출력) ===");
            System.out.println("받는 사람: " + email);
            System.out.println("인증번호: " + code);
            System.out.println("Redis 저장 완료 (5분 만료)");
            System.out.println("=====================================");
        }
    }
    
    // 비밀번호 이메일 전송
    public void sendPasswordEmail(String email, String password, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("QuestLog 비밀번호 찾기");
            message.setText("안녕하세요 " + username + "님,\n\nQuestLog 비밀번호 찾기 요청이 접수되었습니다.\n\n현재 비밀번호: " + password + "\n\n보안을 위해 로그인 후 비밀번호를 변경해주시기 바랍니다.\n\nQuestLog 팀");
            
            mailSender.send(message);
            
            // 성공 로그
            System.out.println("=== 비밀번호 이메일 전송 성공 ===");
            System.out.println("받는 사람: " + email);
            System.out.println("사용자: " + username);
            System.out.println("비밀번호: " + password);
            System.out.println("================================");
            
        } catch (Exception e) {
            // 이메일 전송 실패 시 콘솔에 출력 (개발용)
            System.err.println("비밀번호 이메일 전송 실패: " + e.getMessage());
            System.out.println("=== 비밀번호 이메일 전송 (개발용 콘솔 출력) ===");
            System.out.println("받는 사람: " + email);
            System.out.println("사용자: " + username);
            System.out.println("비밀번호: " + password);
            System.out.println("==========================================");
        }
    }
}
