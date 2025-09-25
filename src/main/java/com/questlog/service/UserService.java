package com.questlog.service;

import com.questlog.dto.FindIdRequest;
import com.questlog.dto.FindIdResult;
import com.questlog.dto.FindPasswordRequest;
import com.questlog.dto.LoginRequest;
import com.questlog.dto.SignupRequest;
import com.questlog.entity.User;
import com.questlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private GameService gameService;
    
    public User signup(SignupRequest signupRequest) {
        // 비밀번호 일치 확인
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 아이디 중복 확인
        if (userRepository.existsByUserId(signupRequest.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        
        // 새 사용자 생성
        User user = new User(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            signupRequest.getUserId(),
            signupRequest.getPassword() // 실제로는 암호화해야 함
        );
        
        // 사용자 저장
        User savedUser = userRepository.save(user);
        
        // 게임 데이터 초기화
        gameService.initializeGameData(savedUser);
        
        return savedUser;
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }
    
    public User login(LoginRequest loginRequest) {
        // 아이디로 사용자 찾기
        User user = userRepository.findByUserId(loginRequest.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        
        // 비밀번호 확인
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        return user;
    }
    
    // 아이디 찾기 - 사용자 정보 확인 및 인증 코드 발송
    public void sendFindIdVerificationCode(FindIdRequest findIdRequest) {
        // 사용자 이름과 이메일로 사용자 찾기
        User user = userRepository.findByUsernameAndEmail(findIdRequest.getUsername(), findIdRequest.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 계정을 찾을 수 없습니다."));
        
        // 인증 코드 생성 및 저장
        String verificationCode = emailService.generateVerificationCode();
        emailService.storeVerificationCode(findIdRequest.getEmail(), verificationCode);
        
        // 이메일 전송
        emailService.sendVerificationEmail(findIdRequest.getEmail(), verificationCode);
    }
    
    // 인증 코드 검증
    public boolean verifyFindIdCode(String email, String code) {
        return emailService.verifyCode(email, code);
    }
    
    // 아이디 찾기 결과 반환
    public FindIdResult findUserId(String email) {
        // 이메일이 인증되었는지 확인
        if (!emailService.isEmailVerified(email)) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 등록된 계정을 찾을 수 없습니다."));
        
        // 인증 코드 삭제 (일회성)
        emailService.removeVerificationCode(email);
        
        return new FindIdResult(user.getUserId(), user.getUsername(), user.getEmail());
    }
    
    // 비밀번호 찾기 - 사용자 정보 확인 및 비밀번호 이메일 전송
    public void sendPasswordByEmail(FindPasswordRequest findPasswordRequest) {
        // 사용자 이름, 아이디, 이메일로 사용자 찾기
        User user = userRepository.findByUsernameAndUserIdAndEmail(
            findPasswordRequest.getUsername(), 
            findPasswordRequest.getUserId(), 
            findPasswordRequest.getEmail()
        ).orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 계정을 찾을 수 없습니다."));
        
        // 이메일로 현재 비밀번호 전송
        emailService.sendPasswordEmail(findPasswordRequest.getEmail(), user.getPassword(), user.getUsername());
    }
}
