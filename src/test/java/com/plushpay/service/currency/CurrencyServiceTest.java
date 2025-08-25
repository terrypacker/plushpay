package com.plushpay.service.currency;


import static org.assertj.core.api.Assertions.assertThat;

import com.plushpay.service.currency.code.CurrencyCodeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CurrencyTypeService currencyTypeService;

    @Test
    void convertCurrency() {

        long value = 1001;
        //Get current USD
        PyCurrency usd = currencyService.getCurrentCurrency(value, CurrencyCodeEnum.USD);

        //Convert to AUD
        PyCurrency aud = currencyService.convertCurrency(usd,
            currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.AUD));

        //Convert back to usd
        PyCurrency usd2 = currencyService.convertCurrency(aud,
            currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.USD));
        assertThat(value).isEqualTo(usd2.getValue());
    }

}
