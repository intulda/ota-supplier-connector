package com.bohouse.ota.controller;

import com.bohouse.ota.application.sync.AccommodationSyncCommand;
import com.bohouse.ota.application.sync.AccommodationSyncResult;
import com.bohouse.ota.application.sync.AccommodationSyncService;
import com.bohouse.ota.domain.model.SupplierType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sync")
public class AccommodationSyncController {

    private final AccommodationSyncService syncService;

    public AccommodationSyncController(AccommodationSyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/{supplier}")
    public AccommodationSyncResult sync(@PathVariable String supplier) {

        AccommodationSyncCommand command =
                new AccommodationSyncCommand(SupplierType.valueOf(supplier), List.of("E-1001", "E-1002", "E-1003"));

        return syncService.sync(command);
    }
}