package com.bohouse.ota.adapter.outbound.supplier;

import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.domain.model.SupplierType;

import java.util.List;

public interface SupplierAccommodationClient {
    SupplierType supports();
    List<ExternalAccommodationDto> fetch();
}
