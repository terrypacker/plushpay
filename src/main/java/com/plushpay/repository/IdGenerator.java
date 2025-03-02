package com.plushpay.repository;

/**
 * @author Terry Packer
 */
public interface IdGenerator<ID> {

    /**
     * Generate the next ID
     *
     * @return
     */
    ID generateId();
}
