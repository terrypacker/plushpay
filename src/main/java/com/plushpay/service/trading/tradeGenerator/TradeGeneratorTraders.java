/**
 *
 */
package com.plushpay.service.trading.tradeGenerator;

import com.plushpay.repository.trader.Trader;
import com.plushpay.service.trader.TraderService;
import java.util.List;

/**
 * @author tpacker
 */
public class TradeGeneratorTraders {

    private List<Trader> freeTraders;
    private List<Trader> allTraders;

    private final TraderService traderService;

    public TradeGeneratorTraders(TraderService traderService) {
        this.traderService = traderService;
        this.freeTraders = traderService.getFreeTraders().toList();
        this.allTraders = traderService.getAll().toList();

    }

    public void setFreeTraders(List<Trader> freeTraders) {
        this.freeTraders = freeTraders;
    }

    public List<Trader> getFreeTraders() {
        return freeTraders;
    }

    public int getNumFreeTraders() {
        return this.freeTraders.size();
    }

    public int getNumAllTraders() {
        return this.allTraders.size();
    }

    public void setAllTraders(List<Trader> allTraders) {
        this.allTraders = allTraders;
    }

    public List<Trader> getAllTraders() {
        return allTraders;
    }

}
