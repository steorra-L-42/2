![image.png](https://github.com/steorra-L-42/2/blob/dev/etc/images/mobipay.png?raw=true)

## 👨‍👨‍👦‍👦 Kim & Lee 팀 소개
| 이상철 | 김세진 | 이철민 | 이재빈 |
| --- | --- | --- | --- |
| Team Leader | Android<br>Tech Leader | Android<br>Developer | Android<br>Developer |
| <img src="https://avatars.githubusercontent.com/u/9999293?v=4" width="150"/> | <img src="https://avatars.githubusercontent.com/u/174983748?v=4" width="150"/> | <img src="https://avatars.githubusercontent.com/u/148309370?s=400&v=4" width="150"/> | <img src="https://avatars.githubusercontent.com/u/50922212?v=4" width="150"/> |
| [@Harvey-sc-Lee](https://github.com/Harvey-sc-Lee) | [@rlatpwls30](https://github.com/tpwls30) | [@steorra-L-42](https://github.com/steorra-L-42) | [@Ak-mong](https://github.com/Ak-mong) |

| 김의근 | 김범중 | 이진송 |
| --- | --- | --- |
| Backend<br>Tech Leader | Backend<br>Developer | Backend<br>Developer |
| <img src="https://avatars.githubusercontent.com/u/99334790?v=4" width="150"/> | <img src="https://avatars.githubusercontent.com/u/121084350?v=4"  width="150"/> | <img src="https://avatars.githubusercontent.com/u/157494028?s=40&v=4" width="150"/> |
| [@ramen4598](https://github.com/ramen4598) | [@bbamjoong](https://github.com/bbamjoong) | [@nosong2](https://github.com/nosong2) |

# 📷 프로젝트 소개

**창문 내리지 마세요**✋ **자동차 간편결제 서비스, MobiPay**

### **📝 자동차 내에서 편한 결제를 원하시나요?**

> MobiPay에서 차량을 등록하고 자동결제를 사용해보세요!
>
> 창문을 내리지 않고, 문콕 하지 않고 결제가 가능합니다.

### **👬 편하게 지인들을 차량에 초대해보세요!**

> 블루투스로 옆자리 지인을 바로 초대해보세요.
> 
> 전화번호로 멀리 있는 지인도 초대가 가능해요!

# 🔧 기술 스택
<details>
  <summary>Android</summary>

  | Android | Version |
  | --- | --- |
  | Android Studio | 2024.1.1 |
  | Android min SDK | 28 |
  | Android target SDK | 34 |
  | Android max SDK | 34 |

</details>

<details>
  <summary>Backend</summary>

  | Backend | Version |
  | --- | --- |
  | OpenJDK | 17.0.12 |
  | Spring Boot | 3.3.3 |
  | Spring Data JPA | 3.3.3 |
  | JUnit5 | 5.10.3 |
  | Nginx | 1.27.2 |
  | MySQL | 8.4.1 |
  | Node.js | 20.18.0 |
  | FastAPI | 0.115.0 |

</details>

<details>
  <summary>CI/CD</summary>

  | CI/CD | Version |
  | --- | --- |
  | Jenkins | jenkins/jenkins:2.462.2-jdk17 |
  | Docker | 27.1.1 |
  | Docker Compose | 2.29.1 |

</details>

<details>
  <summary>Frontend</summary>

  | Frontend | Version |
  | --- | --- |
  | Alpine.js | 3.14.1 |

</details>

<details>
  <summary>AI</summary>

  | AI | Version |
  | --- | --- |
  | NVIDIA GPU Driver | 560.94 |
  | Torch | 2.3.1 |
  | Torch Vision | 0.18.1 |
  | MSVC | 143 |
  | CUDA | 12.1 |
  | cuDNN | 9.3.0 |
  | AI Model | TPS ResNet BiLSTM Attn |

</details>

# 🏗️ 서비스 아키텍처
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/transparent.png?raw=true">

# 결제 처리 과정    
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/payment.png?raw=true">

# **✨  주요 기능**

### 💵 창문을 내리지 않고 자동결제를 진행해 보세요.
차량 번호가 인식되고, 점원의 POS에서 제품을 선택해 결제 요청을 하면 앱에서 자동결제가 이루어집니다.
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/15.%20food%EC%9E%90%EB%8F%99.gif?raw=true" width="800">
### 💵 깜빡하고 자동결제를 체크하지 않았다면 수동결제도 가능해요.
앱에 결제 요청이 오면 지문인식으로 수동결제가 이루어집니다.
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/15.%20food%EC%88%98%EB%8F%99.gif?raw=true" width="800">

### 👬 블루투스로 옆자리 지인을 차량에 초대가 가능해요.
| 블루투스 멤버 초대 | 블루투스 초대 수락 |
|--------------------|---------------------|
|초대받을 지인이 블루투스 초대 대기상태라면<br> 여러명도 동시에 초대가 가능해요.|블루투스 초대 대기상태에 진입했을 때<br> 차량 그룹에 초대받을 수 있어요.|
|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/5.%20%EB%A9%A4%EB%B2%84%EC%B4%88%EB%8C%80%20%EB%B8%94%EB%A3%A8%ED%88%AC%EC%8A%A4.gif?raw=true" width="250"/>|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/5.%20%EC%B0%A8%EB%9F%89%20%EC%88%98%EB%9D%BD%20%EB%B8%94%EB%A3%A8%ED%88%AC%EC%8A%A4.gif?raw=true" width="250"/>|

### 👭 전화번호로 초대도 가능해요.
| 전화번호 멤버 초대 | 전화번호 멤버 수락 |
|--------------------|---------------------|
|초대받을 지인의 전화번호를 입력해<br>차량에 초대해 보세요.|초대 알림이 오면 알림을 클릭해<br> 차량 그룹에 가입할 수 있어요. |
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/5.%20%EB%A9%A4%EB%B2%84%20%EC%B4%88%EB%8C%80%20%EC%88%98%EA%B8%B0%20%EC%9E%85%EB%A0%A5.gif?raw=true" width="250"/>|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/5.%20%EC%B0%A8%EB%9F%89%20%EC%88%98%EB%9D%BD%20%EC%88%98%EA%B8%B0%20%EC%9E%85%EB%A0%A5.gif?raw=true" width="250"/>|


### 📱작동 화면
| 1. 로그인 | 2. 카드 여러개 등록 | 3. 카드 한개 등록 | 4. 자동결제 상태 변경|
|---|---|---|---|
|카카오 소셜 로그인이 가능해요.<br>추가로 전화번호 이름도 입력해주세요.|여러장의 카드를 등록할 수 있어요.<br>(일괄 한도 적용도 가능해요.)|한장의 카드도 등록이 가능해요.|자동결제로 이용할 카드를 정할 수 있어요.<br>물론 해제도 가능해요.|
|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/1.%20%EB%A1%9C%EA%B7%B8%EC%9D%B8.gif?raw=true" width="250"/>|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/2.%20%EC%B9%B4%EB%93%9C%20%EC%83%81%EC%84%B8%EC%A1%B0%ED%9A%8C.gif?raw=true" width="250"/>|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/2.%20%EB%8B%A8%EC%9D%BC%20%EC%B9%B4%EB%93%9C%20%EC%B6%94%EA%B0%80.gif?raw=true" width="250"/>|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/3.%20%EC%B9%B4%EB%93%9C%20%EC%9E%90%EB%8F%99%EA%B2%B0%EC%A0%9C%20%EC%B2%B4%ED%81%AC%20%ED%95%B4%EC%A0%9C.gif?raw=true" width="250"/>|

| 5. 번호판 인식 차량 등록 | 6. 번호판 입력 차량 등록 | 7. 차량 자동결제 여부 체크 |
|---|---|---|
|차량 번호판을 촬영해서<br>간편하게 차량을 등록할 수 있어요.|차량 번호를 직접 입력해서<br> 차량 등록도 가능해요.|모비페이를 이용하며 해당 차량으로<br>자동결제 여부를 정할 수 있어요.|
|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/4.%20%EC%B0%A8%EB%9F%89%20%EB%93%B1%EB%A1%9D%20%EC%82%AC%EC%A7%84%20%EC%9D%B8%EC%8B%9D.gif?raw=true" width="250"/>|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/4.%20%EC%B0%A8%EB%9F%89%20%EB%93%B1%EB%A1%9D%20%EC%88%98%EA%B8%B0%EC%9E%85%EB%A0%A5.gif?raw=true" width="250"/>|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/6.%20%EC%B0%A8%20%EC%9E%90%EB%8F%99%EA%B2%B0%EC%A0%9C%20%EC%B2%B4%ED%81%AC.gif?raw=true" width="250"/>|

8. **입차 시 예상 결제 금액 표시** <br>
주차장에 입차를 하면 메인 화면에 주차 예상 금액이 표시돼요.
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/18.%20%EC%A3%BC%EC%B0%A8%EC%9E%A5%EC%9A%94%EA%B8%88%20%EC%83%9D%EC%84%B1.gif?raw=true" width="800"/>

<br>
<br>

9. **입차 후 주차한 위치 표시** <br>
입차 후 일정 거리를 벗어나면 메인 화면에 주차한 위치가 표시돼요.
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/19.%20%EC%95%88%EC%98%A4%ED%86%A0%20%EC%A3%BC%EC%B0%A8%20%EC%9C%84%EC%B9%98%20%EC%A0%80%EC%9E%A5.gif?raw=true" width="800"/>
<br>
<br>

10. **출차 시 예상 결제 금액 미표시** <br>
주차장에서 출차를 하면 메인 화면에 주차 예상 금액이 표시되지 않아요.
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/18.%20%EC%A3%BC%EC%B0%A8%EC%9E%A5%EC%9A%94%EA%B8%88%20%EC%82%AD%EC%A0%9C.gif?raw=true" width="800"/>

| 11. 결제내역 조회 | 12. 전자 영수증 조회 |
|--------------------|---------------------|
| 모비페이를 이용하면서 결제한 내역을<br>조회할 수 있어요. | 결제내역을 클릭하면 상세 정보를<br>조회할 수 있어요. |
|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/13.%20%EA%B2%B0%EC%A0%9C%20%EB%82%B4%EC%97%AD%20%EC%A1%B0%ED%9A%8C.gif?raw=true" width="250">|<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/14.%20%EC%98%81%EC%88%98%EC%A6%9D%20%EC%A1%B0%ED%9A%8C.gif?raw=true" width="250">|

13. **결제 취소** <br>
가맹점 POS에서 결제 취소를 하면, 사용자에게 결제 취소 알림이 와요.<br> (클릭하면 결제취소 내역을 확인할 수 있어요.)
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/gif/17.%20%EA%B2%B0%EC%A0%9C%20%EC%B7%A8%EC%86%8C.gif?raw=true" width="1000"/>

# 🔨 기술 소개

## ➡️ MockMvcTest

### 도입 이유

- 테스트에 필요한 기능만 가지는 가짜 객체를 만들어서 애플리케이션 서버에 배포하지 않고도 스프링 MVC 동작을 재현할 수 있어 도입했습니다.
- Request → Response의 API 호출 과정을 테스트할 수 있어 API 연결 시 발생할 수 있는 오류를 최소화 할 수 있도록 하였습니다.

### 도입 결과

- **테스트 속도 향상**: 실제 서버를 배포하지 않고도 API 호출 과정을 재현함으로써 빠르게 테스트할 수 있었습니다. 이를 통해 개발 주기를 단축해 프로젝트 완성도를 높일 수 있었습니다.
- **API 통신 오류 사전 예방**: MockMvc를 사용하여 Request-Response 흐름을 테스트한 결과, 실제 배포 환경에서 발생할 수 있는 API 통신 오류를 사전에 식별하고 해결할 수 있었습니다. 덕분에 배포 후 발생할 수 있는 문제를 줄일 수 있었습니다.
- **통합 테스트 비용 절감**: 모든 레이어를 통합하는 방식이 아닌, 필요한 컨트롤러 및 서비스 레이어만을 독립적으로 테스트할 수 있어 통합 테스트에 소요되는 리소스를 절감할 수 있었습니다.

## ➡️ Spring Security, JWT
<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKCAZqvGwovmjKOU86x56uoPK0drzJRCIxYA&s" width="100"/>
<img src="https://blog.kakaocdn.net/dn/x9OS9/btr0HyQ7jwf/KthFxTqL06rlOjel1kxmRK/img.png" width="100"/>

### 도입 이유
- 접근 제어를 쉽게 구현할 수 있어, 인증(Authentication)과 인가(Authorization)를 관리하기 위해 도입했습니다.
- Security의 FilterChain에 직접 구현한 Filter를 배치해 보안을 강화할 수 있도록 하였습니다.

### 도입 결과
- **보안성 강화**: 인증 및 인가 과정이 Spring Security로 일관성 있게 처리됨으로써, RBAC(Roll-Based-Access-Control)을 통한 API에 대한 무단 접근을 방지할 수 있었습니다.
- **XSS와 CSRF 공격 방지**: XSS 공격을 방지하기 위해 Access Token은 로컬 스토리지에 저장했고, Refresh Token은 httpOnly 옵션을 적용한 쿠키에 저장하여 브라우저 스크립트에서 접근할 수 없도록 했습니다. Refresh Token은 오직 토큰 재발급 기능만 수행하게 하여, CSRF 공격에 취약할 수 있음을 인지하면서도 피해 범위를 최소화할 수 있었습니다.
- **JWT 복제 공격 방지**: 로그아웃 시, JWT가 복제되는 것을 방지하기 위해 생명주기가 긴 Refresh Token을 서버 측에서 관리하였습니다. 이를 통해 사용자의 Request가 들어올 때마다 서버에서 토큰의 유효성을 검증하고, 서버가 인증의 주도권을 가지는 구조로 개선하였습니다.
- **Refresh Token의 Rotate 전략**: Access Token이 만료될 때 Refresh Token도 함께 재발급하는 'Rotate' 기능을 도입했습니다. 이를 통해 Refresh Token이 탈취되었을 경우 피해를 줄일 수 있었고, 비록 두 토큰의 생명주기가 동일해지는 단점이 있지만 보안성을 향상시키는 데 기여했습니다.

## ➡️ Jetpack Compose
<img src="https://velog.velcdn.com/images/kmjin/post/e4e1ca6f-fff9-4d2d-8605-4af824140e4f/image.png" width="100">

### 도입 이유

- 기존 XML기반 레이아웃 시스템에 비해 더 직관적이고 간결한 코드 작성이 가능합니다.
- 상태 변경에 따른 UI업데이트가 자동으로 이루어져, 기존의 복잡한 UI로직 처리를 간소화 시킬 수 있습니다.
- 필요한 UI요소만 다시 그리는 효율적인 렌더링 방식을 사용하여 앱의 성능을 최적화하고자 했습니다.

### 도입 결과

- **코드 양 감소**: 기존 XML 레이아웃 대비 30~50% 정도의 코드 줄 수를 감소 시킬 수 있었습니다.
- **재사용 가능한 컴포넌트**: 커스텀 UI요소를 쉽게 만들고 재사용할 수 있어, 앱 전반의 UI 일관성을 향상 시킬 수 있었습니다.
- **메모리 사용량 감소 및 렌더링 성능 향상**: 효율적인 컴포지션 시스템으로 메모리 사용량을 최적화 시킬 수 있었고, 필요한 부분만 다시 그리는 방식으로 UI업데이트 성능을 향상 시킬 수 있었습니다.

## ➡️ 멀티모듈
<img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/multimodule.png?raw=true" width="200">

### 도입 이유

- 팀 단위 작업에서 각 개발자가 특정 모듈에 집중하여 개발할 수 있어 협업 효율성을 높일 수 있다.
- 각 모듈이 특정 기능에 집중하므로 코드의 응집도가 높아지고 모듈 간 의존성을 명확히 정의하여 의존성 규칙 위반을 방지할 수 있습니다.
- 새로운 기능을 개발할 때 기존 코드를 수정하지 않고 새 모듈을 만들어 추가 및 모듈 단위로 코드를 수정하고 관리할 수 있어 유지보수가 용이합니다.

### 도입 결과

- **코드 재사용성 향상** : 기능별로 모듈을 나누어 개발하면 필요한 기능을 다른 프로젝트에서 쉽게 재사용할 수 있습니다.
- **의존성 관리 개선** :  각 모듈의 build.gradle 파일에서 의존성을 명확히 정의함으로써 의존성 규칙 위반을 방지할 수 있습니다.
- **팀 협업 효율성 증대** : 각 팀이나 개발자가 특정 모듈에 집중하여 개발할 수 있었고, 새로운 기능을 추가할 때 기존 코드를 수정하지 않아서 효율성을 높일 수 있었음.

## ➡️ Android Auto
<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdav_bpO0Kk7lDPHu-Xycbc6ZXUkOjesEFQw&s" width="100">

### 도입 이유

- 자동차 번호판 인식 기술과 결합하여 운전자가 차량에서 직접 간편하게 결제할 수 있는 시스템을 구현하고자 했습니다.
- 운전 중 스마트폰 사용의 위험성을 줄이면서도 필요한 기능에 접근할 수 있는 안전한 인터페이스를 제공하고자 했습니다.
- 사용자의 모바일 앱 경험을 차량 환경으로 자연스럽게 확장하여 서비스의 연속성을 보장하고자 했습니다.

### 도입 결과

- **사용자의 편의성 향상**: 창문을 내리거나 차에서 내릴 필요 번호판 인식을 통한 자동 인증으로 결제 과정이 크게 간소화 되어 주유, 주차 요금 등을 쉽게 결제할 수 있게 되었습니다.
- **주의 분산 감소**: 운전자가 스마트폰을 직접 조작할 필요 없이 차량 디스플레이를 통해 안전하게 앱을 사용할 수 있게 되었습니다.
- **차량 기능 활용도 증가**: 앱의 결제 기능과 차량의 네비게이션 시스템을 연동하여 맞춤형 서비스 제공이 가능해졌습니다.

## ➡️ WebRTC
<img src="https://velog.velcdn.com/images/iknow/post/ac33262b-5bbd-40f6-af17-ca973d62df4e/image.png" width="100">

### 도입이유

- 운전 주행 특성 상 주의 분산을 줄이는 것이 중요했기 때문에 상품 주문의 보조적 기능으로서 도입하고자 했습니다.
- 단순 결제 서비스 제공을 넘어 사용자와 점주 간의 실시간 통신 기능의 필요성을 느꼈습니다.

### 도입 결과

- **사용자의 편의성 향상** : 사용자가 운전중 핸즈프리로 점주 간 통화를 하거나, 창문을 내리거나 차에서 내릴 필요 없이 결제 과정이 크게 간소화 할 수 있게 되었습니다.
- **Android Auto의 활용도 증가** : Android Auto에 WebRTC의 낮은 지연시간과 고품질 음성/ 영상 통신 기능을 결합해 사용자 경험을 개선할 수 있게 되었습니다.

## ➡️ EncryptedSharedPreferences
<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwusKnhfwjJuwthkIkq6Qv78taTyv2ldE_4Q&s" width="100">

### 도입이유

- 사용자 데이터의 보안 강화를 위해 중요한 정보들을 암호화하여 내부에서 사용할 필요가 있었습니다.

### 도입 결과

- **시스템 보안성 개선**: 중요 정보의 암호화 저장 및 시스템만이 접근 가능한 컨테이너에 저장하여 무단 접근 및 데이터 유출에 대한 위험을 감소 시킬 수 있었습니다.

# 📚💻 개발 문서

<details>
  <summary>📋✨ 기능 정의서</summary>
  
  <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/function.png?raw=true" width="700">

</details>

<details>
  <summary>🗂️ ERD (Entity Relationship Diagram)</summary>
  
  <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/MobiPayERD.png?raw=true">

</details>

<details>
  <summary>👣 유저 플로우 (User Flow)</summary>
  
  <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/userflow.png?raw=true">

</details>

<details>
  <summary>🖼️ 와이어프레임 (Wireframe)</summary>
  
  <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/wireframe.png?raw=true">

</details>

<details>
  <summary> 📊 시퀀스 다이어그램 (Sequence Diagram)</summary>
  <details>
    <summary> 📝 회원가입 </summary>
    <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/seq_signup.png?raw=true">
  </details>
  <details>
    <summary> 🔑 로그인 </summary>
    <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/seq_login.png?raw=true">
  </details>
  <details>
    <summary> 💳 결제요청 </summary>
    <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/seq_payment.png?raw=true">
  </details>
  <details>
    <summary> ✅ 결제승인 </summary>
    <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/seq_approval.png?raw=true">
  </details>
  <details>
    <summary> 📜 결제내역 조회 & ❌ 결제 취소 </summary>
    <img src="https://github.com/steorra-L-42/2/blob/dev/etc/images/seq_cancel.png?raw=true">
  </details>

</details>


## 🙆 협업 방식

### **🙌 적극적인 의사소통**

### Mattermost

- 어려운 문제로 개발이 지연될 때 적극적으로 팀원들에게 도움을 요청하여 문제를 해결하고, 빠르게 개발을 진행할 수 있도록 했습니다. 문제 해결 과정을 투명하게 공유하며 팀의 협업을 원활하게 유지했습니다.

### Discord

- Discord의 원격 소통 및 화면 공유 기능을 활용해 팀원들과 긴밀히 협업 했습니다. 실시간 피드백을 통해 문제를 빠르게 해결하고, 원활한 의사소통으로 프로젝트의 효율성을 높였습니다.

### Jira

- Jira를 통해 프로젝트 이슈를 체계적으로 관리하며, 작업 진행 상황을 팀원들과 공유했습니다. 스프린트 계획을 수립하고, 우선순위를 설정해 프로젝트 목표를 명확히 해 협업의 효율을 높였습니다.

### 코드 컨벤션

- 구글 코딩 컨벤션을 참고한 우아한 테크코스(우테코) 컨벤션을 바탕으로 코드 컨벤션을 유지했습니다. 일관된 컨벤션으로 협업을 수월하게 진행할 수 있었습니다.

> [우아한 테크코스 코드 컨벤션](https://github.com/woowacourse/woowacourse-docs/blob/main/styleguide/java/intellij-java-wooteco-style.xml)

### Notion

- 프로젝트 전반에 대한 내용과 공통 자료를 Notion을 통해 관리하며, 체계적인 정보 공유와 문서화를 실천했습니다.
    - 명세서, 코드 컨벤션, 공유 자료, API 정의서, 환경변수 등을 문서화하여 모든 팀원이 쉽게 접근할 수 있도록 했습니다.
