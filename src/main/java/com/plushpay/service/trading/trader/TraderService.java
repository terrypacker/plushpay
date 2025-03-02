package com.plushpay.service.trading.trader;

import com.plushpay.repository.trading.trader.Trader;
import com.plushpay.repository.trading.trader.TraderRepository;
import com.plushpay.repository.trading.trader.TraderStatus;
import com.plushpay.service.AbstractPlushPayService;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Terry Packer
 */
public class TraderService extends AbstractPlushPayService<Trader, Long, TraderRepository> {

    public TraderService(TraderRepository repository) {
        super(repository);
    }

    public Stream<Trader> getSelling(TraderStatus traderStatus, CurrencyCodeEnum currencyCodeEnum) {
        return this.repository.getSelling(traderStatus, currencyCodeEnum);
    }

    public Stream<Trader> getSelling(TraderStatus traderStatus, CurrencyCodeEnum currencyCodeEnum,
        List<Long> exludeIds) {
        return this.repository.getSelling(traderStatus, currencyCodeEnum, exludeIds);
    }
}
