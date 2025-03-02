package com.plushpay.repository.trade;

import com.plushpay.repository.AbstractInMemoryRepository;
import com.plushpay.repository.LongIdGenerator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

/**
 * @author Terry Packer
 */
@Repository
public class TradeRepository extends AbstractInMemoryRepository<Trade, Long> {

    public TradeRepository() {
        super(new LongIdGenerator());
    }

    public Stream<Trade> getAllWithStatus(TradeStatus status) {
        return this.rows.stream().filter(t -> t.getStatus() == status);
    }

    public Stream<Trade> loadAllWithStatusExcluding(TradeStatus tradeStatus,
        List<Long> processedTradeIds) {
        return this.rows.stream().filter(t -> {
            return t.getStatus() == tradeStatus && !processedTradeIds.contains(t.getId());
        });
    }
}
