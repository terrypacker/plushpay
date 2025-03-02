package com.plushpay.repository.user;

import com.plushpay.repository.AbstractInMemoryRepository;
import com.plushpay.repository.IntegerIdGenerator;
import org.springframework.stereotype.Repository;

/**
 * @author Terry Packer
 */
@Repository
public class UserRepository extends AbstractInMemoryRepository<User, Integer> {

    public UserRepository() {
        super(new IntegerIdGenerator());
    }
}
