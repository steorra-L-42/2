package com.example.mobipay.domain.car.entity;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.common.entity.AuditableCreatedEntity;
import com.example.mobipay.domain.invitation.entity.Invitation;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "car")
public class Car extends AuditableCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number", nullable = false, unique = true, length = 8)
    private String number;

    @Column(name = "car_model", nullable = false, length = 20)
    private String carModel;

    @Column(name = "auto_pay_status", nullable = false)
    private Boolean autoPayStatus = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private MobiUser owner; // MobiUser객체는 Car Entity에서 차주(Owner)를 뜻함.

    @OneToMany(mappedBy = "car")
    private List<CarGroup> carGroups = new ArrayList<>();

    @OneToMany(mappedBy = "car")
    private List<Invitation> invitations = new ArrayList<>();

    @OneToMany(mappedBy = "car")
    private List<ApprovalWaiting> approvalWaitings = new ArrayList<>();

    private Car(String number, String carModel) {
        this.number = number;
        this.carModel = carModel;
    }

    public static Car of(String number, String carModel) {
        return new Car(number, carModel);
    }

    public void setOwner(MobiUser mobiUser) {
        if (this.owner != null) {
            this.owner.getCars().remove(this);
        }
        this.owner = mobiUser;
        mobiUser.getCars().add(this);
    }

    public void changeAutoPayStatus(Boolean status) {
        this.autoPayStatus = status;
    }
}
