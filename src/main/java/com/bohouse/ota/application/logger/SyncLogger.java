package com.bohouse.ota.application.logger;

import com.bohouse.ota.domain.model.SupplierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncLogger {
    private final Logger log;
    private final SupplierType supplier;

    public SyncLogger(Class<?> clazz, SupplierType supplier) {
        this.log = LoggerFactory.getLogger(clazz);
        this.supplier = supplier;
    }

    public void start(String step) {
        log.info("[SUPPLIER={}][STEP={}][RESULT=START]", supplier, step);
    }

    public void success(String step, String detail) {
        log.info("[SUPPLIER={}][STEP={}][RESULT=SUCCESS]{}", supplier, step, detail);
    }

    public void failed(String step, String reason, Exception e) {
        log.error("[SUPPLIER={}][STEP={}][RESULT=FAILED][REASON={}]", supplier, step, reason, e);
    }

}
