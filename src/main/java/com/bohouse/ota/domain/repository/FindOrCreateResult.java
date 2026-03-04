package com.bohouse.ota.domain.repository;

public record FindOrCreateResult<T>(T entity, boolean created) {
}
