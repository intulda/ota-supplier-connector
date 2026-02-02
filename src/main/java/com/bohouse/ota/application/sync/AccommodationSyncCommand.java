package com.bohouse.ota.application.sync;

import com.bohouse.ota.domain.model.SupplierType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AccommodationSyncCommand {
    private final SupplierType supplierType;   // EXPEDIA, BOOKING 등
    private final List<String> externalAccommodationIds; // 외부 숙소 ID들
}
