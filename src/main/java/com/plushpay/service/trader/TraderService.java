package com.plushpay.service.trader;

import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.trader.TraderRepository;
import com.plushpay.repository.trader.TraderStatus;
import com.plushpay.service.AbstractPlushPayService;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * @author Terry Packer
 */
@Service
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

    public Stream<Trader> getFreeTradersExcept(CurrencyCodeEnum buying, CurrencyCodeEnum selling,
        List<Trader> except) {
        return this.repository.getFreeTradersExcept(buying, selling, except);
    }

    public Stream<Trader> getFreeTradersExcept(CurrencyCodeEnum buying, CurrencyCodeEnum selling,
        List<Trader> except, boolean sortOnBuying) {
        return this.repository.getFreeTradersExcept(buying, selling, except, sortOnBuying);
    }

    public Stream<Trader> getWithGroupId(long groupId) {
        return repository.getWithGroupId(groupId);
    }

    public Stream<Trader> getFreeTraders() {
        return repository.getFreeTraders();
    }

    public Stream<Trader> getFreeTraders(CurrencyCodeEnum buying, CurrencyCodeEnum selling) {
        return repository.getFreeTraders(buying, selling);
    }
}
