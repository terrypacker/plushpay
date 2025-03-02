/**
 *
 */
package com.plushpay.service.trading.tradeGenerator;

import com.plushpay.repository.trading.trade.Trade;
import com.plushpay.repository.trading.trade.TradeRepository;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import com.plushpay.service.trading.trader.TraderHibernation;
import com.plushpay.repository.trading.trader.group.TraderGroupUtil;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tpacker
 */

public class TradeGeneratorAllTrades {

    private List<Trade> trades;
    private TraderGroupUtil buyers;
    private TraderGroupUtil sellers;
    private Trade chosenTrade;

    private PyCurrency totalBuyerBuyMismatch;
    private PyCurrency totalBuyerSellMismatch;

    private boolean tradeLoaded;
    private final TradeRepository tradeRepository;

    public TradeGeneratorAllTrades(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public void init() {

        this.setTrades(this.tradeRepository.getAll().collect(Collectors.toList()));

        long totalBuyerBuy = 0, totalBuyerSell = 0;

        //Compute mismatches
        for (int i = 0; i < this.trades.size(); i++) {
            totalBuyerBuy = totalBuyerBuy + this.trades.get(i).getBuyerBuyMismatch();
            totalBuyerSell = totalBuyerSell + this.trades.get(i).getBuyerSellMismatch();
        }

        if (this.trades.size() > 1) {
            this.setTotalBuyerBuyMismatch(new PyCurrency(totalBuyerBuy,
                this.trades.get(0).getBuyerGroup().getCurrencyToBuy().getType()));
            this.setTotalBuyerSellMismatch(new PyCurrency(totalBuyerSell,
                this.trades.get(0).getBuyerGroup().getCurrencyToSell().getType()));


        } else {
            //Need to init to 0
            this.setTotalBuyerBuyMismatch(new PyCurrency(0,
                new PyCurrencyType(CurrencyCodeEnum.USD, 0, ZonedDateTime.now())));
            this.setTotalBuyerSellMismatch(new PyCurrency(0,
                new PyCurrencyType(CurrencyCodeEnum.USD, 0, ZonedDateTime.now())));
        }

        this.tradeLoaded = false;
    }


    /**
     * collect data for the chosen Trade
     *
     * @throws Exception
     */
    public void collectData() throws Exception {

        TraderHibernation th = new TraderHibernation();
        this.buyers = new TraderGroupUtil(
            th.loadTradersWithGroupId(this.chosenTrade.getBuyerGroup().getGroupid()));
        this.sellers = new TraderGroupUtil(
            th.loadTradersWithGroupId(this.chosenTrade.getSellerGroup().getGroupid()));

        this.tradeLoaded = true;

    }


    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }


    public List<Trade> getTrades() {
        return trades;
    }


    public void setBuyers(TraderGroupUtil buyers) {
        this.buyers = buyers;
    }


    public TraderGroupUtil getBuyers() {
        return buyers;
    }


    public void setSellers(TraderGroupUtil sellers) {
        this.sellers = sellers;
    }


    public TraderGroupUtil getSellers() {
        return sellers;
    }


    public void setChosenTrade(Trade chosenTrade) {
        this.chosenTrade = chosenTrade;
    }


    public Trade getChosenTrade() {
        return chosenTrade;
    }


    public void setTradeLoaded(boolean tradeLoaded) {
        this.tradeLoaded = tradeLoaded;
    }


    public boolean isTradeLoaded() {
        return tradeLoaded;
    }

    /**
     * For use on page, return the number of trades loaded.
     *
     * @return
     */
    public int getTotalTrades() {
        return this.trades.size();
    }


    public void setTotalBuyerBuyMismatch(PyCurrency totalBuyerBuyMismatch) {
        this.totalBuyerBuyMismatch = totalBuyerBuyMismatch;
    }


    public PyCurrency getTotalBuyerBuyMismatch() {
        return totalBuyerBuyMismatch;
    }


    public void setTotalBuyerSellMismatch(PyCurrency totalBuyerSellMismatch) {
        this.totalBuyerSellMismatch = totalBuyerSellMismatch;
    }


    public PyCurrency getTotalBuyerSellMismatch() {
        return totalBuyerSellMismatch;
    }


}
