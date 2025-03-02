package com.plushpay.simulation;

import com.plushpay.service.banking.au.AudBankSimulator;
import com.plushpay.service.banking.au.AudTradeCreditManager;
import com.plushpay.service.banking.au.AudTradeDebitManager;
import com.plushpay.service.banking.au.AudTraderDepositSimulator;
import com.plushpay.service.banking.us.UsdBankSimulator;
import com.plushpay.service.banking.us.UsdTradeCreditManager;
import com.plushpay.service.banking.us.UsdTradeDebitManager;
import com.plushpay.service.banking.us.UsdTraderDepositSimulator;
import com.plushpay.service.trading.evolution.EvolutionTradeManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Simulation Control
 * <p>
 * TODO Create beans for all these classes
 */
@ConditionalOnProperty(value = "com.plushpay.simulation.enabled", havingValue = "true")
@Component
public class SimulationStartupListener extends Thread {

    private final AudBankSimulator audBankSimulator;
    private final AudTradeCreditManager audTradeCreditManager;
    private final AudTradeDebitManager audTradeDebitManager;
    private final AudTraderDepositSimulator audTraderDepositSimulator;

    private final UsdBankSimulator usdBankSimulator;
    private final UsdTradeCreditManager usdTradeCreditManager;
    private final UsdTradeDebitManager usdTradeDebitManager;
    private final UsdTraderDepositSimulator usdTraderDepositSimulator;

    private final EvolutionTradeManager tradeManager;

    public SimulationStartupListener(AudBankSimulator audBankSimulator,
        AudTradeCreditManager audTradeCreditManager,
        AudTradeDebitManager audTradeDebitManager,
        AudTraderDepositSimulator audTraderDepositSimulator,
        UsdBankSimulator usdBankSimulator,
        UsdTradeCreditManager usdTradeCreditManager,
        UsdTradeDebitManager usdTradeDebitManager,
        UsdTraderDepositSimulator usdTraderDepositSimulator,
        EvolutionTradeManager tradeManager) {

        this.audBankSimulator = audBankSimulator;
        this.audTradeCreditManager = audTradeCreditManager;
        this.audTradeDebitManager = audTradeDebitManager;
        this.audTraderDepositSimulator = audTraderDepositSimulator;

        this.usdBankSimulator = usdBankSimulator;
        this.usdTradeCreditManager = usdTradeCreditManager;
        this.usdTradeDebitManager = usdTradeDebitManager;
        this.usdTraderDepositSimulator = usdTraderDepositSimulator;

        this.tradeManager = tradeManager;

    }

    @PostConstruct
    public void init() {
        audBankSimulator.startUp();
        audTradeCreditManager.startUp();
        audTradeDebitManager.startUp();
        audTraderDepositSimulator.startUp();

        usdBankSimulator.startUp();
        usdTradeCreditManager.startUp();
        usdTradeDebitManager.startUp();
        usdTraderDepositSimulator.startUp();

        //TradeProfitTreeManager.getTradeProfitTreeManager().startUp();
        tradeManager.startUp();

        //TraderTestDataSimulation.getTraderTestDataSimulation().startUp();
    }

    @PreDestroy
    public void destroy() {
        //RateTestDataCollector.getRateTestDataCollector().shutDown();
        audBankSimulator.shutDown();
        audTradeCreditManager.shutDown();
        audTradeDebitManager.shutDown();
        audTraderDepositSimulator.shutDown();

        usdBankSimulator.shutDown();
        usdTradeCreditManager.shutDown();
        usdTradeDebitManager.shutDown();
        usdTraderDepositSimulator.shutDown();
    }
}
