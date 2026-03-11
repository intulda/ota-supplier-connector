package com.bohouse.ota.domain.repository;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAccommodationRepository implements AccommodationRepository {

    private final Map<Long, Accommodation> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public Accommodation save(String externalId, SupplierType supplierType, Accommodation accommodation) {
        store.put(accommodation.getId() == null ? idGenerator.incrementAndGet() : accommodation.getId(), accommodation);
        return accommodation;
    }
}
