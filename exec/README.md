# Porting manual

## 0. 준비물

- Docker
- Docker Compose
- 도메인
- EC2
  

## 1. git clone

EC2에서 진행한다.  

```
git clone https://lab.ssafy.com/s11-fintech-finance-sub1/S11P21D102.git
```
  
## 2. HTTPS 인증서

`exec`에서 실행  
```
docker compose -f ./cert/cert-compose.yml up
```

`cert/data/certbot/conf`에 인증서가 생성되었다면 성공한 것.  

인증서 생성 후 컨테이너를 내린다.  

## 3. DB 설치

`exec`에서 실행.  
```
docker-compose -f ./db/db-compose.yml up -d
```
  

## 4. 배포

`exec`에서 실행.  
```
docker-compose -f ./app/app-compose.yml up -d
```
  

## 5. AI server

<br>

## 6. WebRTC Signaling server

<br>


## 7. Jenkins - CI/CD

`exec`에서 실행.  
```
docker-compose -f ./jenkins/jenkins-compose.yml up -d
```

실행 후 Jenkins 설정.  

- Item 생성
- Plugins 설치
- Credentials 추가

---

# 버전


### Android
- Android Studio : 2024.1.1
- Android min SDK : 28
- Android target SDK : 34
- Android max SDK : 34

### Backend
- OpenJDK : 17.0.12
- Spring Boot : 3.3.3
- Spring Data JPA : 3.3.3
- JUnit : 5.10.3
- Nginx : 1.27.2
- MySQL : mysql:8.4.1
- Node.js : 20.18.0
- FastAPI : 0.115.0

### CI/CD
- Jenkins : jenkins/jenkins:2.462.2-jdk17
- docker : 27.1.1
- docker compose : 2.29.1

### Front
- Alpine.js : 3.14.1

### AI
- NVIDIA GPU Driver : 560.94
- Torch : 2.3.1
- Torch Vision : 0.18.1
- MSVC : 143
- CUDA : 12.1
- cuDNN : 9.3.0
- AI Model : TPS ResNet BiLSTM Attn

<br>

---
# 외부 서비스
<br>

- Firebase Cloud Messaging
- 카카오 Oauth
- SSAFY 금융 API

<br>

---
# 환경변수

```
# MOBI SERVER
MOBI_DB= mobipay
MOBI_PASSWORD=
MOBI_USER=mobi

SSAFY_API_KEY=

# MERCHANT SERVER
MER_DB=merchant
MER_PASSWORD=
MER_USER=mer

# 공통
DDL_AUTO_OPTION=none
JWT_SECRET_KEY=

# KAKAO OAUTH
KAKAO_CLIENT_NAME=MobyPay
KAKAO_CLIENT_ID=
KAKAO_CLIENT_SECRET=
KAKAO_REDIRECT_URI=https://mobipay.kr/api/v1/login/oauth2/code/kakao
KAKAO_AUTHORIZATION_GRANT_TYPE=authorization_code
KAKAO_SCOPE=profile_nickname,profile_image,account_email,phone_number
KAKAO_AUTHORIZATION_URI=https://kauth.kakao.com/oauth/authorize
KAKAO_TOKEN_URI=https://kauth.kakao.com/oauth/token
KAKAO_USER_INFO_URI=https://kapi.kakao.com/v2/user/me
KAKAO_USER_NAME_ATTRIBUTE=id

# cors url 설정
cors.url="https://mobipay.kr/api/v1/**, http://10.0.2.2:8080"

# FCM
FIREBASE_CONFIG=
FIREBASE_PROJECT_ID=mobipay-c0c38

# 가맹점 ID (MER -> MOBI)
PARKING_MERCHANT_ID=1906
OIL_MERCHANT_ID=1907
FOOD_MERCHANT_ID=1911
WASHING_MERCHANT_ID=1908
MOTEL_MERCHANT_ID=1910
STREET_MERCHANT_ID=1909

# 가맹점 별 MOBI_API_KEY (MER -> MOBI)
PARKING_MOBI_API_KEY=
OIL_MOBI_API_KEY=
FOOD_MOBI_API_KEY=
WASHING_MOBI_API_KEY=
MOTEL_MOBI_API_KEY=
STREET_MOBI_API_KEY=

# MER_API_KEY (POS, MOBI -> MER)
POS_MER_API_KEY=
MOBI_MER_API_KEY=
```

---