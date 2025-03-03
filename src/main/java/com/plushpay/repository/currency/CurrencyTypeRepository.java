package com.plushpay.repository.currency;

import com.plushpay.repository.AbstractInMemoryRepository;
import com.plushpay.repository.LongIdGenerator;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Repository;

/**
 * @author Terry Packer
 */
@Repository
public class CurrencyTypeRepository extends AbstractInMemoryRepository<PyCurrencyType, Long> {

    public CurrencyTypeRepository() {
        super(new LongIdGenerator());
    }

    public PyCurrencyType getBaseCurrencyType() {
        return new PyCurrencyType(CurrencyCodeEnum.USD, 10000, ZonedDateTime.now(), true);
    }
}
