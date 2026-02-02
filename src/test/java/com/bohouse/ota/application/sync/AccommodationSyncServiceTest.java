package com.bohouse.ota.application.sync;

import com.bohouse.ota.adapter.outbound.supplier.SupplierAccommodationClient;
import com.bohouse.ota.adapter.outbound.supplier.SupplierClientRouter;
import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.domain.model.SupplierType;
import com.bohouse.ota.domain.service.ExternalAccommodationSyncResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccommodationSyncServiceTest {

    private SupplierClientRouter router;
    private SupplierAccommodationClient supplierClient;
    private AccommodationDomainService domainService;
    private AccommodationSyncService syncService;

    @BeforeEach
    void setUp() {
        router = mock(SupplierClientRouter.class);
        supplierClient = mock(SupplierAccommodationClient.class);
        domainService = mock(AccommodationDomainService.class);

        syncService = new AccommodationSyncService(router, domainService);
    }

    @Test
    void 도메인에서_예외가_발생하면_failed가_증가하고_details에_FAILED가_추가된다() {
        SupplierType supplierType = SupplierType.EXPEDIA;

        ExternalAccommodationDto dto =
                new ExternalAccommodationDto("EXTERNAL_1", "조선호텔", 37.5645, 126.9806, "서울 강남은 테헤란로 231");
        ExternalAccommodationDto dto2 =
                new ExternalAccommodationDto("EXTERNAL_2", "조선호텔2", 37.5645, 126.9806, "서울 강남은 테헤란로 231");

        when(router.get(supplierType)).thenReturn(supplierClient);
        when(supplierClient.fetch()).thenReturn(List.of(dto, dto2));

        when(domainService.syncFromExternal(supplierType, dto))
                .thenReturn(ExternalAccommodationSyncResult.success(
                        supplierType,
                        dto.externalId(),
                        1L,
                        true
                ));

        when(domainService.syncFromExternal(supplierType, dto2))
                .thenThrow(new RuntimeException("force error test"));

        AccommodationSyncCommand command =
                new AccommodationSyncCommand(supplierType, List.of("EXTERNAL_1", "EXTERNAL_2"));

        AccommodationSyncResult result = syncService.sync(command);

        assertThat(result.total()).isEqualTo(2);
        assertThat(result.created()).isEqualTo(1);
        assertThat(result.reused()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(1);

        assertThat(result.details()).hasSize(2);

        ExternalAccommodationSyncResult r1 = result.details().get(0);
        ExternalAccommodationSyncResult r2 = result.details().get(1);

        assertThat(r1.isSuccess()).isTrue();
        assertThat(r1.newlyCreated()).isTrue();

        assertThat(r2.isFailed()).isTrue();
        assertThat(r2.externalAccommodationId()).isEqualTo(dto2.externalId());
    }
}
