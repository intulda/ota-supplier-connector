package com.bohouse.ota.application.sync;

import com.bohouse.ota.domain.model.SupplierType;

import java.util.List;

/**
 * @param supplierType             EXPEDIA, BOOKING 등
 * @param externalAccommodationIds 외부 숙소 ID들
 */
public record AccommodationSyncCommand(SupplierType supplierType, List<String> externalAccommodationIds) {
}
