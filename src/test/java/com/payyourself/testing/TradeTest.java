package com.payyourself.testing;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cactus.ServletTestCase;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.banking.au.AudBankSimulator;
import com.payyourself.banking.au.AudTradeCreditManager;
import com.payyourself.banking.au.AudTradeDebitManager;
import com.payyourself.banking.au.AudTraderDepositSimulator;
import com.payyourself.currency.CurrencyConverter;
import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.PyCurrencyUtil;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;
import com.payyourself.trading.trade.Trade;
import com.payyourself.trading.trade.TradeHibernation;
import com.payyourself.trading.trade.TradeStatus;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;
import com.payyourself.trading.trader.TraderStatus;
import com.payyourself.trading.trader.group.TraderGroup;
import com.payyourself.trading.trader.group.TraderGroupHibernation;
import com.payyourself.trading.trader.group.TraderGroupUtil;
import com.payyourself.trading.traderSimulation.TradersSimulation;
import com.payyourself.userManagement.user.User;
import com.payyourself.userManagement.user.UserHibernation;

public class TradeTest extends ServletTestCase {

	private Logger log;
	private Session sesh;
	

	//List of amounts to Buy
	private List<PyCurrency> audToBuy;
	private List<PyCurrency> usdToBuy;
	
	//List of traders
	private List<Trader> audBuyers;
	private List<Trader> usdBuyers;
	
	//Currency Converter As Used on web
	private CurrencyConverter conv;

	
	public TradeTest(){
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		
		this.conv = new CurrencyConverter();
		
		List<Long> audAmounts = new ArrayList<Long>();
		audAmounts.add(1000000L);
		audAmounts.add(2000000L);

		//Changing these will result in a failed test due to the way we extract rates from DB during testing
		List<PyCurrencyType> audRates = new ArrayList<PyCurrencyType>();
		audRates.add(new PyCurrencyType(CurrencyCodeEnum.AUD,9200L,"$",Calendar.getInstance()));
		audRates.add(new PyCurrencyType(CurrencyCodeEnum.AUD,9200L,"$",Calendar.getInstance()));
		//audRates.add(9220L);
		//audRates.add(9230L);
		//audRates.add(9240L);
	//	audRates.add(9250L);
		//audRates.add(9260L);
		//audRates.add(9270L);
		//audRates.add(9280L);
		//audRates.add(9290L);
		
		try {
			this.audToBuy = this.generateAudCurrency(audAmounts, audRates);
		} catch (Exception e) {
			this.log.error("Unable to setup currencies for test",e);
			fail();
		}
		
		this.audBuyers = this.generateTraders(this.audToBuy);
		
		List<Long> usdAmounts = new ArrayList<Long>();
		usdAmounts.add(3000000L);
		usdAmounts.add(4000000L);
		
		List<PyCurrencyType> usdRates = new ArrayList<PyCurrencyType>();
		usdRates.add(new PyCurrencyType(CurrencyCodeEnum.USD,10000L,"$",Calendar.getInstance()));
		usdRates.add(new PyCurrencyType(CurrencyCodeEnum.USD, 10000L, "$",Calendar.getInstance()));
		
		try {
			this.usdToBuy = this.generateUsdCurrency(usdAmounts, usdRates);
		} catch (Exception e) {
			this.log.error("Unable to setup currencies for test",e);
			fail();
		}
		
		this.usdBuyers = this.generateTraders(this.usdToBuy);



		
		
		

	}
	
	private List<Trader> generateTraders(List<PyCurrency> toBuy) {
		
		this.sesh = HibernateUtil.getSessionFactory().getCurrentSession();
		this.sesh.beginTransaction();
		
		//Get the User data from the DB.
		UserHibernation uh = new UserHibernation();
		List<User> users = uh.loadAllUsers();
		assertNotNull(users);
		
		//Create a Traders Simulation to help generate Traders
		TradersSimulation traderSim = TradersSimulation.getTradersSimulation();
		List<Trader> traders = null;
		try {
			traders = traderSim.createNewTraders(users.get(0), toBuy);
		} catch (Exception e) {
			this.log.error("Unable to generate AUD Buyers",e);
			fail();
		}
		
		this.sesh.close();
		
		return traders;
		
	}

