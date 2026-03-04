package com.bohouse.ota.application.sync;

import com.bohouse.ota.adapter.outbound.supplier.SupplierAccommodationClient;
import com.bohouse.ota.adapter.outbound.supplier.SupplierClientRouter;
import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationSyncService {

    private final SupplierClientRouter router;
    private final AccommodationDomainService domainService;

    private final Logger log = LoggerFactory.getLogger(AccommodationSyncService.class);

    public AccommodationSyncService(SupplierClientRouter router, AccommodationDomainService domainService) {
        this.router = router;
        this.domainService = domainService;
    }

    public AccommodationSyncResult sync(AccommodationSyncCommand command) {

        SupplierAccommodationClient supplier = router.get(command.supplierType());
        List<ExternalAccommodationDto> external = supplier.fetch();

        int created = 0;
        int reused = 0;
        int failed = 0;

        List<ExternalAccommodationSyncResult> details = new ArrayList<>();

        for  (ExternalAccommodationDto externalDto : external) {
            try {
                AccommodationUpsertResult result = domainService.syncFromExternal(command.supplierType(), externalDto);

                ExternalAccommodationSyncResult externalResult =
                        ExternalAccommodationSyncResult.success(
                                command.supplierType(),
                                externalDto.externalId(),
                                result.accommodationId(),
                                result.newlyCreated()
                        );

                details.add(externalResult);

                if (externalResult.isSuccess()) {
                    if (externalResult.newlyCreated()) {
                        created++;
                    } else {
                        reused++;
                    }
                } else {
                    failed++;
                }
            } catch (Exception e) {
                failed++;
                ExternalAccommodationSyncResult failedResult = ExternalAccommodationSyncResult.failed(
                        command.supplierType(),
                        externalDto.externalId()
                );
                details.add(failedResult);

                log.error("[SUPPLIER={}][STEP=FETCH][RESULT=FAILED][REASON={}]", command.supplierType(), e.getMessage(), e);
            }

        }

        return new AccommodationSyncResult(
                command.supplierType(),
                external.size(),
                created,
                reused,
                failed,
                details
        );
    }
}
