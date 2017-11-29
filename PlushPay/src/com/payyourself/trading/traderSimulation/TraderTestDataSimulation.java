package com.payyourself.trading.traderSimulation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.currency.CurrencyConverter;
import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.PyCurrencyUtil;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.rate.RateTestDataCollector;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;
import com.payyourself.trading.beneficiary.tradeBeneficiary.TradeBeneficiary;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;
import com.payyourself.trading.trader.TraderStatus;
import com.payyourself.userManagement.user.User;
import com.payyourself.userManagement.user.UserHibernation;

public class TraderTestDataSimulation extends Thread {

	private long pollPeriod; // in ms
	private CurrencyConverter conv;
	private volatile boolean shutdown;
	private TraderTestData data;
	private int dataPos;

	private Logger log;

	private static TraderTestDataSimulation singleton;

	public static TraderTestDataSimulation getTraderTestDataSimulation() {
		if (TraderTestDataSimulation.singleton == null)
			// it's ok, we can call this constructor
			TraderTestDataSimulation.singleton = new TraderTestDataSimulation();
		return singleton;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
		// that'll teach 'em
	}

	//TODO Figure out how to do this via log4j.xml
	private TraderTestDataSimulation() {
		super("Trader Test Data Simulation");
		this.log = LogfileFactory.getHTMLLogger(Level.ERROR,this.getClass());
		this.conv = new CurrencyConverter();
		this.pollPeriod = 5000;
		this.data = new TraderTestData();

	}

	public void run() {

		URL url = RateTestDataCollector.class
				.getResource("/com/payyourself/testing/data/testTradeAmounts2.csv");
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
				if (this.shutdown)
					break;
				else {
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
	 * 
	 * This was done as a fn so it can be tested.
	 * 
	 * @throws Exception
	 */
	public void insertNewTraders() throws Exception {

		Session sesh = HibernateUtil.getSessionFactory().getCurrentSession();
		sesh.beginTransaction();

		/* This trader needs to be added to DB, but make sure we don't add twice */
		TraderHibernation th = new TraderHibernation();

		UserHibernation uh = new UserHibernation();
		List<User> users = uh.loadAllUsers();

		PyCurrencyTypeHibernation pycth;

		// TODO See: http://www.javapractices.com/topic/TopicAction.do?Id=13

		boolean currencyType = true;
		long toBuy;

		List<PyCurrency> toBuyList;
		List<Trader> traders;

		// Have to do this
		sesh = HibernateUtil.getSessionFactory().getCurrentSession();
		sesh.beginTransaction();

		pycth = new PyCurrencyTypeHibernation();
		// Load a rate change if necessary
		PyCurrencyType usdType = pycth.getCurrentUsd();
		PyCurrencyType audType = pycth.getCurrentAud();

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
				traders.set(j, th.merge(traders.get(j)));

				this.log.debug("Added "
						+ traders.get(j).getUser().getUsername()
						+ " buying "
						+ conv.getAsString(null, null, traders.get(j)
								.getCurrencyToBuy()) + " Type id " + traders.get(j).getCurrencyToBuy().getType().getId()
						+ " selling "
						+ conv.getAsString(null, null, traders.get(j)
								.getCurrencyToSell()) + " Type id " + traders.get(j).getCurrencyToSell().getType().getId());

			}
			sesh.flush();

		}

		sesh.close();
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

		PyCurrencyTypeHibernation pycth = new PyCurrencyTypeHibernation();
		// Load a rate change if necessary

		PyCurrencyType usdType = pycth.getCurrentUsd();
		PyCurrencyType audType = pycth.getCurrentAud();

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
						.getValue(), toBuy.get(i).getType(), audType); // audUtil.toPyCurrencyFromBaseValue(toBuy.get(i).getBaseValue());

				// Create Beneficiaries
				for (int j = 0; j < user.getBeneficiaries().size(); j++) {
					if (user.getBeneficiaries().get(j).getType() == toBuy
							.get(i).getType().getCode()) {
						/* Create ONE beneficiary and provide the amount */
						TradeBeneficiary newBene = new TradeBeneficiary();
						newBene.setAmount(new PyCurrency(toBuy.get(i).getValue(),toBuy.get(i).getType()));
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
						TraderStatus.CONFIRMED.name(), benies);
				traders.add(trader); // Insert him to be persisted
				// TODO Upgrade ENUM Types using com.payyourself.enumUserType
			} else if (toBuy.get(i).getType().getCode() == CurrencyCodeEnum.AUD) {
				// Buying AUD
				currencyToSell = PyCurrencyUtil.createCurrency(toBuy.get(i)
						.getValue(), toBuy.get(i).getType(), usdType); // usdUtil.toPyCurrencyFromBaseValue(toBuy.get(i).getBaseValue());

				// Create Beneficiaries
				for (int j = 0; j < user.getBeneficiaries().size(); j++) {
					if (user.getBeneficiaries().get(j).getType() == toBuy
							.get(i).getType().getCode()) {
						/* Create ONE beneficiary and provide the amount */
						TradeBeneficiary newBene = new TradeBeneficiary();
						newBene.setAmount(new PyCurrency(toBuy.get(i).getValue(),toBuy.get(i).getType()));
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
						TraderStatus.CONFIRMED.name(), benies);
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
