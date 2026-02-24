package com.bohouse.ota.domain.repository;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccommodationRepository {
    Optional<Accommodation> findByExternal(String id, SupplierType supplierType);

    Accommodation save(String externalId, SupplierType supplierType, Accommodation accommodation);
}
