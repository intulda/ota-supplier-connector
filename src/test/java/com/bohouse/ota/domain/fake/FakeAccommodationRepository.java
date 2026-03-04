package com.bohouse.ota.domain.fake;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.SupplierType;
import com.bohouse.ota.domain.repository.AccommodationRepository;

import java.util.HashMap;
import java.util.Map;

public class FakeAccommodationRepository implements AccommodationRepository {

    private final Map<String, Accommodation> store = new HashMap<>();

    @Override
    public Accommodation save(String externalId, SupplierType supplierType, Accommodation accommodation) {
        store.put(externalId + "-" + supplierType.name(), accommodation);
        return accommodation;
    }
}
