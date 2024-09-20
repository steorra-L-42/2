package com.example.mobipay.domain.car.service;

import com.example.mobipay.domain.car.dto.CarListResponse;
import com.example.mobipay.domain.car.dto.CarRegisterRequest;
import com.example.mobipay.domain.car.dto.CarRegisterResponse;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.error.DuplicatedCarNumberException;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CarService {

    private final CarRepository carRepository;
    private final MobiUserRepository mobiUserRepository;
    private final CarGroupRepository carGroupRepository;

    /**
     * 차량을 등록하고 사용자와의 관계를 설정한다.
     *
     * @param request    차량 등록 요청 정보
     * @param oauth2User 인증된 사용자 정보
     * @return 등록된 차량 정보
     */
    @Transactional
    public CarRegisterResponse registerCar(CarRegisterRequest request, CustomOAuth2User oauth2User) {
        // 차량 번호 중복 확인
        validateDuplicatedCarNumber(request.getNumber());

        // 사용자 정보 조회
        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());

        // 차량 & 차량 그룹 생성 및 연관관계 설정
        Car car = Car.from(request.getNumber());
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroupRepository.save(carGroup);

        return CarRegisterResponse.of(car);
    }

    /**
     * 차량 번호 중복 여부를 검증한다.
     *
     * @param carNumber 차량 번호
     */
    private void validateDuplicatedCarNumber(String carNumber) {
        Boolean duplicated = carRepository.existsByNumber(carNumber); // 차량이 중복되었는지?

        if (duplicated) { // 차량이 중복 되었다면 예외를 발생한다.
            throw new DuplicatedCarNumberException();
        }
    }

    /**
     * mobiUserId로 사용자 정보를 조회한다.
     *
     * @param mobiUserId 사용자 ID
     * @return MobiUser
     */
    private MobiUser findMobiUser(Long mobiUserId) {

        return mobiUserRepository.findById(mobiUserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }

    /**
     * mobiUser의 차량 목록을 조회한다.
     *
     * @param oauth2User 인증된 mobiUser 사용자
     * @return CarListResponse
     */
    @Transactional(readOnly = true)
    public CarListResponse getCars(CustomOAuth2User oauth2User) {
        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());
        List<Car> cars = carRepository.findAllByOwner(mobiUser);

        return CarListResponse.from(cars);
    }
}
