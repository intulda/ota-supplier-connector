package com.bohouse.ota.domain;

import com.bohouse.ota.adapter.outbound.supplier.dto.ExternalAccommodationDto;
import com.bohouse.ota.application.sync.AccommodationDomainService;
import com.bohouse.ota.application.sync.AccommodationUpsertResult;
import com.bohouse.ota.domain.fake.FakeAccommodationMappingRepository;
import com.bohouse.ota.domain.fake.FakeAccommodationRepository;
import com.bohouse.ota.domain.model.SupplierType;
import com.bohouse.ota.application.sync.ExternalAccommodationSyncResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AccommodationDomainServiceTest {

    private AccommodationDomainService service;

    @BeforeEach
    void setUp() {
        service = new AccommodationDomainService(
                new FakeAccommodationRepository(),
                new FakeAccommodationMappingRepository()
        );
    }

    @Test
    void 같은_externalId를_두번_동기화하면_한번_생성된다() {
        ExternalAccommodationDto dto =
                new ExternalAccommodationDto("EXTERNAL_1", "조선호텔", 37.5645, 126.9806, "서울 강남은 테헤란로 231");

        AccommodationUpsertResult first = service.syncFromExternal(SupplierType.EXPEDIA, dto);
        AccommodationUpsertResult second = service.syncFromExternal(SupplierType.EXPEDIA, dto);

        assertThat(first.newlyCreated()).isTrue();
        assertThat(second.newlyCreated()).isFalse();

        assertThat(first.accommodationId())
                .isEqualTo(second.accommodationId());
    }

    @Test
    void 도메인서비스는_저장중_실패하면_예외를_던진다() {
        FakeAccommodationMappingRepository mappingRepository = new FakeAccommodationMappingRepository();
        mappingRepository.forceError(true);
        FakeAccommodationRepository fakeAccommodationRepository = new FakeAccommodationRepository();
        AccommodationDomainService domainService = new AccommodationDomainService(fakeAccommodationRepository, mappingRepository);

        ExternalAccommodationDto dto =
                new ExternalAccommodationDto("EXTERNAL_1", "조선호텔", 37.5645, 126.9806, "서울 강남은 테헤란로 231");

        Assertions.assertThatThrownBy(() -> domainService.syncFromExternal(SupplierType.EXPEDIA, dto))
                .isInstanceOf(RuntimeException.class);
    }
}
