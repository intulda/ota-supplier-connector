package com.bohouse.ota.application.sync;

import com.bohouse.ota.adapter.outbound.supplier.SupplierAccommodationClient;
import com.bohouse.ota.adapter.outbound.supplier.SupplierClientRouter;
import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.domain.service.ExternalAccommodationSyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationSyncService {

    private final SupplierClientRouter router;
    private final AccommodationDomainService domainService;

    public AccommodationSyncService(SupplierClientRouter router, AccommodationDomainService domainService) {
        this.router = router;
        this.domainService = domainService;
    }

    public AccommodationSyncResult sync(AccommodationSyncCommand command) {

        SupplierAccommodationClient supplier = router.get(command.getSupplierType());
        List<ExternalAccommodationDto> external = supplier.fetch();

        int created = 0;
        int reused = 0;
        int failed = 0;

        List<ExternalAccommodationSyncResult> details = new ArrayList<>();

        for  (ExternalAccommodationDto externalDto : external) {
            try {
                ExternalAccommodationSyncResult externalAccommodationSyncResult = domainService.syncFromExternal(command.getSupplierType(), externalDto);

                details.add(externalAccommodationSyncResult);

                if (externalAccommodationSyncResult.isSuccess()) {
                    if (externalAccommodationSyncResult.newlyCreated()) {
                        created++;
                    } else {
                        reused++;
                    }
                } else {
                    failed++;
                }
            } catch (Exception e) {
                failed++;
                ExternalAccommodationSyncResult.failed(
                        command.getSupplierType(),
                        externalDto.getExternalId()
                );
            }

        }

        return new AccommodationSyncResult(
                command.getSupplierType(),
                external.size(),
                created,
                reused,
                failed,
                details
        );
    }
}
