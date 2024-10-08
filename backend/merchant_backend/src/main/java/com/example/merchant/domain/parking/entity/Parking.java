package com.example.merchant.domain.parking.entity;

import com.example.merchant.domain.parking.dto.ParkingEntryRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "parking")
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, length = 8)
    private String number;

    @Column(name = "entry", nullable = false)
    private LocalDateTime entry;

    @Column(name = "`exit`")
    private LocalDateTime exit;

    @Column(name = "paid", nullable = false)
    private Boolean paid = false;

    @Builder
    public Parking(String number, LocalDateTime entry) {
        this.number = number;
        this.entry = entry;
    }

    public static Parking of(ParkingEntryRequest request) {
        return Parking.builder()
                .number(request.getCarNumber())
                .entry(request.getEntry())
                .build();
    }

    public Parking goExit(LocalDateTime exit) {
        this.exit = exit;
        return this;
    }

    public void changePaid() {
        this.paid = true;
    }
}