	private List<PyCurrency> generateUsdCurrency(List<Long> amounts, List<PyCurrencyType> rates) throws Exception{
		
		if(amounts.size() != rates.size())
			throw new Exception("amounts.size() doesn't match rates.size()");
		
		List<PyCurrency> usds = new ArrayList<PyCurrency>();
		
		for(int i=0; i<amounts.size(); i++){
			usds.add(new PyCurrency(amounts.get(i), rates.get(i)));
		}
		
		return usds;
		
	}

	private List<PyCurrency> generateAudCurrency(List<Long> amounts, List<PyCurrencyType> rates) throws Exception{

		if(amounts.size() != rates.size())
			throw new Exception("amounts.size() doesn't match rates.size()");
		
		List<PyCurrency> auds = new ArrayList<PyCurrency>();
		
		for(int i=0; i<amounts.size(); i++){
			auds.add(new PyCurrency(amounts.get(i), rates.get(i)));
		}
		
		return auds;
	}

	
	public void setUp(){
		this.sesh = HibernateUtil.getSessionFactory().getCurrentSession();
		this.sesh.beginTransaction();
	}
	
	public void tearDown(){
		this.sesh.flush();
		this.sesh.disconnect();
		this.sesh.close();
	}
	
	
	
	public void testInsertRate(){
		
		this.log.info("Running Test Insert Rate");
		
		//Must have a session
		assertNotNull(this.sesh);
		
		PyCurrencyTypeHibernation pycth= new PyCurrencyTypeHibernation();
		
		//Load the Types from the DB

		PyCurrencyType aud = pycth.getCurrentAud();
		PyCurrencyType usd = pycth.getCurrentUsd();
		
		
		//Load the rates from a file
		//TODO load rates from test file



		this.log.info("Setting " + aud.getCode() + " to " + 9200 );
		aud.setRateToBase(9200); //

		this.log.info("Setting " + usd.getCode() + " to " + 10000);
		usd.setRateToBase(10000); //


		//Persist them to the DB
		pycth.persist(aud);
		pycth.persist(usd);
	
		
	}

	public void testCurrencyUtil(){
		
		this.log.info("Running Test Currency Util");
		
		//Get the types from the DB
		PyCurrencyTypeHibernation pycth= new PyCurrencyTypeHibernation();
		
		//Load the Types from the DB
		PyCurrencyType audType = pycth.getCurrentAud();
		PyCurrencyType usdType = pycth.getCurrentUsd();
		
		//First test that it works for 1 dollar
		//long audBase = (this.types.get(1).getRateToBase() *this.types.get(1).getRateToBase())/this.types.get(0).getRateToBase();
		PyCurrency testUsd = new PyCurrency(10000,usdType);
		PyCurrency testAud = new PyCurrency(10869, audType); //1 AUD
		
		assertEquals(testAud,PyCurrencyUtil.createCurrency(testUsd.getValue(), testUsd.getType(), audType)); //.toPyCurrencyFromValue(10000),testAud); //10000 = 10000
		
	}
	
