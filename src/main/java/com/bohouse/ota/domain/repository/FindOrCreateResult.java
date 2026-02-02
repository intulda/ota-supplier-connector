package com.bohouse.ota.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FindOrCreateResult<T> {
    private final T entity;
    private final boolean created;
}
