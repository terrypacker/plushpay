package com.plushpay.repository.trading.trader;

import com.plushpay.repository.AbstractInMemoryRepository;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

/**
 * @author Terry Packer
 */
@Repository
public class TraderRepository extends AbstractInMemoryRepository<Trader, Long> {

    public Stream<Trader> getSelling(TraderStatus traderStatus, CurrencyCodeEnum currencyCodeEnum) {
        return this.rows.stream().filter(row -> {
            return row.getStatus().equals(traderStatus) && row.getCurrencyToSell()
                .equals(currencyCodeEnum);
        });
    }

    public Stream<Trader> getSelling(TraderStatus traderStatus, CurrencyCodeEnum currencyCodeEnum,
        List<Long> excludeIds) {
        return this.rows.stream().filter(row -> {
            return row.getStatus().equals(traderStatus) && row.getCurrencyToSell()
                .equals(currencyCodeEnum) && !excludeIds.contains(row.getId());
        });
    }
}

