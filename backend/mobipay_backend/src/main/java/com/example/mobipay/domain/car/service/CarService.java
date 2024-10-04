package com.example.mobipay.domain.car.service;

import com.example.mobipay.domain.car.dto.CarAutoPayChangeRequest;
import com.example.mobipay.domain.car.dto.CarAutoPayChangeResponse;
import com.example.mobipay.domain.car.dto.CarListResponse;
import com.example.mobipay.domain.car.dto.CarMemberListResponse;
import com.example.mobipay.domain.car.dto.CarRegisterRequest;
import com.example.mobipay.domain.car.dto.CarRegisterResponse;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.error.DuplicatedCarNumberException;
import com.example.mobipay.domain.car.error.NotMemberException;
import com.example.mobipay.domain.car.error.NotOwnerException;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import java.util.List;
import java.util.stream.Collectors;
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
        Car car = Car.of(request.getNumber(), request.getCarModel());
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

        List<CarGroup> carGroups = carGroupRepository.findByMobiUserId(mobiUser.getId());
        List<Car> cars = carGroups.stream()
                .map(CarGroup::getCar)
                .collect(Collectors.toList());

        return CarListResponse.from(cars);
    }

    /**
     * 차량의 차주라면 차량의 자동결제 상태를 변경한다.
     *
     * @param request    자동결제 상태 변경 요청 정보
     * @param oauth2User 인증된 사용자 정보
     * @return 자동결제 상태 변경 정보
     */
    @Transactional
    public CarAutoPayChangeResponse changeAutoPayStatus(CarAutoPayChangeRequest request, CustomOAuth2User oauth2User) {
        Car car = verifyCarOwner(oauth2User.getMobiUserId(), request.getCarId());
        car.changeAutoPayStatus(request.getAutoPayStatus());
        carRepository.save(car);

        return CarAutoPayChangeResponse.of(car);
    }

    /**
     * 주어진 차량의 소유자를 검증한다.
     *
     * @param mobiUserId 사용자의 PK
     * @param carId      검증할 차량의 ID
     * @return 검증된 Car 객체
     */
    private Car verifyCarOwner(Long mobiUserId, Long carId) {
        if (isMobiUserNotExists(mobiUserId)) {
            throw new MobiUserNotFoundException();
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        if (isUserNotOwner(car, mobiUserId)) {
            throw new NotOwnerException();
        }

        return car;
    }

    /**
     * 주어진 사용자가 존재하지 않는지 확인한다.
     *
     * @param mobiUserId 검증할 사용자 ID
     * @return 사용자가 존재하지 않으면 true, 존재하면 false
     */
    private Boolean isMobiUserNotExists(Long mobiUserId) {
        return !mobiUserRepository.existsById(mobiUserId);
    }


    /**
     * 사용자가 주어진 차량의 소유자가 아닌지 확인한다.
     *
     * @param car        검증할 Car 객체
     * @param mobiUserId 확인할 사용자의 PK
     * @return true이면 사용자가 소유자가 아님을 나타내고, false이면 소유자임을 나타낸다.
     */
    private Boolean isUserNotOwner(Car car, Long mobiUserId) {
        return !car.getOwner().getId().equals(mobiUserId);
    }

    /**
     * 차량의 멤버 목록을 조회한다.
     *
     * @param carId      차량 ID
     * @param oauth2User 인증된 사용자 정보
     * @return 차량 멤버 목록
     */
    public CarMemberListResponse getCarMemberList(Long carId, CustomOAuth2User oauth2User) {
        MobiUser mobiUser = mobiUserRepository.findById(oauth2User.getMobiUserId())
                .orElseThrow(MobiUserNotFoundException::new);

        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        boolean notMember = car.getCarGroups().stream()
                .noneMatch(carGroup -> carGroup.getMobiUser().equals(mobiUser));
        if (notMember) {
            throw new NotMemberException();
        }

        return CarMemberListResponse.from(car);
    }
}
