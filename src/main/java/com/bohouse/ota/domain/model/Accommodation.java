package com.bohouse.ota.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_id")
    private Long id;

    @Column(name = "accommodation_name")
    private String name;

    private double latitude;

    private double longitude;

    @Column(name = "accommodation_address")
    private String address;

    @Enumerated(value = EnumType.STRING)
    private AccommodationStatus status;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Accommodation(String name, double latitude, double longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