	/**
	 * Create a new user and insert 
	 * into DB
	 */
	public void testInsertUsers(){
		this.log.info("Running Test Insert Users");
	}
	
	
	/**
	 * This test assumes that the default test data is in the DB
	 */
	public void testInsertTraders(){

		this.log.info("Running Test Insert Traders");


		//Must have a session
		assertNotNull(this.sesh);
		
		//Get the types from the DB
		PyCurrencyTypeHibernation pycth= new PyCurrencyTypeHibernation();
		
		TraderHibernation th = new TraderHibernation();


		//GENERATE THE AUD BUYERS
		assertNotNull(this.audBuyers);
		
		//Put em in the DB
		this.audBuyers = th.merge(this.audBuyers);
		assertNotNull(this.audBuyers);
		
		
		for(int i=0; i<this.audBuyers.size(); i++){
			String buying = this.conv.getAsString(null, null,this.audBuyers.get(i).getCurrencyToBuy());
			String selling = this.conv.getAsString(null, null, this.audBuyers.get(i).getCurrencyToSell());
			this.log.info("Inserted AUD Buyer with id: " + this.audBuyers.get(i).getTraderid() + 
					" buying " + buying + " selling " + selling);
			
		}
		
		TraderGroupUtil audBuyerUtil = null;
		//Test the Util Group
		//Create a Buyers Trader Group
		try {
			audBuyerUtil = new TraderGroupUtil(this.audBuyers);
		} catch (Exception e) {
			this.log.error("Couldn't Create AUD Util Group",e);
			fail();
		}
		
		long audBuyerSelling = 0;
		long audBuyerBuying = 0;
		for(int i=0; i<this.audBuyers.size(); i++){
			audBuyerSelling = audBuyerSelling + this.audBuyers.get(i).getCurrencyToSell().getValue();
			audBuyerBuying = audBuyerBuying + this.audBuyers.get(i).getCurrencyToBuy().getValue();
		}
	
		//Check the values of the group
		assertEquals(audBuyerUtil.getCurrencyToBuy().getValue(),audBuyerBuying); //To check if we have other traders in DB
		assertEquals(audBuyerUtil.getCurrencyToSell().getValue(),audBuyerSelling); //Just to confirm that the util group works

		
		
		
		//GENERATE THE USD BUYERS
		assertNotNull(this.usdBuyers);
		
		//Put em in the DB
		this.usdBuyers = th.merge(this.usdBuyers);
		assertNotNull(this.usdBuyers);
		
		for(int i=0; i<this.usdBuyers.size(); i++){
			String buying = this.conv.getAsString(null, null,this.usdBuyers.get(i).getCurrencyToBuy());
			String selling = this.conv.getAsString(null, null, this.usdBuyers.get(i).getCurrencyToSell());
			this.log.info("Inserted USD buyer with id: " + this.usdBuyers.get(i).getTraderid() + 
					" buying " + buying + " selling " + selling);
			
		}
		
		//Test the Util Groups ability to sum values
		TraderGroupUtil usdBuyerUtil = null;
		
		try {
			usdBuyerUtil = new TraderGroupUtil(this.usdBuyers);
		} catch (Exception e) {
			this.log.error("Couldn't Create USD Util Group",e);
			fail();
		}
		
		long usdBuyerSelling = 0;
		long usdBuyerBuying = 0;
		for(int i=0; i<this.usdBuyers.size(); i++){
			usdBuyerSelling = usdBuyerSelling + this.usdBuyers.get(i).getCurrencyToSell().getValue();
			usdBuyerBuying = usdBuyerBuying + this.usdBuyers.get(i).getCurrencyToBuy().getValue();
		}
		
		//Check the values of the group
		assertEquals(usdBuyerUtil.getCurrencyToBuy().getValue(),usdBuyerBuying);
		assertEquals(usdBuyerUtil.getCurrencyToSell().getValue(),usdBuyerSelling);		
		
		
	}
	/**
	 * Test the creation of a trade using the traders we inserted above,
	 * this assumes that the DB was empty before we started the test.
	 */
	public void testCreateTrade(){
		
		this.log.info("Running Test Create Trade");
		//Get the currency Types
		//Get the types from the DB
		PyCurrencyTypeHibernation pycth= new PyCurrencyTypeHibernation();
		
		//Load the Types from the DB
		PyCurrencyType audType = pycth.getCurrentAud();
		PyCurrencyType usdType = pycth.getCurrentUsd();
				
		assertNotNull(audType);
		assertNotNull(usdType);
		
		//Load in the trades from the DB
		TraderHibernation th = new TraderHibernation();
		List<Trader> audBuyers = th.loadFreeTraders(audType,usdType);
		List<Trader> usdBuyers = th.loadFreeTraders(usdType,audType);
		
		
		assertNotNull(audBuyers);
		assertNotNull(usdBuyers);
		TraderGroupUtil audBuyerUtil = null;
		TraderGroupUtil usdBuyerUtil = null;
		//Create a Buyers Trader Group
		try {
			audBuyerUtil = new TraderGroupUtil(audBuyers);
		} catch (Exception e) {
			this.log.error("Couldn't Create AUD Util Group",e);
			fail();
		}
		
		try {
			usdBuyerUtil = new TraderGroupUtil(usdBuyers);
		} catch (Exception e) {
			this.log.error("Couldn't Create USD Util Group",e);
			fail();
		}
		
		TraderGroup audBuyerGroup = new TraderGroup(audBuyerUtil.getCurrencyToSell(),audBuyerUtil.getCurrencyToBuy(),audBuyers);
		
		assertNotNull(audBuyerGroup);

		
		

		
		TraderGroup usdBuyerGroup = new TraderGroup(usdBuyerUtil.getCurrencyToSell(),usdBuyerUtil.getCurrencyToBuy(),usdBuyers);
		assertNotNull(usdBuyerGroup);

		

		TraderGroupHibernation tgh = new TraderGroupHibernation();

		audBuyerGroup = tgh.mergeAndUpdateMembers(audBuyerGroup);
		
		usdBuyerGroup = tgh.mergeAndUpdateMembers(usdBuyerGroup);

		String buying = this.conv.getAsString(null, null, audBuyerGroup.getCurrencyToBuy());
		String selling = this.conv.getAsString(null,null, audBuyerGroup.getCurrencyToSell());

		this.log.info("Created AUD Buying Group: " + audBuyerGroup.getGroupid() + " Buying " + buying + " Selling " + selling);
		
		buying = this.conv.getAsString(null, null, usdBuyerGroup.getCurrencyToBuy());
		selling = this.conv.getAsString(null,null, usdBuyerGroup.getCurrencyToSell());
		this.log.info("Created USD Buying Group: " + usdBuyerGroup.getGroupid() + " Buying " + buying + " Selling " + selling);
		
		
		//Load traders again and check their group IDs
		List<Trader> traders = th.loadTradersWithGroupId(audBuyerGroup.getGroupid());
		assertNotNull(traders);
		
		traders = th.loadTradersWithGroupId(usdBuyerGroup.getGroupid());
		
		
		Trade newTrade = new Trade(usdBuyerGroup,audBuyerGroup,Calendar.getInstance(),TradeStatus.DEPOSIT.name());
		
		TradeHibernation tradeHibernation = new TradeHibernation();
		newTrade = tradeHibernation.merge(newTrade);
		
		assertNotNull(newTrade);
		

		this.log.info("Created Trade with ID: " + newTrade.getTradeId());
		
		
	}
	
