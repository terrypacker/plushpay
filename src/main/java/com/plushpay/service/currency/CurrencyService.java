package com.plushpay.service.currency;

import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    private final CurrencyTypeService currencyTypeService;

    public CurrencyService(CurrencyTypeService currencyTypeService) {
        this.currencyTypeService = currencyTypeService;
    }

    /**
     * Retrieves the current currency object based on the provided value and currency code.
     *
     * @param value        the numeric value representing the amount of the currency
     * @param currencyCode the code representing the type of the currency
     * @return a PyCurrency instance representing the specified value in the given currency type
     */
    public PyCurrency getCurrentCurrency(long value, CurrencyCodeEnum currencyCode) {
        PyCurrencyType currencyType = this.currencyTypeService.getCurrentCurrencyType(currencyCode);
        return new PyCurrency(value, currencyType);
    }

    /**
     * Converts a given currency to a specified target currency type.
     *
     * @param from the source currency to be converted
     * @param to   the target currency type for conversion
     * @return the converted currency object in the target type
     */
    public PyCurrency convertCurrency(PyCurrency from, PyCurrencyType to) {
        long baseFrom = from.getValue() * from.getType().getRateToBase();
        long baseTo = baseFrom / to.getRateToBase();
        return new PyCurrency(baseTo, to);
    }

}
