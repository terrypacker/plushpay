package com.plushpay.repository;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Terry Packer
 */
public interface PlushPayRespository<T, ID> {

    Stream<T> getAll();

    Optional<T> findById(ID id);

    Optional<T> save(T t);

    void delete(T t);
}
