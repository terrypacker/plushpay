package com.plushpay.repository;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Terry Packer
 */
public class IntegerIdGenerator implements IdGenerator<Integer> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public Integer generateId() {
        return atomicInteger.getAndIncrement();
    }
}
