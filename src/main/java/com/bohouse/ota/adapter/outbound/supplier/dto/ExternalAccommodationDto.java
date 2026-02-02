package com.bohouse.ota.adapter.outbound.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExternalAccommodationDto {
    private final String externalId;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String address;
}
