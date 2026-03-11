package com.bohouse.ota.domain.repository;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.AccommodationMapping;
import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Repository
public class InMemoryAccommodationMappingRepository implements AccommodationMappingRepository {

    private final Map<String, AccommodationMapping> store = new HashMap<>();

    @Override
    public FindOrCreateResult<AccommodationMapping> findOrCreate(SupplierType supplierType, String externalId, Supplier<Accommodation> accommodation) {
        String key = supplierType + ":" + externalId;

        if (store.containsKey(key)) {
            return new FindOrCreateResult<>(store.get(key), false);
        }

        AccommodationMapping mapping = AccommodationMapping.create(supplierType, externalId, accommodation.get());

        store.put(key, mapping);

        return new FindOrCreateResult<>(mapping, true);
    }
}
