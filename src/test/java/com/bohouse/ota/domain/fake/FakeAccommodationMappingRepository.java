package com.bohouse.ota.domain.fake;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.AccommodationMapping;
import com.bohouse.ota.domain.model.SupplierType;
import com.bohouse.ota.domain.repository.AccommodationMappingRepository;
import com.bohouse.ota.domain.repository.FindOrCreateResult;

import java.util.HashMap;
import java.util.Map;

public class FakeAccommodationMappingRepository implements AccommodationMappingRepository {

    private final Map<String, AccommodationMapping> mappings = new HashMap<>();
    private boolean forceError = false;

    @Override
    public FindOrCreateResult<AccommodationMapping> findOrCreate(SupplierType supplierType, String externalId, Accommodation accommodation) {
        if (forceError) {
            throw new RuntimeException("force error test");
        }

        String key = supplierType + ":" + externalId;

        if (mappings.containsKey(key)) {
            return new FindOrCreateResult<>(mappings.get(key), false);
        }

        AccommodationMapping mapping = AccommodationMapping.create(supplierType, externalId, accommodation);

        mappings.put(key, mapping);

        return new FindOrCreateResult<>(mapping, true);
    }

    public void forceError(boolean error) {
        this.forceError = error;
    }
}
