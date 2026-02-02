package com.bohouse.ota.domain.fake;

import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.repository.AccommodationRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeAccommodationRepository implements AccommodationRepository {

    private final Map<String, Accommodation> store = new HashMap<>();

    @Override
    public Optional<Accommodation> findByExternal(String externalId) {
        return Optional.ofNullable(store.get(externalId));
    }

    @Override
    public Accommodation save(String externalId, Accommodation accommodation) {
        store.put(externalId, accommodation);
        return accommodation;
    }
}
