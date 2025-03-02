package com.plushpay.repository.tradergroup;

import com.plushpay.repository.AbstractInMemoryRepository;
import com.plushpay.repository.LongIdGenerator;
import org.springframework.stereotype.Repository;

/**
 * @author Terry Packer
 */
@Repository
public class TraderGroupRepository extends AbstractInMemoryRepository<TraderGroup, Long> {

    public TraderGroupRepository() {
        super(new LongIdGenerator());
    }
}