	/**
	 * Test the simulation of depositing funds into the account
	 */
	public void testTraderDepositFunds(){
		
		//First Collect all Traders that are ready for deposit
		AudTraderDepositSimulator sim = AudTraderDepositSimulator.getAudTraderDeptositSimulator();
		
		TraderHibernation th = new TraderHibernation();
		
		//Get the traders to compare to
		List<Trader> traders = th.loadTradersSelling(new ArrayList<Long>(), TraderStatus.DEPOSIT, CurrencyCodeEnum.AUD);
	
		TradeTest.assertNotSame(0,traders.size());
		
		try {
			sim.simulateDeposits(traders);
		} catch (Exception e) {
			this.log.error("Unable to simulate deposits.",e);
			fail();
		}
	}

	/**
	 * Test to see if the bank will read in the deposit listing
	 * from the previous test and generate an NAI file
	 * describing them.
	 */
	public void testBankCreditFunds(){
		
		AudBankSimulator sim = AudBankSimulator.getAudBankSimulator();
		try {
			sim.checkAndProcessNewFile();
		} catch (Exception e1) {
			this.log.error("Unable to process new file." ,e1);
			fail();
		}
		this.log.info(sim.getAccountBalance());
		
		//Total up the AUD being sold
		PyCurrency aud = this.usdBuyers.get(0).getCurrencyToSell();

		try {
			for(int i=1; i<this.usdBuyers.size(); i++){
				aud = aud.add(this.usdBuyers.get(i).getCurrencyToSell());
			}
		} catch (Exception e) {
			this.log.error("Unable to test bank credits.",e);
			fail();

		}

		//Round as in the Bank
		aud = PyCurrencyUtil.roundCurrency(aud);
		
		
		try {
			sim.outputNaiFile();
		} catch (Exception e) {
			this.log.error("AUD Bank unable to output NAI File.",e);
			fail();
		}

		assertEquals(this.conv.getAsString(null, null, aud),sim.getAccountBalance());
		
	}
	
