package com.plushpay.repository;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Terry Packer
 */
public class LongIdGenerator implements IdGenerator<Long> {

    private AtomicLong atomicLong = new AtomicLong();

    @Override
    public Long generateId() {
        return atomicLong.getAndIncrement();
    }
}
