package com.plushpay.service.currency;

import com.plushpay.repository.currency.CurrencyTypeRepository;
import com.plushpay.service.AbstractPlushPayService;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;

/**
 * @author Terry Packer
 */
@Service
public class CurrencyTypeService extends
    AbstractPlushPayService<PyCurrencyType, Long, CurrencyTypeRepository> {

    public CurrencyTypeService(CurrencyTypeRepository currencyTypeRepository) {
        super(currencyTypeRepository);
    }

    public PyCurrencyType getCurrentCurrencyType(CurrencyCodeEnum currencyCode) {
        switch (currencyCode) {
            case USD -> {
                return getBaseCurrencyType();
            }
            case AUD -> {
                long rateToBase = 6980; //TODO Get this from a source
                return new PyCurrencyType(currencyCode, rateToBase, ZonedDateTime.now());
            }
            default -> {
                throw new RuntimeException("Unsupported currency code " + currencyCode);
            }
        }
    }


    public PyCurrencyType getBaseCurrencyType() {
        return repository.getBaseCurrencyType();
    }
}
