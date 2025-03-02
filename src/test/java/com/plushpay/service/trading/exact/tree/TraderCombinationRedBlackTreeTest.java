package com.plushpay.service.trading.exact.tree;

import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.tradergroup.TraderGroupUtil;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.PyCurrencyUtil;
import com.plushpay.service.currency.type.PyCurrencyType;
import com.plushpay.service.trade.TradeService;
import com.plushpay.service.trader.TraderService;
import com.plushpay.service.tradergroup.TraderGroupService;
import com.plushpay.service.trading.tree.TraderCombinationNode;
import com.plushpay.service.trading.tree.TraderCombinationRedBlackTree;
import com.plushpay.service.trading.tree.TraderCombinationRedBlackTreeManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Terry Packer
 */
@SpringBootTest
public class TraderCombinationRedBlackTreeTest {

    @Autowired
    private TradeService tradeService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private TraderGroupService traderGroupService;

    /**
     * Test simulation
     */
    public void test() {

        String usdCode = "USD";
        long usdRateToBase = 10000;
        String usdSymbol = "$";
        PyCurrencyType usd = new PyCurrencyType(usdCode, usdRateToBase, usdSymbol);

        String audCode = "AUD";
        long audRateToBase = 10000;
        String audSymbol = "$";
        PyCurrencyType aud = new PyCurrencyType(audCode, audRateToBase, audSymbol);

        /*Create the util */
        PyCurrencyUtil audUtil = new PyCurrencyUtil(aud);
        PyCurrencyUtil usdUtil = new PyCurrencyUtil(usd);

        /* Setup Group 1 To Buy AUD and Sell USD*/

        /* Fill group 1 */
        TraderGroupUtil buyerUtil = new TraderGroupUtil(aud, usd);
        User buyerUser = new User("tpacker", "Terry", "Packer", "shithead",
            "tpacker@terrypacker.com");
        List<Trader> buyers = new ArrayList<Trader>();


        /* Generate a The buyer values */
        PyCurrency toBuy = audUtil.toPyCurrencyFromValue(104);
        PyCurrency toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        Trader newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            buyerUtil.add(newTrader);
            buyers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        toBuy = audUtil.toPyCurrencyFromValue(102);
        toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            buyerUtil.add(newTrader);
            buyers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        toBuy = audUtil.toPyCurrencyFromValue(201);
        toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            buyerUtil.add(newTrader);
            buyers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        toBuy = audUtil.toPyCurrencyFromValue(101);
        toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            buyerUtil.add(newTrader);
            buyers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




        /* Setup group 2 To Buy USD and Sell AUD */
        TraderGroupUtil sellerUtil = new TraderGroupUtil(usd, aud);
        List<Trader> sellers = new ArrayList<Trader>();
        User sellerUser = new User("jallemann", "Jeanne", "Allemann", "shithead",
            "jeanneallemann@hotmail.com");

        toBuy = usdUtil.toPyCurrencyFromValue(101);
        toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            sellerUtil.add(newTrader);
            sellers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        toBuy = usdUtil.toPyCurrencyFromValue(102);
        toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            sellerUtil.add(newTrader);
            sellers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        toBuy = usdUtil.toPyCurrencyFromValue(201);
        toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            sellerUtil.add(newTrader);
            sellers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        toBuy = usdUtil.toPyCurrencyFromValue(101);
        toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
        newTrader = new Trader();
        newTrader.setCurrencyToBuy(toBuy);
        newTrader.setCurrencyToSell(toSell);
        try {
            sellerUtil.add(newTrader);
            sellers.add(newTrader);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        TraderCombinationRedBlackTreeManager manager = new TraderCombinationRedBlackTreeManager();

        this.trimWithin = .000000001f;

        this.buyingCurrency = aud;
        this.sellingCurrency = usd;

        PyCurrency buyZero = new PyCurrency();
        buyZero.setType(this.buyingCurrency);
        buyZero.setValue(0);
        buyZero.setBaseValue(0);

        PyCurrency sellZero = new PyCurrency();
        sellZero.setType(this.sellingCurrency);
        sellZero.setValue(0);
        sellZero.setBaseValue(0);

        Trader zeroBuyer = new Trader();
        zeroBuyer.setCurrencyToBuy(buyZero);
        zeroBuyer.setCurrencyToSell(sellZero);

        Trader zeroSeller = new Trader();
        zeroSeller.setCurrencyToBuy(sellZero);
        zeroSeller.setCurrencyToSell(buyZero);

        TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
        TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
        TraderCombinationNode zeroNode = new TraderCombinationNode(zeroBuyers, zeroSellers);

        this.log = Logger.getLogger(this.getClass());
        this.log.info("Starting Trade Tree Thread.");

        this.currentBuyers = new ArrayList<Trader>();
        this.currentSellers = new ArrayList<Trader>();
        this.tree = new TraderCombinationRedBlackTree();//new TraderCombinationsTree(zeroNode);
        this.tree.put(0, zeroNode);

        try {
            this.insertBuyers(buyers);
            this.insertSellers(sellers);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.tree.printKeys();
    }
}