	public void testAudTradeCreditManagerProcessFiles(){
		this.log.info("Running Test Aud Trade Credit Manager Process Files");
		AudTradeCreditManager man = AudTradeCreditManager.getAudTradeCreditManager();
		try {
			man.processNewFiles();
		} catch (Exception e) {
			this.log.error("Failed Processing Trade Credit File.",e);
			fail();
		}
	}
	
	public void testAudTradeCreditManagerFinalizeTrade(){
		this.log.info("Running Test Aud Trade Credit Manager Finalize Trade");
		AudTradeCreditManager man = AudTradeCreditManager.getAudTradeCreditManager();
		man.processOpenTrades();
		
		//Now check to see if we have a finalized trade in db
		TradeHibernation th = new TradeHibernation();
		List<Trade> trades = th.loadAllWithStatus(TradeStatus.CLOSED);
		
		assertNotSame(0,trades.size());
		
	}
	
	public void testAudTradeDebitManagerProcessDebits(){
		AudTradeDebitManager man = AudTradeDebitManager.getAudTradeDebitManager();
		
		try {
			man.processTradeDebits();
		} catch (Exception e) {
			this.log.error("Unable to process trade debits.",e);
			fail();
		}
		
		
		
	}
	
	public void testAudBankDeposits(){
		AudBankSimulator sim = AudBankSimulator.getAudBankSimulator();
		try {
			sim.checkAndProcessNewFile();
		} catch (Exception e) {
			this.log.error("Unable to Test Aud Bank Deposits.",e);
			fail();
		}
		
		//Total up what we should see in the account
		try {
			//Start with 0
			PyCurrency aud = this.audBuyers.get(0).getCurrencyToBuy().minus(this.audBuyers.get(0).getCurrencyToBuy());

			for(int i=0; i<this.audBuyers.size(); i++){
				aud = aud.minus(this.audBuyers.get(i).getCurrencyToBuy());
			}
			
			
			
			for(int i=0; i<this.usdBuyers.size(); i++){
				aud = aud.add(this.usdBuyers.get(i).getCurrencyToSell());
			}
			
			aud = PyCurrencyUtil.roundCurrency(aud); //Round it to compare with bank
			
			String accountBalance = sim.getAccountBalance();
			String total = this.conv.getAsString(null, null, aud);
			assertEquals(accountBalance,total);
		} catch (Exception e) {
			this.log.error("Unable to Test Aud Bank Deposits.",e);
			fail();
		}

		
		
		
	}
	
	
	public void testTotalTrade(){
		AudBankSimulator sim = AudBankSimulator.getAudBankSimulator();
		try {
			sim.checkAndProcessNewFile();
		} catch (Exception e) {
			this.log.error("Unable to Test Aud Bank Deposits.",e);
			fail();
		}
		
		//Total up what we should see in the account
		PyCurrency aud = this.usdBuyers.get(0).getCurrencyToSell();
		try {
			for(int i=1; i<this.usdBuyers.size(); i++){
				aud = aud.add(this.usdBuyers.get(i).getCurrencyToSell());
			}
			
			for(int i=0; i<this.audBuyers.size(); i++){
				aud = aud.minus(this.audBuyers.get(i).getCurrencyToBuy());
			}
			
		} catch (Exception e) {
			this.log.error("Unable to Test Aud Bank Deposits.",e);
			fail();
		}

	}
	
	
}
