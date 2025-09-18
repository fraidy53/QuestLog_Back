# QuestLog

Spring Boot를 사용한 퀘스트 로그 애플리케이션입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web**
- **Spring Data JPA**
- **H2 Database** (인메모리)
- **Gradle**

## 실행 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. JAR 파일로 실행
```bash
./gradlew build
java -jar build/libs/questlog-0.0.1-SNAPSHOT.jar
```

### 3. Windows CMD에서 실행
```cmd
gradlew.bat bootRun
```

## API 엔드포인트

- `GET /api/health` - 애플리케이션 상태 확인
- `GET /api/quests` - 퀘스트 목록 조회

## 데이터베이스

- H2 인메모리 데이터베이스 사용
- H2 콘솔: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:questlog`
  - Username: `sa`
  - Password: (비어있음)

## 개발 환경

- 포트: 8080
- 컨텍스트 패스: /
- 로깅 레벨: DEBUG
