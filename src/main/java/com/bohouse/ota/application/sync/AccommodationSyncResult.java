package com.bohouse.ota.application.sync;

import com.bohouse.ota.domain.model.SupplierType;

import java.util.List;

public record AccommodationSyncResult(
        SupplierType supplierType,
        int total,
        int created,
        int reused,
        int failed,
        List<ExternalAccommodationSyncResult> details
) {
    public boolean isSuccess() {
        return failed == 0;
    }
}
