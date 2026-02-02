package com.bohouse.ota.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "supplier_type",
                        "external_accommodation_id"
                }
        )
)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class AccommodationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_mapping_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_type")
    private SupplierType supplierType;

    @Column(name = "external_accommodation_id")
    private String externalAccommodationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @CreatedDate
    private LocalDateTime createdAt;

    private AccommodationMapping(
            SupplierType supplierType,
            String externalId,
            Accommodation accommodation
    ) {
        this.supplierType = supplierType;
        this.externalAccommodationId = externalId;
        this.accommodation = accommodation;
    }

    public static AccommodationMapping create(
            SupplierType supplierType,
            String externalId,
            Accommodation accommodation
    ) {
        return new AccommodationMapping(supplierType, externalId, accommodation);
    }
}
