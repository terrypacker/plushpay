package com.plushpay.service.banking.us;

import com.plushpay.service.banking.DebitCredit;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


/**
 * Singleton class for one simulator
 *
 * @author tpacker
 */
@ConditionalOnProperty(value = "com.plushpay.simulation.enabled", havingValue = "true")
@Component
public class UsdBankSimulator extends Thread {

    private final Log log = LogFactory.getLog(getClass());
    private String accountNumber = "9999999";
    private PyCurrency accountBalance; //In Usd cents
    private PyCurrencyType type;

    private String folder; //Location on server where files will be dropped and picked up
    private boolean shutdown; //Flag to shutdown thread;

    private List<UsdBankTransaction> transactionHistory;
    private List<UsdBankTransaction> withdrawls;
    private List<UsdBankTransaction> deposits;

    private long pollPeriod;

    public String getRoutingNumber() {
        return "001-001-92342";
    }

    public String getAccoutnNumber() {
        return accountNumber;
    }

    private UsdBankSimulator() {
        super("Usd Bank Simulator");

        //Pick the folder location
        this.folder = Thread.currentThread().getContextClassLoader().getResource("com")
            .getPath(); //Get the directory to com

        //Move up to the nab folder in web root
        this.folder = this.folder + "../../../nab/";

        this.type = new PyCurrencyType(CurrencyCodeEnum.USD, 10000, ZonedDateTime.now());
        this.accountBalance = new PyCurrency(0, type);

        this.transactionHistory = new ArrayList<UsdBankTransaction>();
        this.deposits = new ArrayList<UsdBankTransaction>();
        this.withdrawls = new ArrayList<UsdBankTransaction>();

        this.shutdown = false;

        this.pollPeriod = 5000;
    }


    public void shutDown() {
        this.shutdown = true; //Shut'er down Mike

        this.interrupt(); //Interrupt execution
    }


    public void run() {

        while (true) {
            try {

                Thread.sleep(this.pollPeriod);

            } catch (InterruptedException e) {
                if (this.shutdown) {
                    this.log.info("Shutting down Usd Bank Simulator");
                    return;
                } else {
                    this.log.error("Interrupted Exception: ", e);
                }
            } catch (Exception e) {
                this.log.error("Bank Simulator Error", e);
            }
        }

    }

    public PyCurrency getAccountBalance() {
        return this.accountBalance;
    }

    public synchronized void addWithdrawl(PyCurrency amount, long traderId, long tradeId)
        throws Exception {
        UsdBankTransaction newTrans = new UsdBankTransaction(DebitCredit.DEBIT, traderId, tradeId,
            amount.getValue() / 100);
        this.transactionHistory.add(newTrans);
        this.withdrawls.add(newTrans);
        this.accountBalance = this.accountBalance.minus(amount);


    }

    public synchronized void addDeposit(PyCurrency amount, long traderId, long tradeId)
        throws Exception {

        UsdBankTransaction newTrans = new UsdBankTransaction(DebitCredit.CREDIT, traderId, tradeId,
            amount.getValue() / 100);
        this.transactionHistory.add(newTrans);
        this.deposits.add(newTrans);
        this.accountBalance = this.accountBalance.add(amount);
    }


    /**
     * Get any deposits made since last call of this function
     *
     * @return
     */
    public synchronized List<UsdBankTransaction> getDeposits() {

        List<UsdBankTransaction> deposits = this.deposits;
        this.deposits = new ArrayList<UsdBankTransaction>();
        return deposits;


    }

    /**
     * Get any withdrawls made since last call of this function
     *
     * @return
     */
    public synchronized List<UsdBankTransaction> getWithdrawls() {

        List<UsdBankTransaction> withdrawls = this.withdrawls;
        this.withdrawls = new ArrayList<UsdBankTransaction>();
        return withdrawls;

    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }


}
