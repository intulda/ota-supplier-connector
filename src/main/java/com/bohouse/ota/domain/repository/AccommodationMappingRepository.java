package com.bohouse.ota.domain.repository;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.AccommodationMapping;
import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.stereotype.Repository;

import java.util.function.Supplier;

@Repository
public interface AccommodationMappingRepository {
    FindOrCreateResult<AccommodationMapping> findOrCreate(SupplierType supplierType, String externalId,  Supplier<Accommodation> accommodation);
}
