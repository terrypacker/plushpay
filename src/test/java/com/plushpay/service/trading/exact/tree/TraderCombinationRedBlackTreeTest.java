package com.plushpay.service.trading.exact.tree;

import static org.junit.jupiter.api.Assertions.fail;

import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.tradergroup.TraderGroupUtil;
import com.plushpay.service.currency.CurrencyService;
import com.plushpay.service.currency.CurrencyTypeService;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import com.plushpay.service.trade.TradeService;
import com.plushpay.service.trader.TraderService;
import com.plushpay.service.tradergroup.TraderGroupService;
import com.plushpay.service.trading.tree.TraderCombinationRedBlackTreeManager;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Terry Packer
 */
@SpringBootTest
class TraderCombinationRedBlackTreeTest {

    private Log log = LogFactory.getLog(TraderCombinationRedBlackTreeTest.class);
    private PyCurrencyType buyingCurrency;
    private PyCurrencyType sellingCurrency;
    private float trimWithin;

    @Autowired
    private CurrencyTypeService currencyTypeService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TradeService tradeService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private TraderGroupService traderGroupService;

    /**
     * Test simulation
     */
    void test() {

        PyCurrencyType usd = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType aud = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        this.buyingCurrency = aud;
        this.sellingCurrency = usd;

        /* Setup Group 1 To Buy AUD and Sell USD*/

        /* Fill group 1 */
        TraderGroupUtil buyerUtil = new TraderGroupUtil(aud, usd);
        List<Trader> buyers = new ArrayList<Trader>();

        //Setup Trader 1 to buy AUD and sell USD
        Trader audUsdTrader1 = new Trader();
        PyCurrency buyAudUsd1 = new PyCurrency(104, aud);
        PyCurrency sellAudUsd1 = currencyService.convertCurrency(buyAudUsd1, usd);
        audUsdTrader1.setCurrencyToBuy(buyAudUsd1);
        audUsdTrader1.setCurrencyToSell(sellAudUsd1);
        try {
            buyerUtil.add(audUsdTrader1);
            buyers.add(audUsdTrader1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        //Setup trader 2
        Trader audUsdTrader2 = new Trader();
        PyCurrency buyAudUsd2 = new PyCurrency(102, aud);
        PyCurrency sellAudUsd2 = currencyService.convertCurrency(buyAudUsd2, usd);
        audUsdTrader2.setCurrencyToBuy(buyAudUsd2);
        audUsdTrader2.setCurrencyToSell(sellAudUsd2);
        try {
            buyerUtil.add(audUsdTrader2);
            buyers.add(audUsdTrader2);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        //Setup trader 3
        Trader audUsdTrader3 = new Trader();
        PyCurrency buyAudUsd3 = new PyCurrency(201, aud);
        PyCurrency sellAudUsd3 = currencyService.convertCurrency(buyAudUsd3, usd);
        audUsdTrader3.setCurrencyToBuy(buyAudUsd3);
        audUsdTrader3.setCurrencyToSell(sellAudUsd3);
        try {
            buyerUtil.add(audUsdTrader3);
            buyers.add(audUsdTrader3);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        //Setup trader 4
        Trader audUsdTrader4 = new Trader();
        PyCurrency buyAudUsd4 = new PyCurrency(101, aud);
        PyCurrency sellAudUsd4 = currencyService.convertCurrency(buyAudUsd4, usd);
        audUsdTrader4.setCurrencyToBuy(buyAudUsd4);
        audUsdTrader4.setCurrencyToSell(sellAudUsd4);
        try {
            buyerUtil.add(audUsdTrader4);
            buyers.add(audUsdTrader4);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        /* Setup group 2 To Buy USD and Sell AUD */
        TraderGroupUtil sellerUtil = new TraderGroupUtil(usd, aud);
        List<Trader> sellers = new ArrayList<Trader>();

        PyCurrency buyUsdAud1 = new PyCurrency(101, usd);
        PyCurrency sellUsdAud1 = currencyService.convertCurrency(buyUsdAud1, aud);
        Trader usdAudTrader1 = new Trader();
        usdAudTrader1.setCurrencyToBuy(buyUsdAud1);
        usdAudTrader1.setCurrencyToSell(sellUsdAud1);
        try {
            sellerUtil.add(usdAudTrader1);
            sellers.add(usdAudTrader1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        PyCurrency buyUsdAud2 = new PyCurrency(102, usd);
        PyCurrency sellUsdAud2 = currencyService.convertCurrency(buyUsdAud2, aud);
        Trader usdAudTrader2 = new Trader();
        usdAudTrader1.setCurrencyToBuy(buyUsdAud2);
        usdAudTrader1.setCurrencyToSell(sellUsdAud2);
        try {
            sellerUtil.add(usdAudTrader2);
            sellers.add(usdAudTrader2);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        PyCurrency buyUsdAud3 = new PyCurrency(201, usd);
        PyCurrency sellUsdAud3 = currencyService.convertCurrency(buyUsdAud3, aud);
        Trader usdAudTrader3 = new Trader();
        usdAudTrader3.setCurrencyToBuy(buyUsdAud3);
        usdAudTrader3.setCurrencyToSell(sellUsdAud3);
        try {
            sellerUtil.add(usdAudTrader3);
            sellers.add(usdAudTrader3);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        PyCurrency buyUsdAud4 = new PyCurrency(101, usd);
        PyCurrency sellUsdAud4 = currencyService.convertCurrency(buyUsdAud4, aud);
        Trader usdAudTrader4 = new Trader();
        usdAudTrader4.setCurrencyToBuy(buyUsdAud4);
        usdAudTrader4.setCurrencyToSell(sellUsdAud4);
        try {
            sellerUtil.add(usdAudTrader4);
            sellers.add(usdAudTrader4);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TraderCombinationRedBlackTreeManager manager = new TraderCombinationRedBlackTreeManager(
            tradeService,
            traderService,
            traderGroupService,
            buyingCurrency,
            sellingCurrency);

        this.trimWithin = .000000001f;

        this.log.info("Starting Trade Tree Thread.");
        try {
            manager.insertBuyers(buyers);
            manager.insertSellers(sellers);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        //TODO Finish this test

    }
}
