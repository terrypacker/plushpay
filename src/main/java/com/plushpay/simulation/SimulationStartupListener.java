package com.plushpay.simulation;

import com.plushpay.service.banking.au.AudBankSimulator;
import com.plushpay.service.banking.au.AudTradeCreditManager;
import com.plushpay.service.banking.au.AudTradeDebitManager;
import com.plushpay.service.banking.au.AudTraderDepositSimulator;
import com.plushpay.service.currency.rate.RateTestDataCollector;
import com.plushpay.service.trading.evolution.GeneticTradeManager;
import com.plushpay.service.trading.tradeProfitTree.TradeProfitTreeManager;
import com.plushpay.service.trading.traderSimulation.TraderTestDataSimulation;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Simulation Control
 *
 * TODO Create beans for all these classes
 *
 */
@ConditionalOnProperty(value = "com.plushpay.simulation.enabled", havingValue = "true")
@Component
public class SimulationStartupListener {

	public SimulationStartupListener() {
		RateTestDataCollector.getRateTestDataCollector().startUp();

		AudBankSimulator.getAudBankSimulator().startUp();

		AudTradeCreditManager.getAudTradeCreditManager().startUp();

		AudTradeDebitManager.getAudTradeDebitManager().startUp();

		AudTraderDepositSimulator.getAudTraderDeptositSimulator().startUp();

		//TradeProfitTreeManager.getTradeProfitTreeManager().startUp();
		GeneticTradeManager.getGeneticTradeManager().startUp();

		//TraderTestDataSimulation.getTraderTestDataSimulation().startUp();
	}

	@PreDestroy
	public void destroy() {
		RateTestDataCollector.getRateTestDataCollector().shutDown();
		AudBankSimulator.getAudBankSimulator().shutDown();
		AudTradeCreditManager.getAudTradeCreditManager().shutDown();
		AudTradeDebitManager.getAudTradeDebitManager().shutDown();
		AudTraderDepositSimulator.getAudTraderDeptositSimulator().shutDown();
		TradeProfitTreeManager.getTradeProfitTreeManager().shutDown();
		TraderTestDataSimulation.getTraderTestDataSimulation().shutDown();
	}
}
