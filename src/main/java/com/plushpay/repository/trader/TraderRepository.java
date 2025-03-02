package com.plushpay.repository.trader;

import com.plushpay.repository.AbstractInMemoryRepository;
import com.plushpay.repository.LongIdGenerator;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

/**
 * @author Terry Packer
 */
@Repository
public class TraderRepository extends AbstractInMemoryRepository<Trader, Long> {

    public TraderRepository() {
        super(new LongIdGenerator());
    }

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

    public Stream<Trader> getFreeTradersExcept(CurrencyCodeEnum buying, CurrencyCodeEnum selling,
        List<Trader> except) {
        List<Long> excludeIds = except.stream().map(Trader::getId).collect(Collectors.toList());
        return this.rows.stream().filter(t -> {
            return t.getGroup() == null && t.getCurrencyToBuy().equals(buying)
                && t.getCurrencyToSell().equals(selling)
                && !excludeIds.contains(t);
        });
    }


    /**
     * @param buying
     * @param selling
     * @param except
     * @param sortOnBuying - sort on buying or selling currency
     * @return
     */
    public Stream<Trader> getFreeTradersExcept(CurrencyCodeEnum buying, CurrencyCodeEnum selling,
        List<Trader> except, boolean sortOnBuying) {
        return getFreeTradersExcept(buying, selling, except).sorted((t1, t2) -> {
            if (sortOnBuying) {
                //TODO verify this sorts ASC
                return Math.toIntExact(
                    t1.getCurrencyToBuy().getType().getRateToBase() - t2.getCurrencyToBuy()
                        .getType().getRateToBase());
            } else {
                //TODO verify this sorts ASC
                return Math.toIntExact(
                    t1.getCurrencyToSell().getType().getRateToBase() - t2.getCurrencyToSell()
                        .getType().getRateToBase());
            }
        }).sorted((t1, t2) -> {
            if (sortOnBuying) {
                //TODO verify this sorts ASC
                return Math.toIntExact(
                    t1.getCurrencyToBuy().getValue() - t2.getCurrencyToBuy().getValue());
            } else {
                //TODO verify this sorts ASC
                return Math.toIntExact(
                    t1.getCurrencyToSell().getValue() - t2.getCurrencyToSell().getValue());
            }
        });
    }

    public Stream<Trader> getWithGroupId(long groupId) {
        return this.rows.stream().filter(t -> {
            if (t.getGroup() != null) {
                return t.getGroup().equals(groupId);
            } else {
                return false;
            }
        });
    }

    /**
     * Get traders not set into a group
     *
     * @return
     */
    public Stream<Trader> getFreeTraders() {
        return this.rows.stream().filter(t -> {
            return t.getGroup() == null;
        });
    }
}

