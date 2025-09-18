package com.questlog.controller;

import com.questlog.dto.ApiResponse;
import com.questlog.dto.FindIdRequest;
import com.questlog.dto.FindIdResult;
import com.questlog.dto.FindPasswordRequest;
import com.questlog.dto.LoginRequest;
import com.questlog.dto.SignupRequest;
import com.questlog.dto.VerifyCodeRequest;
import com.questlog.entity.User;
import com.questlog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // CORS 설정 (개발용)
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            // 회원가입 처리
            User user = userService.signup(signupRequest);
            
            // 성공 응답
            ApiResponse response = ApiResponse.success(
                "회원가입이 성공적으로 완료되었습니다.",
                user.getUsername()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            // 비즈니스 로직 오류 (중복, 비밀번호 불일치 등)
            ApiResponse response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            // 기타 서버 오류
            ApiResponse response = ApiResponse.error("회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        String message = exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.";
        
        ApiResponse response = new ApiResponse(!exists, message);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check-userid")
    public ResponseEntity<ApiResponse> checkUserId(@RequestParam String userId) {
        boolean exists = userService.existsByUserId(userId);
        String message = exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.";
        
        ApiResponse response = new ApiResponse(!exists, message);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 로그인 처리
            User user = userService.login(loginRequest);
            
            // 성공 응답 (실제로는 JWT 토큰 등을 반환해야 함)
            ApiResponse response = ApiResponse.success(
                "로그인이 성공적으로 완료되었습니다.",
                user.getUsername()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 로그인 실패 (아이디 없음, 비밀번호 불일치 등)
            ApiResponse response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            
        } catch (Exception e) {
            // 기타 서버 오류
            ApiResponse response = ApiResponse.error("로그인 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/find-id/send-code")
    public ResponseEntity<ApiResponse> sendFindIdCode(@Valid @RequestBody FindIdRequest findIdRequest) {
        try {
            // 인증 코드 발송
            userService.sendFindIdVerificationCode(findIdRequest);
            
            ApiResponse response = ApiResponse.success(
                "인증번호가 이메일로 발송되었습니다. 5분 내에 입력해주세요."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("인증번호 발송 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/find-id/verify-code")
    public ResponseEntity<ApiResponse> verifyFindIdCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        try {
            // 인증 코드 검증
            boolean isValid = userService.verifyFindIdCode(verifyCodeRequest.getEmail(), verifyCodeRequest.getCode());
            
            if (isValid) {
                ApiResponse response = ApiResponse.success("인증이 완료되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse response = ApiResponse.error("인증번호가 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("인증 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/find-id/result")
    public ResponseEntity<ApiResponse> getFindIdResult(@RequestParam String email) {
        try {
            // 아이디 찾기 결과 반환
            FindIdResult result = userService.findUserId(email);
            
            ApiResponse response = ApiResponse.success(
                "아이디 찾기가 완료되었습니다.",
                result
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("아이디 찾기 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/find-password")
    public ResponseEntity<ApiResponse> findPassword(@Valid @RequestBody FindPasswordRequest findPasswordRequest) {
        try {
            // 비밀번호 찾기 처리
            userService.sendPasswordByEmail(findPasswordRequest);
            
            ApiResponse response = ApiResponse.success(
                "비밀번호가 이메일로 전송되었습니다. " + findPasswordRequest.getEmail() + "을 확인해주세요."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("비밀번호 찾기 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
