package com.plushpay.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * @author Terry Packer
 */
public abstract class AbstractInMemoryRepository<T extends IdEntity<ID>, ID> implements
    PlushPayRespository<T, ID> {

    protected final List<T> rows;
    protected final IdGenerator<ID> idGenerator;

    public AbstractInMemoryRepository(IdGenerator<ID> idGenerator) {
        this.rows = new CopyOnWriteArrayList<>();
        this.idGenerator = idGenerator;
    }

    @Override
    public Stream<T> getAll() {
        return rows.stream();
    }

    @Override
    public Optional<T> findById(ID id) {
        return rows.stream().filter(row -> row.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<T> save(T t) {
        if (t.getId() == null) {
            t.setId(generateNewId());
        } else {
            Optional<T> existing = findById(t.getId());
            if (existing.isPresent()) {
                delete(existing.get());
            }
        }
        rows.add(t);
        return Optional.of(t);
    }

    @Override
    public void delete(T t) {
        rows.remove(t);
    }

    public ID generateNewId() {
        return idGenerator.generateId();
    }

}
