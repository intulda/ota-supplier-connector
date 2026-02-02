package com.bohouse.ota.adapter.outbound.supplier;

import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SupplierClientRouter {
    private final Map<SupplierType, SupplierAccommodationClient> clients;

    public SupplierClientRouter(List<SupplierAccommodationClient> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toMap(SupplierAccommodationClient::supports, Function.identity()));
    }

    public SupplierAccommodationClient get(SupplierType type) {
        return clients.get(type);
    }
}
