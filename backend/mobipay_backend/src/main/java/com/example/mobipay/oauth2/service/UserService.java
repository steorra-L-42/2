package com.example.mobipay.oauth2.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.refreshtoken.entity.RefreshToken;
import com.example.mobipay.oauth2.error.MissingUserDetailsException;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import com.example.mobipay.oauth2.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MobiUserRepository mobiUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    // 1. 이메일로 사용자 조회(중복 유무 확인 후, 중복이 되지 않는다면 SSAFY API에서 이메일 검증 수행)

    public MobiUser findOrCreateUser(String email, String name, String phoneNumber, String picture) {

//        MobiUser mobiUser = mobiUserRepository.findByEmail(email)
//                .orElseGet(() -> SSAFYAPI(email, name, phoneNumber, picture));
//        System.out.println(mobiUser.getEmail());
        return mobiUserRepository.findByEmail(email)
                .orElseGet(() -> SSAFYAPI(email, name, phoneNumber, picture));

//        if (phoneNumber == null || phoneNumber.isEmpty()) {
//            throw new IllegalArgumentException("PhoneNumber cannot be null or empty:findOrCreateUser");
//        }
//        return mobiUserRepository.findByEmail(email)
//                .orElseGet(() -> createUser(email, name, phoneNumber, picture));

    }


    // 2. 사용자 생성 (카카오 API에서 받아온 정보 기반)
    public MobiUser createUser(String email, String name, String phoneNumber, String picture) {

        MobiUser user = MobiUser.builder()
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .picture(picture)
                .build();
        return mobiUserRepository.save(user);
    }

    // 3. 사용자 정보 업데이트
    public MobiUser updateUser(Long userId, String name, String phoneNumber, String picture) {
        MobiUser mobiUser = mobiUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        mobiUser.updatePicture(picture);
        // 추가로 이름, 전화번호 등 업데이트할 수 있는 로직 작성
        return mobiUser;
    }

    // 4. 리프레시 토큰 추가
    public void addRefreshToken(Long userId, String refreshTokenValue, LocalDateTime issuedAt,
                                LocalDateTime expiresAt) {
        MobiUser mobiUser = mobiUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        RefreshToken refreshToken = RefreshToken.builder()
                .value(refreshTokenValue)
                .issuedAt(issuedAt)
                .expiredAt(expiresAt)
                .build();

        // 리프레시 토큰 저장
        refreshTokenRepository.save(refreshToken);
        // MobiUser와 연관 설정
        mobiUser.addRefreshToken(refreshToken);
        // MobiUser 업데이트
        mobiUserRepository.save(mobiUser);
    }

    //    public String generateJwtToken(MobiUser mobiUser) {}
    // 사용자 정보를 기반으로 JWT 생성
//        return jwtTokenProvider.createToken(
//                mobiUser.getEmail(),
//                mobiUser.getName(),
//                mobiUser.getPicture(),
//                mobiUser.getPhoneNumber()
////                mobiUser.getRole().name()
//        );
    public String generateJwtAccessToken(MobiUser mobiUser) {
        return jwtUtil.createAccessToken(
                mobiUser.getEmail(),
                mobiUser.getName(),
                mobiUser.getPicture(),
                mobiUser.getPhoneNumber()
        );
    }

    public String generateJwtRefreshToken(MobiUser mobiUser) {
        return jwtUtil.createRefreshToken(
                mobiUser.getEmail(),
                mobiUser.getName(),
                mobiUser.getPicture(),
                mobiUser.getPhoneNumber()
        );
    }

    // 5. 리프레시 토큰 삭제
    public void deleteRefreshToken(Long userId) {
        MobiUser mobiUser = mobiUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        mobiUser.deleteRefreshToken();
    }

    // 6. SSAFY API로 DB확인 검증
    public MobiUser SSAFYAPI(String email, String name, String phoneNumber, String picture) {
        boolean emailExists = checkEmail(email);
        System.out.println(emailExists);

        if (!emailExists) {
            return sendUserDetailRequest(email);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("name and phoneNumber is null");
//             만약 이메일이 없다면, 안드로이드에서 name, phoneNumber를 입력받기 슈발 헷갈려
        }

        // checkEmail 을 통해 ssafy 금융 DB에서 값이 있다면(true) 아래 로직에서 값을 받아오고 mobipay DB에 다시 저장함.
        MobiUser user = MobiUser.builder()
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .picture(picture)
                .build();
        System.out.println(user.getEmail());
        System.out.println(user.getName());
        System.out.println(user.getPhoneNumber());
        System.out.println(user.getPicture());
        return mobiUserRepository.save(user);
    }

    public MobiUser sendUserDetailRequest(String email) {
        // 안드로이드에 name과 phoneNumber를 요청하는 HTTP 404 응답을 반환
        // throw 사용으로
        throw new MissingUserDetailsException("Please provide name, phone number.");
    }

    private boolean checkEmail(String email) {
//        String apiUrl = "싸삐";  // 실제 API URL로 변경하세요

//        // 요청 헤더 설정 (필요 시)
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//
//        // 요청 바디 설정 (이메일을 포함한 JSON 객체를 전송)
//        EmailCheckRequestDto requestDto = new EmailCheckRequestDto(email);
//
//        // HTTP 요청
//        HttpEntity<EmailCheckRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);
//        ResponseEntity<EmailCheckResponseDto> responseEntity = restTemplate.exchange(
//                apiUrl, HttpMethod.POST, requestEntity, EmailCheckResponseDto.class);
//
//        // 응답으로부터 이메일 존재 여부 확인
//        EmailCheckResponseDto responseBody = responseEntity.getBody();
//        if (responseBody != null && responseBody.isExists()) {
        return true;
    }

//        return false;
//    }

}
