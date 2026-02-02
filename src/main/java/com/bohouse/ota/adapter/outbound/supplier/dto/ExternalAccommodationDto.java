package com.bohouse.ota.adapter.outbound.supplier.dto;

public record ExternalAccommodationDto(
        String externalId,
        String name,
        double latitude,
        double longitude,
        String address
) {
}
