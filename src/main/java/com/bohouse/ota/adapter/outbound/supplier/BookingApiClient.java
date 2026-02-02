package com.bohouse.ota.adapter.outbound.supplier;

import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingApiClient implements SupplierAccommodationClient {

    @Override
    public SupplierType supports() {
        return SupplierType.BOOKING;
    }

    @Override
    public List<ExternalAccommodationDto> fetch() {
        return List.of();
    }
}
