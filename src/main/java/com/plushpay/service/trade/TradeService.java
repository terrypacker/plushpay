package com.plushpay.service.trade;

import com.plushpay.repository.trade.Trade;
import com.plushpay.repository.trade.TradeRepository;
import com.plushpay.repository.trade.TradeStatus;
import com.plushpay.service.AbstractPlushPayService;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * @author Terry Packer
 */
@Service
public class TradeService extends AbstractPlushPayService<Trade, Long, TradeRepository> {


    public TradeService(TradeRepository tradeRepository) {
        super(tradeRepository);
    }

    public Stream<Trade> getAllWithStatus(TradeStatus tradeStatus) {
        return repository.getAllWithStatus(tradeStatus);
    }

    public Stream<Trade> getAllWithStatusExcluding(TradeStatus tradeStatus,
        List<Long> processedTradeIds) {
        return repository.loadAllWithStatusExcluding(tradeStatus, processedTradeIds);
    }
}
