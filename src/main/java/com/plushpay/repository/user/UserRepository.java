package com.plushpay.repository.user;

import com.plushpay.repository.AbstractInMemoryRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Terry Packer
 */
@Repository
public class UserRepository extends AbstractInMemoryRepository<User, Integer> {

}
