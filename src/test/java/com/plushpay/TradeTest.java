package com.plushpay;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.plushpay.repository.trade.Trade;
import com.plushpay.repository.trade.TradeStatus;
import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.trader.TraderStatus;
import com.plushpay.repository.tradergroup.TraderGroup;
import com.plushpay.repository.tradergroup.TraderGroupUtil;
import com.plushpay.repository.user.User;
import com.plushpay.service.banking.au.AudBankSimulator;
import com.plushpay.service.banking.au.AudTradeCreditManager;
import com.plushpay.service.banking.au.AudTradeDebitManager;
import com.plushpay.service.banking.au.AudTraderDepositSimulator;
import com.plushpay.service.currency.CurrencyTypeService;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.PyCurrencyUtil;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import com.plushpay.service.trade.TradeService;
import com.plushpay.service.trader.TraderService;
import com.plushpay.service.tradergroup.TraderGroupService;
import com.plushpay.service.trading.traderSimulation.TradersSimulation;
import com.plushpay.service.user.UserService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TradeTest {

    private final Log log = LogFactory.getLog(getClass());

    //List of amounts to Buy
    private List<PyCurrency> audToBuy;
    private List<PyCurrency> usdToBuy;

    //List of traders
    private List<Trader> audBuyers;
    private List<Trader> usdBuyers;

    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyTypeService currencyTypeService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private TradersSimulation tradersSimulation;
    @Autowired
    AudTraderDepositSimulator audTraderDepositSimulator;
    @Autowired
    AudBankSimulator audBankSimulator;
    @Autowired
    private TraderGroupService traderGroupService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    AudTradeCreditManager audTradeCreditManager;
    @Autowired
    AudTradeDebitManager audTradeDebitManager;

    public TradeTest() {

        List<Long> audAmounts = new ArrayList<Long>();
        audAmounts.add(1000000L);
        audAmounts.add(2000000L);

        //Changing these will result in a failed test due to the way we extract rates from DB during testing
        List<PyCurrencyType> audRates = new ArrayList<PyCurrencyType>();
        audRates.add(new PyCurrencyType(CurrencyCodeEnum.AUD, 9200L, ZonedDateTime.now()));
        audRates.add(new PyCurrencyType(CurrencyCodeEnum.AUD, 9200L, ZonedDateTime.now()));

        try {
            this.audToBuy = this.generateAudCurrency(audAmounts, audRates);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        this.audBuyers = this.generateTraders(this.audToBuy);

        List<Long> usdAmounts = new ArrayList<Long>();
        usdAmounts.add(3000000L);
        usdAmounts.add(4000000L);

        List<PyCurrencyType> usdRates = new ArrayList<PyCurrencyType>();
        ZonedDateTime now = ZonedDateTime.now();
        usdRates.add(new PyCurrencyType(CurrencyCodeEnum.USD, 10000L, now));
        usdRates.add(new PyCurrencyType(CurrencyCodeEnum.USD, 10000L, now));

        try {
            this.usdToBuy = this.generateUsdCurrency(usdAmounts, usdRates);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        this.usdBuyers = this.generateTraders(this.usdToBuy);
    }

    private List<Trader> generateTraders(List<PyCurrency> toBuy) {

        //Get the User data from the DB.
        List<User> users = userService.getAll().toList();
        assertThat(users).isNotNull();

        //Create a Traders Simulation to help generate Traders
        List<Trader> traders = null;
        try {
            traders = tradersSimulation.createNewTraders(users.get(0), toBuy);
        } catch (Exception e) {
            this.log.error("Unable to generate AUD Buyers", e);
            fail(e.getMessage());
        }

        return traders;

    }

    private List<PyCurrency> generateUsdCurrency(List<Long> amounts, List<PyCurrencyType> rates)
        throws Exception {

        if (amounts.size() != rates.size()) {
            throw new Exception("amounts.size() doesn't match rates.size()");
        }

        List<PyCurrency> usds = new ArrayList<PyCurrency>();

        for (int i = 0; i < amounts.size(); i++) {
            usds.add(new PyCurrency(amounts.get(i), rates.get(i)));
        }

        return usds;

    }

    private List<PyCurrency> generateAudCurrency(List<Long> amounts, List<PyCurrencyType> rates)
        throws Exception {

        if (amounts.size() != rates.size()) {
            throw new Exception("amounts.size() doesn't match rates.size()");
        }

        List<PyCurrency> auds = new ArrayList<PyCurrency>();

        for (int i = 0; i < amounts.size(); i++) {
            auds.add(new PyCurrency(amounts.get(i), rates.get(i)));
        }

        return auds;
    }

    public void testInsertRate() {

        this.log.info("Running Test Insert Rate");

        //Load the Types from the DB
        PyCurrencyType usdType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType audType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        //Load the rates from a file
        //TODO load rates from test file

        this.log.info("Setting " + audType.getCode() + " to " + 9200);
        audType.setRateToBase(9200); //

        this.log.info("Setting " + usdType.getCode() + " to " + 10000);
        usdType.setRateToBase(10000); //

        //Persist them to the DB
        currencyTypeService.save(usdType);
        currencyTypeService.save(audType);
    }

    public void testCurrencyUtil() {
        //Get the types from the DB
        PyCurrencyType usdType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType audType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        //First test that it works for 1 dollar
        //long audBase = (this.types.get(1).getRateToBase() *this.types.get(1).getRateToBase())/this.types.get(0).getRateToBase();
        PyCurrency testUsd = new PyCurrency(10000, usdType);
        PyCurrency testAud = new PyCurrency(10869, audType); //1 AUD

        assertThat(testAud).isEqualTo(
            PyCurrencyUtil.createCurrency(testUsd.getValue(), testUsd.getType(),
                audType));

    }

    /**
     * This test assumes that the default test data is in the DB
     */
    public void testInsertTraders() {
        this.log.info("Running Test Insert Traders");

        //GENERATE THE AUD BUYERS
        assertThat(this.audBuyers).isNotNull();

        //Put em in the DB
        this.audBuyers = this.audBuyers.stream().map(t -> {
            return traderService.save(t).get();
        }).collect(Collectors.toList());
        assertThat(this.audBuyers).isNotNull();

        TraderGroupUtil audBuyerUtil = null;
        //Test the Util Group
        //Create a Buyers Trader Group
        try {
            audBuyerUtil = new TraderGroupUtil(this.audBuyers);
        } catch (Exception e) {
            this.log.error("Couldn't Create AUD Util Group", e);
            fail(e.getMessage());
        }

        long audBuyerSelling = 0;
        long audBuyerBuying = 0;
        for (int i = 0; i < this.audBuyers.size(); i++) {
            audBuyerSelling =
                audBuyerSelling + this.audBuyers.get(i).getCurrencyToSell().getValue();
            audBuyerBuying = audBuyerBuying + this.audBuyers.get(i).getCurrencyToBuy().getValue();
        }

        //Check the values of the group
        assertThat(audBuyerUtil.getCurrencyToBuy().getValue()).isEqualTo(
            audBuyerBuying); //To check if we have other traders in DB
        assertThat(audBuyerUtil.getCurrencyToSell().getValue()).isEqualTo(
            audBuyerSelling); //Just to confirm that the util group works

        //GENERATE THE USD BUYERS
        assertThat(this.usdBuyers).isNotNull();

        //Put em in the DB
        this.usdBuyers.stream().map(t -> {
            return traderService.save(t).get();
        }).collect(Collectors.toList());
        assertThat(this.usdBuyers).isNotNull();

        //Test the Util Groups ability to sum values
        TraderGroupUtil usdBuyerUtil = null;

        try {
            usdBuyerUtil = new TraderGroupUtil(this.usdBuyers);
        } catch (Exception e) {
            this.log.error("Couldn't Create USD Util Group", e);
            fail(e.getMessage());
        }

        long usdBuyerSelling = 0;
        long usdBuyerBuying = 0;
        for (int i = 0; i < this.usdBuyers.size(); i++) {
            usdBuyerSelling =
                usdBuyerSelling + this.usdBuyers.get(i).getCurrencyToSell().getValue();
            usdBuyerBuying = usdBuyerBuying + this.usdBuyers.get(i).getCurrencyToBuy().getValue();
        }

        //Check the values of the group
        assertThat(usdBuyerUtil.getCurrencyToBuy().getValue()).isEqualTo(usdBuyerBuying);
        assertThat(usdBuyerUtil.getCurrencyToSell().getValue()).isEqualTo(usdBuyerSelling);
    }

    /**
     * Test the creation of a trade using the traders we inserted above, this assumes that the DB
     * was empty before we started the test.
     */
    public void testCreateTrade() {

        this.log.info("Running Test Create Trade");
        PyCurrencyType usdType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.USD);
        PyCurrencyType audType = currencyTypeService.getCurrentCurrencyType(CurrencyCodeEnum.AUD);

        assertThat(audType).isNotNull();
        assertThat(usdType).isNotNull();

        //Load in the trades from the DB
        List<Trader> audBuyers = traderService.getFreeTraders(audType.getCode(), usdType.getCode())
            .toList();
        List<Trader> usdBuyers = traderService.getFreeTraders(usdType.getCode(), audType.getCode())
            .toList();

        assertThat(audBuyers).isNotNull();
        assertThat(usdBuyers).isNotNull();

        TraderGroupUtil audBuyerUtil = null;
        TraderGroupUtil usdBuyerUtil = null;
        //Create a Buyers Trader Group
        try {
            audBuyerUtil = new TraderGroupUtil(audBuyers);
        } catch (Exception e) {
            this.log.error("Couldn't Create AUD Util Group", e);
            fail(e.getMessage());
        }

        try {
            usdBuyerUtil = new TraderGroupUtil(usdBuyers);
        } catch (Exception e) {
            this.log.error("Couldn't Create USD Util Group", e);
            fail(e.getMessage());
        }

        TraderGroup audBuyerGroup = new TraderGroup(audBuyerUtil.getCurrencyToSell(),
            audBuyerUtil.getCurrencyToBuy(), audBuyers);

        assertThat(audBuyerGroup).isNotNull();

        TraderGroup usdBuyerGroup = new TraderGroup(usdBuyerUtil.getCurrencyToSell(),
            usdBuyerUtil.getCurrencyToBuy(), usdBuyers);
        assertThat(usdBuyerGroup).isNotNull();

        audBuyerGroup = traderGroupService.save(audBuyerGroup).get();
        usdBuyerGroup = traderGroupService.save(usdBuyerGroup).get();

        //Load traders again and check their group IDs
        List<Trader> audBuyersInGroup = traderService.getWithGroupId(audBuyerGroup.getId())
            .toList();
        assertThat(audBuyersInGroup).isNotNull();

        List<Trader> usdBuyersInGroup = traderService.getWithGroupId(usdBuyerGroup.getId())
            .toList();
        assertThat(usdBuyersInGroup).isNotNull();

        Trade newTrade = new Trade(usdBuyerGroup, audBuyerGroup, Calendar.getInstance(),
            TradeStatus.DEPOSIT);

        newTrade = tradeService.save(newTrade).get();
        assertThat(newTrade.getId()).isNotNull();
    }

    /**
     * Test the simulation of depositing funds into the account
     */
    public void testTraderDepositFunds() {

        //First Collect all Traders that are ready for deposit
        List<Trader> traders = traderService.getSelling(TraderStatus.DEPOSIT,
            CurrencyCodeEnum.AUD).toList();
        assertThat(traders).isNotNull();
        assertThat(traders.size()).isGreaterThan(0);

        try {
            audTraderDepositSimulator.simulateDeposits(traders);
        } catch (Exception e) {
            this.log.error("Unable to simulate deposits.", e);
            fail(e.getMessage());
        }
    }

    /**
     * Test to see if the bank will read in the deposit listing from the previous test and generate
     * an NAI file describing them.
     */
    public void testBankCreditFunds() {
        try {
            audBankSimulator.checkAndProcessNewFile();
        } catch (Exception e1) {
            this.log.error("Unable to process new file.", e1);
            fail(e1.getMessage());
        }
        this.log.info(audBankSimulator.getAccountBalance());

        //Total up the AUD being sold
        PyCurrency aud = this.usdBuyers.get(0).getCurrencyToSell();

        try {
            for (int i = 1; i < this.usdBuyers.size(); i++) {
                aud = aud.add(this.usdBuyers.get(i).getCurrencyToSell());
            }
        } catch (Exception e) {
            this.log.error("Unable to test bank credits.", e);
            fail(e.getMessage());

        }

        //Round as in the Bank
        aud = PyCurrencyUtil.roundCurrency(aud);

        try {
            audBankSimulator.outputNaiFile();
        } catch (Exception e) {
            this.log.error("AUD Bank unable to output NAI File.", e);
            fail(e.getMessage());
        }

        assertThat(audBankSimulator.getAccountBalance()).isNotNull();
        assertThat(aud.getValue()).isEqualTo(audBankSimulator.getAccountBalance());
    }

    public void testAudTradeCreditManagerProcessFiles() {
        this.log.info("Running Test Aud Trade Credit Manager Process Files");
        try {
            audTradeCreditManager.processNewFiles();
        } catch (Exception e) {
            this.log.error("Failed Processing Trade Credit File.", e);
            fail(e.getMessage());
        }
    }

    public void testAudTradeCreditManagerFinalizeTrade() {
        this.log.info("Running Test Aud Trade Credit Manager Finalize Trade");
        audTradeCreditManager.processOpenTrades();

        //Now check to see if we have a finalized trade in db
        List<Trade> trades = tradeService.getAllWithStatus(TradeStatus.CLOSED).toList();
        assertThat(trades).isNotNull();
        assertThat(trades.size()).isGreaterThan(0);
    }

    public void testAudTradeDebitManagerProcessDebits() {
        try {
            audTradeDebitManager.processTradeDebits();
        } catch (Exception e) {
            this.log.error("Unable to process trade debits.", e);
            fail(e.getMessage());
        }


    }

    public void testAudBankDeposits() {
        try {
            audBankSimulator.checkAndProcessNewFile();
        } catch (Exception e) {
            this.log.error("Unable to Test Aud Bank Deposits.", e);
            fail(e.getMessage());
        }

        //Total up what we should see in the account
        try {
            //Start with 0
            PyCurrency aud = this.audBuyers.get(0).getCurrencyToBuy()
                .minus(this.audBuyers.get(0).getCurrencyToBuy());

            for (int i = 0; i < this.audBuyers.size(); i++) {
                aud = aud.minus(this.audBuyers.get(i).getCurrencyToBuy());
            }

            for (int i = 0; i < this.usdBuyers.size(); i++) {
                aud = aud.add(this.usdBuyers.get(i).getCurrencyToSell());
            }

            aud = PyCurrencyUtil.roundCurrency(aud); //Round it to compare with bank

            PyCurrency accountBalance = audBankSimulator.getAccountBalance();
            assertThat(accountBalance).isNotNull();
            assertThat(accountBalance.getValue()).isEqualTo(aud.getValue());
        } catch (Exception e) {
            this.log.error("Unable to Test Aud Bank Deposits.", e);
            fail(e.getMessage());
        }
    }


    public void testTotalTrade() {
        try {
            audBankSimulator.checkAndProcessNewFile();
        } catch (Exception e) {
            this.log.error("Unable to Test Aud Bank Deposits.", e);
            fail(e.getMessage());
        }

        //Total up what we should see in the account
        PyCurrency aud = this.usdBuyers.get(0).getCurrencyToSell();
        try {
            for (int i = 1; i < this.usdBuyers.size(); i++) {
                aud = aud.add(this.usdBuyers.get(i).getCurrencyToSell());
            }

            for (int i = 0; i < this.audBuyers.size(); i++) {
                aud = aud.minus(this.audBuyers.get(i).getCurrencyToBuy());
            }

        } catch (Exception e) {
            this.log.error("Unable to Test Aud Bank Deposits.", e);
            fail(e.getMessage());
        }
    }
}
