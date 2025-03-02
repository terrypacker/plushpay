package com.plushpay.service;

import com.plushpay.repository.IdEntity;
import com.plushpay.repository.PlushPayRespository;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Terry Packer
 */
public abstract class AbstractPlushPayService<T extends IdEntity<ID>, ID, REPO extends PlushPayRespository<T, ID>> {

    protected final REPO repository;

    public AbstractPlushPayService(REPO repository) {
        this.repository = repository;
    }

    public Stream<T> getAll() {
        return repository.getAll();
    }

    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    public Optional<T> save(T t) {
        return repository.save(t);
    }

    public void delete(T t) {
        repository.delete(t);
    }


}
