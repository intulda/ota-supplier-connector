package com.bohouse.ota.adapter.outbound.supplier;

import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpediaApiClient implements SupplierAccommodationClient {

    @Override
    public SupplierType supports() {
        return SupplierType.EXPEDIA;
    }

    @Override
    public List<ExternalAccommodationDto> fetch() {
        return List.of(
                new ExternalAccommodationDto("E-1001", "Hotel Tokyo", 1, 1, "Tokyo"),
                new ExternalAccommodationDto("E-1002", "Hotel Seoul", 2, 2, "Seoul"),
                new ExternalAccommodationDto("E-1003", "Hotel Busan", 3, 3, "Busan")
        );
    }
}
