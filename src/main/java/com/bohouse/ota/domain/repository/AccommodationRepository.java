package com.bohouse.ota.domain.repository;

import com.bohouse.ota.domain.model.Accommodation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccommodationRepository {
    Optional<Accommodation> findByExternal(String id);

    Accommodation save(String externalId, Accommodation accommodation);
}
