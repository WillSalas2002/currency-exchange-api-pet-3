package com.will.currency.exchange.api.repository;

import java.util.List;

public interface Repository<T> {
    T save(T entity);
    List<T> findAll();
    T update(T updatedEntity);
    void delete(T entity);
}
