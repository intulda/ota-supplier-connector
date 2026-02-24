package com.bohouse.ota.domain.fake;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.SupplierType;
import com.bohouse.ota.domain.repository.AccommodationRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeAccommodationRepository implements AccommodationRepository {

    private final Map<String, Accommodation> store = new HashMap<>();

    @Override
    public Optional<Accommodation> findByExternal(String externalId, SupplierType supplierType) {
        return Optional.ofNullable(store.get(externalId + "-" + supplierType.name()));
    }

    @Override
    public Accommodation save(String externalId, SupplierType supplierType, Accommodation accommodation) {
        store.put(externalId + "-" + supplierType.name(), accommodation);
        return accommodation;
    }
}
