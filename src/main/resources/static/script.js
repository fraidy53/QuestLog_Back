document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM 로드 완료');
    
    // DOM 요소들
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');
    const findIdForm = document.getElementById('findIdForm');
    const findPasswordForm = document.getElementById('findPasswordForm');
    const loginFormElement = document.getElementById('loginFormElement');
    const signupFormElement = document.getElementById('signupFormElement');
    const findIdFormElement = document.getElementById('findIdFormElement');
    const verifyCodeFormElement = document.getElementById('verifyCodeFormElement');
    const findPasswordFormElement = document.getElementById('findPasswordFormElement');
    const showSignupLink = document.getElementById('showSignup');
    const showLoginLink = document.getElementById('showLogin');
    const showFindIdLink = document.getElementById('showFindId');
    const showFindPasswordLink = document.getElementById('showFindPassword');
    const backToLoginLink = document.getElementById('backToLogin');
    const backToLoginFromPasswordLink = document.getElementById('backToLoginFromPassword');
    const resendCodeBtn = document.getElementById('resendCodeBtn');
    const goToLoginBtn = document.getElementById('goToLoginBtn');
    
    // DOM 요소 확인
    console.log('findIdForm:', findIdForm);
    console.log('showFindIdLink:', showFindIdLink);
    
    // 아이디 찾기 관련 변수
    let currentEmail = '';
    let currentUsername = '';
    
    // 폼 전환 이벤트
    showSignupLink.addEventListener('click', function(e) {
        e.preventDefault();
        hideAllForms();
        signupForm.style.display = 'block';
    });
    
    showLoginLink.addEventListener('click', function(e) {
        e.preventDefault();
        hideAllForms();
        loginForm.style.display = 'block';
    });
    
    showFindIdLink.addEventListener('click', function(e) {
        console.log('아이디 찾기 링크 클릭됨');
        e.preventDefault();
        hideAllForms();
        findIdForm.style.display = 'block';
        resetFindIdForm();
        console.log('아이디 찾기 폼 표시됨');
    });
    
    showFindPasswordLink.addEventListener('click', function(e) {
        e.preventDefault();
        hideAllForms();
        findPasswordForm.style.display = 'block';
        resetFindPasswordForm();
    });
    
    backToLoginLink.addEventListener('click', function(e) {
        e.preventDefault();
        hideAllForms();
        loginForm.style.display = 'block';
    });
    
    backToLoginFromPasswordLink.addEventListener('click', function(e) {
        e.preventDefault();
        hideAllForms();
        loginForm.style.display = 'block';
    });
    
    // 로그인 폼 제출 이벤트
    loginFormElement.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        clearErrors();
        hideResult('loginResult');
        
        const formData = {
            userId: document.getElementById('loginUserId').value.trim(),
            password: document.getElementById('loginPassword').value
        };
        
        if (!validateLoginForm(formData)) {
            return;
        }
        
        showLoading('login-btn', '로그인');
        
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                showResult('loginResult', 'success', `환영합니다, ${result.data}님!`);
                loginFormElement.reset();
            } else {
                showResult('loginResult', 'error', result.message);
            }
            
        } catch (error) {
            console.error('Error:', error);
            showResult('loginResult', 'error', '서버와의 통신 중 오류가 발생했습니다.');
        } finally {
            hideLoading('login-btn', '로그인');
        }
    });
    
    // 회원가입 폼 제출 이벤트
    signupFormElement.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        clearErrors();
        hideResult('signupResult');
        
        const formData = {
            username: document.getElementById('username').value.trim(),
            email: document.getElementById('email').value.trim(),
            userId: document.getElementById('userId').value.trim(),
            password: document.getElementById('password').value,
            confirmPassword: document.getElementById('confirmPassword').value
        };
        
        if (!validateSignupForm(formData)) {
            return;
        }
        
        showLoading('signup-btn', '회원가입');
        
        try {
            const response = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                showResult('signupResult', 'success', result.message);
                signupFormElement.reset();
                // 회원가입 성공 후 로그인 폼으로 전환
                setTimeout(() => {
                    signupForm.style.display = 'none';
                    loginForm.style.display = 'block';
                }, 2000);
            } else {
                showResult('signupResult', 'error', result.message);
            }
            
        } catch (error) {
            console.error('Error:', error);
            showResult('signupResult', 'error', '서버와의 통신 중 오류가 발생했습니다.');
        } finally {
            hideLoading('signup-btn', '회원가입');
        }
    });
    
    // 아이디 찾기 폼 제출 이벤트
    findIdFormElement.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        clearErrors();
        hideResult('findIdResult');
        
        const formData = {
            username: document.getElementById('findIdUsername').value.trim(),
            email: document.getElementById('findIdEmail').value.trim()
        };
        
        if (!validateFindIdForm(formData)) {
            return;
        }
        
        showLoading('find-id-btn', '인증번호 발송');
        
        try {
            const response = await fetch('/api/auth/find-id/send-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                showResult('findIdResult', 'success', result.message);
                currentEmail = formData.email;
                currentUsername = formData.username;
                showFindIdStep2();
            } else {
                showResult('findIdResult', 'error', result.message);
            }
            
        } catch (error) {
            console.error('Error:', error);
            showResult('findIdResult', 'error', '서버와의 통신 중 오류가 발생했습니다.');
        } finally {
            hideLoading('find-id-btn', '인증번호 발송');
        }
    });
    
    // 인증번호 확인 폼 제출 이벤트
    verifyCodeFormElement.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        clearErrors();
        hideResult('findIdResult');
        
        const formData = {
            email: currentEmail,
            code: document.getElementById('verificationCode').value.trim()
        };
        
        if (!validateVerificationCode(formData)) {
            return;
        }
        
        showLoading('verify-btn', '확인');
        
        try {
            const response = await fetch('/api/auth/find-id/verify-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                showResult('findIdResult', 'success', result.message);
                showFindIdStep3();
            } else {
                showResult('findIdResult', 'error', result.message);
            }
            
        } catch (error) {
            console.error('Error:', error);
            showResult('findIdResult', 'error', '서버와의 통신 중 오류가 발생했습니다.');
        } finally {
            hideLoading('verify-btn', '확인');
        }
    });
    
    // 인증번호 재발송 버튼 이벤트
    resendCodeBtn.addEventListener('click', async function() {
        if (!currentEmail || !currentUsername) {
            showResult('findIdResult', 'error', '사용자 정보가 없습니다.');
            return;
        }
        
        const formData = {
            username: currentUsername,
            email: currentEmail
        };
        
        showLoading('resend-btn', '재발송');
        
        try {
            const response = await fetch('/api/auth/find-id/send-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                showResult('findIdResult', 'success', '인증번호가 재발송되었습니다.');
            } else {
                showResult('findIdResult', 'error', result.message);
            }
            
        } catch (error) {
            console.error('Error:', error);
            showResult('findIdResult', 'error', '서버와의 통신 중 오류가 발생했습니다.');
        } finally {
            hideLoading('resend-btn', '재발송');
        }
    });
    
    // 로그인하기 버튼 이벤트
    goToLoginBtn.addEventListener('click', function() {
        hideAllForms();
        loginForm.style.display = 'block';
    });
    
    // 비밀번호 찾기 폼 제출 이벤트
    findPasswordFormElement.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        clearErrors();
        hideResult('findPasswordResult');
        
        const formData = {
            username: document.getElementById('findPasswordUsername').value.trim(),
            userId: document.getElementById('findPasswordUserId').value.trim(),
            email: document.getElementById('findPasswordEmail').value.trim()
        };
        
        if (!validateFindPasswordForm(formData)) {
            return;
        }
        
        showLoading('find-password-btn', '비밀번호 찾기');
        
        try {
            const response = await fetch('/api/auth/find-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                showResult('findPasswordResult', 'success', result.message);
            } else {
                showResult('findPasswordResult', 'error', result.message);
            }
            
        } catch (error) {
            console.error('Error:', error);
            showResult('findPasswordResult', 'error', '서버와의 통신 중 오류가 발생했습니다.');
        } finally {
            hideLoading('find-password-btn', '비밀번호 찾기');
        }
    });
    
    // 로그인 폼 유효성 검증
    function validateLoginForm(data) {
        let isValid = true;
        
        if (!data.userId) {
            showError('loginUserIdError', '아이디를 입력해주세요.');
            isValid = false;
        } else if (data.userId.length < 4 || data.userId.length > 20) {
            showError('loginUserIdError', '아이디는 4-20자 사이여야 합니다.');
            isValid = false;
        }
        
        if (!data.password) {
            showError('loginPasswordError', '비밀번호를 입력해주세요.');
            isValid = false;
        } else if (data.password.length < 6) {
            showError('loginPasswordError', '비밀번호는 최소 6자 이상이어야 합니다.');
            isValid = false;
        }
        
        return isValid;
    }
    
    // 회원가입 폼 유효성 검증
    function validateSignupForm(data) {
        let isValid = true;
        
        // 사용자 이름 검증
        if (!data.username) {
            showError('usernameError', '사용자 이름을 입력해주세요.');
            isValid = false;
        } else if (data.username.length < 2 || data.username.length > 50) {
            showError('usernameError', '사용자 이름은 2-50자 사이여야 합니다.');
            isValid = false;
        }
        
        // 이메일 검증
        if (!data.email) {
            showError('emailError', '이메일을 입력해주세요.');
            isValid = false;
        } else if (!isValidEmail(data.email)) {
            showError('emailError', '올바른 이메일 형식이 아닙니다.');
            isValid = false;
        }
        
        // 아이디 검증
        if (!data.userId) {
            showError('userIdError', '아이디를 입력해주세요.');
            isValid = false;
        } else if (data.userId.length < 4 || data.userId.length > 20) {
            showError('userIdError', '아이디는 4-20자 사이여야 합니다.');
            isValid = false;
        }
        
        // 비밀번호 검증
        if (!data.password) {
            showError('passwordError', '비밀번호를 입력해주세요.');
            isValid = false;
        } else if (data.password.length < 6) {
            showError('passwordError', '비밀번호는 최소 6자 이상이어야 합니다.');
            isValid = false;
        }
        
        // 비밀번호 확인 검증
        if (!data.confirmPassword) {
            showError('confirmPasswordError', '비밀번호 확인을 입력해주세요.');
            isValid = false;
        } else if (data.password !== data.confirmPassword) {
            showError('confirmPasswordError', '비밀번호가 일치하지 않습니다.');
            isValid = false;
        }
        
        return isValid;
    }
    
    // 이메일 형식 검증
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
    // 에러 메시지 표시
    function showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
        }
    }
    
    // 모든 에러 메시지 초기화
    function clearErrors() {
        const errorElements = document.querySelectorAll('.error');
        errorElements.forEach(element => {
            element.textContent = '';
        });
    }
    
    // 결과 메시지 표시
    function showResult(resultId, type, message) {
        const resultDiv = document.getElementById(resultId);
        if (resultDiv) {
            resultDiv.className = `result ${type}`;
            resultDiv.textContent = message;
            resultDiv.style.display = 'block';
        }
    }
    
    // 결과 메시지 숨기기
    function hideResult(resultId) {
        const resultDiv = document.getElementById(resultId);
        if (resultDiv) {
            resultDiv.style.display = 'none';
        }
    }
    
    // 로딩 상태 표시
    function showLoading(buttonClass, originalText) {
        const submitBtn = document.querySelector(`.${buttonClass}`);
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="loading"></span>처리 중...';
        }
    }
    
    // 로딩 상태 숨기기
    function hideLoading(buttonClass, originalText) {
        const submitBtn = document.querySelector(`.${buttonClass}`);
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    }
    
    // 모든 폼 숨기기
    function hideAllForms() {
        console.log('모든 폼 숨기기 실행');
        loginForm.style.display = 'none';
        signupForm.style.display = 'none';
        findIdForm.style.display = 'none';
        findPasswordForm.style.display = 'none';
    }
    
    // 아이디 찾기 폼 초기화
    function resetFindIdForm() {
        document.getElementById('findIdStep1').style.display = 'block';
        document.getElementById('findIdStep2').style.display = 'none';
        document.getElementById('findIdStep3').style.display = 'none';
        document.getElementById('findIdUsername').value = '';
        document.getElementById('findIdEmail').value = '';
        document.getElementById('verificationCode').value = '';
        currentEmail = '';
        currentUsername = '';
        clearErrors();
        hideResult('findIdResult');
    }
    
    // 아이디 찾기 2단계 표시
    function showFindIdStep2() {
        document.getElementById('findIdStep1').style.display = 'none';
        document.getElementById('findIdStep2').style.display = 'block';
    }
    
    // 아이디 찾기 3단계 표시 및 결과 로드
    async function showFindIdStep3() {
        try {
            const response = await fetch(`/api/auth/find-id/result?email=${encodeURIComponent(currentEmail)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });
            
            const result = await response.json();
            
            if (result.success && result.data) {
                document.getElementById('resultUsername').textContent = result.data.username;
                document.getElementById('resultUserId').textContent = result.data.userId;
                document.getElementById('resultEmail').textContent = result.data.email;
                
                document.getElementById('findIdStep2').style.display = 'none';
                document.getElementById('findIdStep3').style.display = 'block';
            } else {
                showResult('findIdResult', 'error', result.message || '아이디 찾기 중 오류가 발생했습니다.');
            }
            
        } catch (error) {
            console.error('Error:', error);
            showResult('findIdResult', 'error', '서버와의 통신 중 오류가 발생했습니다.');
        }
    }
    
    // 아이디 찾기 폼 유효성 검증
    function validateFindIdForm(data) {
        let isValid = true;
        
        if (!data.username || data.username.length < 2) {
            showError('findIdUsernameError', '사용자 이름은 2자 이상이어야 합니다.');
            isValid = false;
        }
        
        if (!data.email || !isValidEmail(data.email)) {
            showError('findIdEmailError', '올바른 이메일 형식이 아닙니다.');
            isValid = false;
        }
        
        return isValid;
    }
    
    // 인증번호 유효성 검증
    function validateVerificationCode(data) {
        let isValid = true;
        
        if (!data.code || !/^\d{6}$/.test(data.code)) {
            showError('verificationCodeError', '6자리 숫자를 입력해주세요.');
            isValid = false;
        }
        
        return isValid;
    }
    
    // 비밀번호 찾기 폼 초기화
    function resetFindPasswordForm() {
        document.getElementById('findPasswordUsername').value = '';
        document.getElementById('findPasswordUserId').value = '';
        document.getElementById('findPasswordEmail').value = '';
        clearErrors();
        hideResult('findPasswordResult');
    }
    
    // 비밀번호 찾기 폼 유효성 검증
    function validateFindPasswordForm(data) {
        let isValid = true;
        
        if (!data.username || data.username.length < 2) {
            showError('findPasswordUsernameError', '사용자 이름은 2자 이상이어야 합니다.');
            isValid = false;
        }
        
        if (!data.userId || data.userId.length < 4) {
            showError('findPasswordUserIdError', '아이디는 4자 이상이어야 합니다.');
            isValid = false;
        }
        
        if (!data.email || !isValidEmail(data.email)) {
            showError('findPasswordEmailError', '올바른 이메일 형식이 아닙니다.');
            isValid = false;
        }
        
        return isValid;
    }
});