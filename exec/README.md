# 1. HTTPS 인증서

`exec`에서 실행.
```
docker-compose -f ./cert/cert-compose.yml up -d
```

`cert/data/certbot/conf`에 인증서가 생성되었다면 성공한 것.

```
docker-compose -f ./cert/cert-compose.yml down
```
인증서 생성 후 컨테이너를 내린다.

---
# 2. DB 설치

`exec`에서 실행.
```
docker-compose -f ./db/db-compose.yml up -d
```

---
# 3. Jenkins - CI/CD

`exec`에서 실행.
```
docker-compose -f ./jenkins/jenkins-compose.yml up -d
```
실행 후 Jenkins 설정할 것.

---
# 4. 수동으로 배포

`exec`에서 실행.
```
docker-compose -f ./app/app-compose.yml up -d
```

---