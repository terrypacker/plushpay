package com.plushpay.service.trading.traderSimulation;

import com.plushpay.repository.beneficiary.tradeBeneficiary.TradeBeneficiary;
import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.trader.TraderStatus;
import com.plushpay.repository.user.User;
import com.plushpay.service.currency.CurrencyTypeService;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.PyCurrencyUtil;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import com.plushpay.service.trader.TraderService;
import com.plushpay.service.user.UserService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraderTestDataSimulation extends Thread {

    private Log log = LogFactory.getLog(getClass());

    private long pollPeriod; // in ms
    private volatile boolean shutdown;
    private TraderTestData data;
    private int dataPos;

    private final UserService userService;
    private final CurrencyTypeService currencyTypeService;
    private final TraderService traderService;


    public TraderTestDataSimulation(UserService userService,
        CurrencyTypeService currencyTypeService,
        TraderService traderService) {
        super("Trader Test Data Simulation");
        this.pollPeriod = 5000;
        this.userService = userService;
        this.currencyTypeService = currencyTypeService;
        this.traderService = traderService;
        this.data = new TraderTestData();

    }

    public void run() {

        URL url = getClass()
            .getResource("/com/plushpay/testing/data/testTradeAmounts2.csv");
        try {
            this.data.loadData(new File(url.getFile()));
        } catch (ParseException e2) {
            this.log.error("Unable to parse data file.");
        } catch (IOException e2) {
            this.log.error("Unable to read from data file");
        }

        this.dataPos = 0;

        // Run forever
        while (!this.shutdown) {

            try {
                this.insertNewTraders();
            } catch (Exception e1) {
                this.log.error("Error Inserting New Traders", e1);
            }

            try {
                Thread.sleep(this.pollPeriod);
            } catch (InterruptedException e) {
                if (this.shutdown) {
                    break;
                } else {
                    this.log.error("Threading issues.", e);
                }
            }

            // Be sure to reset it
            if (this.dataPos == this.data.getData().size()) {
                this.dataPos = 0;
            }

        }// end while

        this.log.info("Shutting Down Trader Simulation");

    }

    /**
     * Insert new traders into DB
     * <p>
     * This was done as a fn so it can be tested.
     *
     * @throws Exception
     */
    public void insertNewTraders() throws Exception {
        /* This trader needs to be added to DB, but make sure we don't add twice */
        List<User> users = userService.getAll().toList();

        boolean currencyType = true;
        long toBuy;

        List<PyCurrency> toBuyList;
        List<Trader> traders;

        // Load a rate change if necessary
        PyCurrencyType usdType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType audType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        // Create one trader for each user each time around
        for (int i = 0; i < users.size(); i++) {

            toBuyList = new ArrayList<PyCurrency>();
            traders = new ArrayList<Trader>();
            // So we get buy/sell of differing currencies
            if (currencyType) {

                // Do Dollars
                toBuy = this.data.getData().get(this.dataPos).getAmount();
                this.dataPos++;

                // toBuyList.add(audUtil.toPyCurrencyFromValue(toBuy));
                toBuyList.add(new PyCurrency(toBuy, audType));
                traders = this.createNewTraders(users.get(i), toBuyList);

            } else {
                toBuy = this.data.getData().get(this.dataPos).getAmount();
                this.dataPos++;

                // toBuyList.add(usdUtil.toPyCurrencyFromValue(toBuy));
                toBuyList.add(new PyCurrency(toBuy, usdType));
                traders = this.createNewTraders(users.get(0), toBuyList);

            }

            // Swap currency to buy/sell
            currencyType = !currencyType;
            for (int j = 0; j < traders.size(); j++) {
                traders.set(j, traderService.save(traders.get(j)).get());
            }
        }
    }

    /**
     * Create a list of new Traders using the currencies provided
     *
     * @param user
     * @param toBuy
     * @return
     * @throws Exception
     */
    public List<Trader> createNewTraders(User user, List<PyCurrency> toBuy)
        throws Exception {

        PyCurrencyType usdType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType audType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        // Currencies for trader
        PyCurrency currencyToSell;

        Trader trader = null;

        List<Trader> traders = new ArrayList<Trader>();
        List<TradeBeneficiary> benies;

        // Cycle across the currency list and create a trader for each currency
        // for this user
        for (int i = 0; i < toBuy.size(); i++) {

            benies = new ArrayList<TradeBeneficiary>();
            // TODO Upgrade ENUM Types using com.payyourself.enumUserType
            if (toBuy.get(i).getType().getCode() == CurrencyCodeEnum.USD) {
                // Buying USD
                currencyToSell = PyCurrencyUtil.createCurrency(toBuy.get(i)
                        .getValue(), toBuy.get(i).getType(),
                    audType); // audUtil.toPyCurrencyFromBaseValue(toBuy.get(i).getBaseValue());

                // Create Beneficiaries
                for (int j = 0; j < user.getBeneficiaries().size(); j++) {
                    if (user.getBeneficiaries().get(j).getType() == toBuy
                        .get(i).getType().getCode()) {
                        /* Create ONE beneficiary and provide the amount */
                        TradeBeneficiary newBene = new TradeBeneficiary();
                        newBene.setAmount(
                            new PyCurrency(toBuy.get(i).getValue(), toBuy.get(i).getType()));
                        newBene.setBeneficiary(user.getBeneficiaries().get(j));
                        benies.add(newBene);
                        break;
                    }
                }

                if (benies.size() == 0) {
                    throw new Exception("Unable to find beneficiary for "
                        + user.getUsername());
                }

                // TODO Upgrade ENUM Types using com.payyourself.enumUserType
                trader = new Trader(null, user, toBuy.get(i), currencyToSell,
                    TraderStatus.CONFIRMED, benies);
                traders.add(trader); // Insert him to be persisted
                // TODO Upgrade ENUM Types using com.payyourself.enumUserType
            } else if (toBuy.get(i).getType().getCode() == CurrencyCodeEnum.AUD) {
                // Buying AUD
                currencyToSell = PyCurrencyUtil.createCurrency(toBuy.get(i)
                        .getValue(), toBuy.get(i).getType(),
                    usdType); // usdUtil.toPyCurrencyFromBaseValue(toBuy.get(i).getBaseValue());

                // Create Beneficiaries
                for (int j = 0; j < user.getBeneficiaries().size(); j++) {
                    if (user.getBeneficiaries().get(j).getType() == toBuy
                        .get(i).getType().getCode()) {
                        /* Create ONE beneficiary and provide the amount */
                        TradeBeneficiary newBene = new TradeBeneficiary();
                        newBene.setAmount(
                            new PyCurrency(toBuy.get(i).getValue(), toBuy.get(i).getType()));
                        newBene.setBeneficiary(user.getBeneficiaries().get(j));
                        benies.add(newBene);
                        break;
                    }
                }

                if (benies.size() == 0) {
                    throw new Exception("Unable to find beneficiary for "
                        + user.getUsername());
                }

                trader = new Trader(null, user, toBuy.get(i), currencyToSell,
                    TraderStatus.CONFIRMED, benies);
                traders.add(trader); // Insert him to be persisted

            }
        }// end for toBuy.size()

        return traders;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public void shutDown() {
        this.setShutdown(true);
        this.interrupt();
    }

    public void startUp() {
        // TODO Check thread state before starting
        this.shutdown = false;
        this.start();

    }


}
