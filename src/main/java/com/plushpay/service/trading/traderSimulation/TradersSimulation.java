package com.plushpay.service.trading.traderSimulation;

import com.plushpay.repository.beneficiary.tradeBeneficiary.TradeBeneficiary;
import com.plushpay.repository.trading.trader.Trader;
import com.plushpay.repository.trading.trader.TraderStatus;
import com.plushpay.repository.user.User;
import com.plushpay.service.currency.CurrencyService;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.PyCurrencyUtil;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import com.plushpay.service.trading.trader.TraderService;
import com.plushpay.service.user.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class will simulate traders inserting trades into the DB.
 *
 * @author tpacker
 */
public class TradersSimulation extends Thread {

    private final Log log = LogFactory.getLog(getClass());

    private static final int MAX_AMOUNT = 10000; //In whole dollars
    private static int POLL_PERIOD = 5000; //in ms
    private volatile boolean shutdown;

    private final UserService userService;
    private final CurrencyService currencyService;
    private final TraderService traderService;

    public TradersSimulation(UserService userService, CurrencyService currencyService,
        TraderService traderServic) {
        super("Trader Simulation");
        this.userService = userService;
        this.currencyService = currencyService;
        this.traderService = traderServic;
    }

    public void run() {

        //Run forever
        while (!this.shutdown) {

            try {
                this.insertNewTraders();
            } catch (Exception e1) {
                this.log.error("Error Inserting New Traders", e1);
            }

            try {
                Thread.sleep(TradersSimulation.POLL_PERIOD);
            } catch (InterruptedException e) {
                if (this.shutdown) {
                    break;
                } else {
                    this.log.error("Threading issues.", e);
                }
            }


        }//end while

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

        /*This trader needs to be added to DB, but make sure we don't add twice */

        List<User> users = userService.getAll().toList();

        boolean currencyType = true;
        long toBuy;

        Random rand = new Random();

        List<PyCurrency> toBuyList;
        List<Trader> traders;
        long cents;
        // Load a rate change if necessary
        PyCurrencyType usdType = currencyService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType audType = currencyService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        //Create one trader for each user each time around
        for (int i = 0; i < users.size(); i++) {

            toBuyList = new ArrayList<PyCurrency>();
            traders = new ArrayList<Trader>();
            //So we get buy/sell of differing currencies
            if (currencyType) {

                //Do Dollars
                toBuy = ((long) (rand.nextInt(TradersSimulation.MAX_AMOUNT) * 10000));

                //Do Cents
                cents = (long) (Math.random() * 10000);

                toBuy = toBuy + cents;

                toBuyList.add(new PyCurrency(toBuy, audType));
                traders = this.createNewTraders(users.get(i), toBuyList);

            } else {
                toBuy = ((long) (rand.nextInt(TradersSimulation.MAX_AMOUNT) * 10000));

                cents = (long) (Math.random() * 10000);

                toBuy = toBuy + cents;

                toBuyList.add(new PyCurrency(toBuy, usdType));
                traders = this.createNewTraders(users.get(0), toBuyList);

            }

            //Swap currency to buy/sell
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
    public List<Trader> createNewTraders(User user, List<PyCurrency> toBuy) throws Exception {

        // Load a rate change if necessary
        PyCurrencyType usdType = currencyService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType audType = currencyService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        //Currencies for trader
        PyCurrency currencyToSell;

        Trader trader = null;

        List<Trader> traders = new ArrayList<Trader>();
        List<TradeBeneficiary> benies;

        String buying;
        String selling;

        //Cycle across the currency list and create a trader for each currency for this user
        for (int i = 0; i < toBuy.size(); i++) {

            benies = new ArrayList<TradeBeneficiary>();
            //TODO Upgrade ENUM Types using com.payyourself.enumUserType
            if (toBuy.get(i).getType().getCode() == CurrencyCodeEnum.USD) {
                //Buying USD
                currencyToSell = PyCurrencyUtil.createCurrency(toBuy.get(i).getValue(),
                    toBuy.get(i).getType(),
                    audType); //audUtil.toPyCurrencyFromBaseValue(toBuy.get(i).getBaseValue());

                //Create Beneficiaries
                for (int j = 0; j < user.getBeneficiaries().size(); j++) {
                    if (user.getBeneficiaries().get(j).getType() == toBuy.get(i).getType()
                        .getCode()) {
                        /* Create ONE beneficiary and provide the amount*/
                        TradeBeneficiary newBene = new TradeBeneficiary();
                        newBene.setAmount(toBuy.get(i));
                        newBene.setBeneficiary(user.getBeneficiaries().get(j));
                        benies.add(newBene);
                        break;
                    }
                }

                if (benies.size() == 0) {
                    throw new Exception("Unable to find beneficiary for " + user.getUsername());
                }

                //TODO Upgrade ENUM Types using com.payyourself.enumUserType
                trader = new Trader(null, user, toBuy.get(i),
                    currencyToSell, TraderStatus.CONFIRMED.name(),
                    benies);
                traders.add(trader); //Insert him to be persisted
                //TODO Upgrade ENUM Types using com.payyourself.enumUserType
            } else if (toBuy.get(i).getType().getCode() == CurrencyCodeEnum.AUD) {
                //Buying AUD
                currencyToSell = PyCurrencyUtil.createCurrency(toBuy.get(i).getValue(),
                    toBuy.get(i).getType(),
                    usdType); //usdUtil.toPyCurrencyFromBaseValue(toBuy.get(i).getBaseValue());

                //Create Beneficiaries
                for (int j = 0; j < user.getBeneficiaries().size(); j++) {
                    if (user.getBeneficiaries().get(j).getType() == toBuy.get(i).getType()
                        .getCode()) {
                        /* Create ONE beneficiary and provide the amount*/
                        TradeBeneficiary newBene = new TradeBeneficiary();
                        newBene.setAmount(toBuy.get(i));
                        newBene.setBeneficiary(user.getBeneficiaries().get(j));
                        benies.add(newBene);
                        break;
                    }
                }

                if (benies.size() == 0) {
                    throw new Exception("Unable to find beneficiary for " + user.getUsername());
                }

                trader = new Trader(null, user, toBuy.get(i),
                    currencyToSell, TraderStatus.CONFIRMED.name(),
                    benies);
                traders.add(trader); //Insert him to be persisted

            }
        }

        return traders;
    }


    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }


    public void shutDown() {
        this.setShutdown(true);
        this.interrupt();
    }


}
