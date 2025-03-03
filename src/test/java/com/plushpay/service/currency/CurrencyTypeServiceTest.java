package com.plushpay.service.currency;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Terry Packer
 */
@SpringBootTest
public class CurrencyTypeServiceTest {

    @Autowired
    private CurrencyTypeService currencyTypeService;


    @Test
    public void getCurrentBaseCurrencyType() {
        PyCurrencyType base = this.currencyTypeService.getBaseCurrencyType();
        assertThat(base).isNotNull();
        assertThat(base.isBase()).isEqualTo(true);
        PyCurrencyType latest = this.currencyTypeService.getCurrentCurrencyType(base.getCode());
        assertThat(latest.getRateToBase()).isEqualTo(base.getRateToBase());
    }

    @Test
    public void insertCurrency() {
        PyCurrencyType newType = new PyCurrencyType(CurrencyCodeEnum.AUD, 67000,
            ZonedDateTime.now());
        PyCurrencyType save = currencyTypeService.save(newType).get();
        assertThat(save).isNotNull();
        assertThat(save.getCode()).isEqualTo(newType.getCode());
        assertThat(save.getRateToBase()).isEqualTo(newType.getRateToBase());

        PyCurrencyType found = currencyTypeService.findById(save.getId()).get();
        assertThat(found.getId()).isEqualTo(save.getId());
        assertThat(found.getCode()).isEqualTo(save.getCode());
        assertThat(found.getRateToBase()).isEqualTo(save.getRateToBase());
    }
}
