package com.bohouse.ota.domain.service;

import com.bohouse.ota.domain.model.SupplierType;

public record ExternalAccommodationSyncResult(
        SupplierType supplierType,
        String externalAccommodationId,
        Long accommodationId,
        SyncStatus status,
        boolean newlyCreated
) {
    public enum SyncStatus {
        SUCCESS,
        FAILED
    }

    public boolean isSuccess() {
        return status == SyncStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == SyncStatus.FAILED;
    }

    public static ExternalAccommodationSyncResult success(
            SupplierType supplierType,
            String externalId,
            Long accommodationId,
            boolean newlyCreated
    ) {
        return new ExternalAccommodationSyncResult(
                supplierType,
                externalId,
                accommodationId,
                SyncStatus.SUCCESS,
                newlyCreated
        );
    }

    public static ExternalAccommodationSyncResult failed(
            SupplierType supplierType,
            String externalId
    ) {
        return new ExternalAccommodationSyncResult(
                supplierType,
                externalId,
                null,
                SyncStatus.FAILED,
                false
        );
    }
}
