package com.bohouse.ota.domain.repository;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.SupplierType;

public interface AccommodationRepository {
    Accommodation save(String externalId, SupplierType supplierType, Accommodation accommodation);
}
