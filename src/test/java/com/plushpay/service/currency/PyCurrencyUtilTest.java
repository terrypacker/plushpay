package com.plushpay.service.currency;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Terry Packer
 */
@SpringBootTest
public class PyCurrencyUtilTest {

    @Test
    public void test() {
        PyCurrencyType usdType = new PyCurrencyType(CurrencyCodeEnum.USD, 10000,
            ZonedDateTime.now(), true);
        PyCurrencyType audType = new PyCurrencyType(CurrencyCodeEnum.AUD, 9200,
            ZonedDateTime.now(), true);

        PyCurrency usd = PyCurrencyUtil.createCurrency(10000, audType, usdType);

        PyCurrency aud = PyCurrencyUtil.createCurrency(10000, usdType, audType);

        assertThat(10869).isEqualTo(aud.getValue()); //1 USD gets you 1.08 AUD

        assertThat(9200).isEqualTo(usd.getValue()); //1AUD gets you .92 USD
    }

}
