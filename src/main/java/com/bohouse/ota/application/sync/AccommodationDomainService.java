package com.bohouse.ota.application.sync;

import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.domain.model.Accommodation;
import com.bohouse.ota.domain.model.AccommodationMapping;
import com.bohouse.ota.domain.model.SupplierType;
import com.bohouse.ota.domain.repository.AccommodationMappingRepository;
import com.bohouse.ota.domain.repository.AccommodationRepository;
import com.bohouse.ota.domain.repository.FindOrCreateResult;
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

    public AccommodationUpsertResult syncFromExternal(SupplierType supplierType, ExternalAccommodationDto dto) {
        FindOrCreateResult<AccommodationMapping> mappingResult =
                accommodationMappingRepository.findOrCreate(
                        supplierType,
                        dto.externalId(),
                        () -> {
                            Accommodation accommodation = new Accommodation(
                                    dto.name(),
                                    dto.latitude(),
                                    dto.longitude(),
                                    dto.address()
                            );
                            accommodationRepository.save(dto.externalId(), supplierType, accommodation);
                            return accommodation;
                        }
                );

        AccommodationMapping mapping = mappingResult.getEntity();

        return new AccommodationUpsertResult(
                mapping.getAccommodation().getId(),
                mappingResult.isCreated()
        );
    }
}
