package com.plushpay.service.tradergroup;

import com.plushpay.repository.tradergroup.TraderGroup;
import com.plushpay.repository.tradergroup.TraderGroupRepository;
import com.plushpay.service.AbstractPlushPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Terry Packer
 */
@Service
public class TraderGroupService extends
    AbstractPlushPayService<TraderGroup, Long, TraderGroupRepository> {

    @Autowired
    public TraderGroupService(TraderGroupRepository repository) {
        super(repository);
    }
}
