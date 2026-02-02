package com.bohouse.ota.application.sync;

import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.AccommodationMapping;
import com.bohouse.ota.domain.model.SupplierType;
import com.bohouse.ota.domain.repository.AccommodationMappingRepository;
import com.bohouse.ota.domain.repository.AccommodationRepository;
import com.bohouse.ota.domain.repository.FindOrCreateResult;
import com.bohouse.ota.domain.service.ExternalAccommodationSyncResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccommodationDomainService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationMappingRepository accommodationMappingRepository;

    public AccommodationDomainService(AccommodationRepository accommodationRepository, AccommodationMappingRepository accommodationMappingRepository) {
        this.accommodationRepository = accommodationRepository;
        this.accommodationMappingRepository = accommodationMappingRepository;
    }

    public ExternalAccommodationSyncResult syncFromExternal(SupplierType supplierType, ExternalAccommodationDto dto) {
        final Accommodation accommodation;
        final boolean accommodationCreated;

        Optional<Accommodation> accommodationOptional = accommodationRepository.findByExternal(dto.externalId());

        if (accommodationOptional.isPresent()) {
            accommodation =  accommodationOptional.get();
            accommodationCreated = false;
        } else {
            accommodationCreated = true;
            accommodation = new Accommodation(
                    dto.name(),
                    dto.latitude(),
                    dto.longitude(),
                    dto.address()
            );
        }

        FindOrCreateResult<AccommodationMapping> mappingResult =
                accommodationMappingRepository.findOrCreate(
                        supplierType,
                        dto.externalId(),
                        accommodation
                );

        boolean newlyCreated =
                accommodationCreated || mappingResult.isCreated();

        return ExternalAccommodationSyncResult.success(
                supplierType,
                dto.externalId(),
                accommodation.getId(),
                newlyCreated
        );
    }
}
