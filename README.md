# spring-token-login

Spring Security OAuth2 모듈과 SPA 클라이언트를 사용해서 소셜(카카오) + 토큰 로그인을 구현한 예시입니다.

액세스 토큰은 self-contained (JWT), 리프레시 토큰은 reference (Opaque) 형식이고, refresh token rotation이 적용되었습니다.

## Setup

### 카카오 애플리케이션 설정

1. 애플리케이션 추가
2. 제품 설정 - 카카오 로그인 - Redirect URI에 아래 주소 추가
    - `http://localhost:5173/login/callback`
3. 제품 설정 - 카카오 로그인 - 동의항목 설정
    - 닉네임, 프로필 사진: 필수 동의
4. 제품 설정 - 카카오 로그인 - 보안 설정
    - Client Secret 코드 발급, 활성화

### 환경 변수 설정

```
MYSQL_USER=${MYSQL_USER}
MYSQL_PASSWORD=${MYSQL_PASSWORD}
KAKAO_CLIENT_ID='앱 설정 - 요약 정보 - 앱 키 - REST API 키
KAKAO_CLIENT_SECRET=제품 설정 - 카카오 로그인 - 보안 - Client Secret - 코드
```

### 실행

`spring-token-login-client`로 클라이언트 구현 예시가 제공됩니다.

- 카카오 로그인 경로: `http://localhost:8080/oauth2/authorization/kakao`
- 로그인 후 콜백 경로: `http://localhost:5173/login/callback?code=${authorization_code}&state=${state}`
- 토큰 요청 경로: `http://localhost:8080/login/oauth2/code/kakao?code=${authorization_code}&state=${state}`
- 로큰 갱신 경로: `http://localhost:8080/oauth2/token?grant_type=refresh_token&refresh_token=${refresh_token}`

## Customizing

### application.yml 설정

```yaml
client:
  base-url: 클라이언트 주소
spring:
  datasource:
    url: MySQL 주소 (데이터베이스 이름 포함)
  security:
    oauth2:
      client:
        registration:
          kakao:
            redirect-uri: 카카오 로그인 후 code와 state 전달받는 경로
            scope: 카카오 로그인 - 동의항목에서 사용 설정한 개인정보의 ID들
authorization:
  issuer: 인증 서버 주소, 서비스명 등 토큰 발급자 이름
  audience: API 서버 주소, 서비스명 등 토큰 사용자 이름
  access-token-time-to-live: 액세스 토큰 유효기간 (초)
  refresh-token-time-to-live: 리프레시 토큰 유효기간 (초)
```

### Configuration 설정

#### AuthorizationConfig

- `authorizationUri`: 로그인 시작 경로
  (기본값: `/oauth2/authorization`)
- `redirectionUri`: authorization_code 토큰 요청 경로
  (기본값: `/login/oauth2/code/*`)

#### JwtConfig

- 기본 설정은 HS256 알고리즘이고 대칭키가 하드코딩되어 있음
  - **키를 반드시 바꿔주세요!!!**

#### SecurityConfig

- 기본적인 시큐리티 설정

#### 기타

- `/oauth2/token` 경로는 `config.SecurityConfig`와 `token.TokenController` 수정

## Dependencies

- Spring Boot 3.2.3
- Spring Security
- Spring Security OAuth2 Client: 소셜 로그인 구현
- Spring Security OAuth2 Resource Server: API 엔드포인트 JWT로 보호
- Spring Data JPA: 사용자 로그인 정보 및 리프레시 토큰 목록 저장
- MySQL