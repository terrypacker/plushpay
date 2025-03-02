package com.plushpay.service.banking.au;

import com.plushpay.repository.trading.trade.Trade;
import com.plushpay.repository.trading.trade.TradeStatus;
import com.plushpay.repository.trading.trader.Trader;
import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFile;
import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFileFilter;
import com.plushpay.service.banking.au.nab.directEntry.codes.NabDirectEntryIndicator;
import com.plushpay.service.banking.au.nab.directEntry.codes.NabTransactionCode;
import com.plushpay.service.banking.au.nab.directEntry.records.NabDetailRecord;
import com.plushpay.service.banking.au.nab.directEntry.records.NabFileTotalRecord;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.trading.TradeService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class responsible for debiting our account
 *
 * @author tpacker
 */
public class AudTradeDebitManager extends Thread {

    private Log log = LogFactory.getLog(getClass());
    private volatile boolean shutdown;
    private long pollPeriod;
    private String folder;
    private String accountNumber;
    private List<Long> processedTrades;

    private AudBankSimulator audBankSimulator;
    private final TradeService tradeService;

    public AudTradeDebitManager(AudBankSimulator audBankSimulator, TradeService tradeService) {
        this.audBankSimulator = audBankSimulator;
        this.tradeService = tradeService;

        //Pick the folder location
        this.folder = Thread.currentThread().getContextClassLoader().getResource("com")
            .getPath(); //Get the directory to com

        //Move up to the nab folder in web root
        this.folder = this.folder + "../../../nab/";

        this.shutdown = false;
        this.pollPeriod = 5000;
        this.accountNumber = audBankSimulator.getAccountNumber();
        this.processedTrades = new ArrayList<Long>();
    }

    public void shutDown() {
        //Shut'er down
        this.shutdown = true;
        this.interrupt();

    }

    public void run() {

        this.log.info("Starting Aud Trade Debit Manager.");
        while (!this.shutdown) {

            try {
                this.processTradeDebits();
            } catch (Exception e1) {
                this.log.error("Unable to process Trade Debits.", e1);
            }

            try {
                Thread.sleep(this.pollPeriod);
            } catch (InterruptedException e) {
                if (shutdown) {
                    break;
                } else {
                    this.log.error("Interrupted.", e);
                }

            }
        }//end while

        this.log.info("Shutting Down.");
    }


    /**
     * @throws Exception
     */
    public void processTradeDebits() throws Exception {

        List<Trade> trades = tradeService.getAllWithStatusExcluding(TradeStatus.CLOSED,
            this.processedTrades).toList();

        List<Trader> traders = null;

        List<NabDetailRecord> debits = new ArrayList<NabDetailRecord>();
        NabDetailRecord debit = null;

        //For total of all debits;
        long totalDebits = 0;

        //For all the closed trades we need to make debits from our account to the chosen beneificiaries
        for (int i = 0; i < trades.size(); i++) {

            this.processedTrades.add(trades.get(i).getId());

            //Find all buyers of AUD and make debits to the account as per the beneis
            if (trades.get(i).getBuyerGroup().getCurrencyToBuy().getType().getCode()
                == CurrencyCodeEnum.AUD) {
                traders = trades.get(i).getBuyerGroup().getTraders();
            } else if (trades.get(i).getSellerGroup().getCurrencyToBuy().getType().getCode()
                == CurrencyCodeEnum.AUD) {
                traders = trades.get(i).getSellerGroup().getTraders();
            }

            for (int j = 0; j < traders.size(); j++) {

                for (int k = 0; k < traders.get(j).getBeneficiaries().size(); k++) {

                    String bsbNumber = "888-000"; //buyers.get(j).getBeneficiaries().get(k).;
                    String accountNumber = "1234456";
                    NabDirectEntryIndicator indicator = NabDirectEntryIndicator.NONE;
                    NabTransactionCode code = NabTransactionCode.DEBIT;
                    long amount = traders.get(j).getBeneficiaries().get(k).getAmount().getValue()
                        / 100; //Value in Cents
                    totalDebits = totalDebits + amount; //Totaal them up
                    String accountTitle = "UpdatBene";
                    String lodgementReference = String.valueOf(trades.get(i).getId());
                    String userBsb = audBankSimulator.getBsbNumber();
                    String userAccountNumber = this.accountNumber;
                    String remitter =
                        traders.get(j).getId() + ""; //Trader ID is the remitter info
                    long withholdingTax = 0;

                    //Create a debit for each
                    debit = new NabDetailRecord(bsbNumber,
                        accountNumber,
                        indicator,
                        code,
                        amount,
                        accountTitle,
                        lodgementReference,
                        userBsb,
                        userAccountNumber,
                        remitter,
                        withholdingTax);
                    debits.add(debit);

                }


            }//end for buyers of trade
        }//end for trades

        if (debits.size() > 0) {

            //Create a file
            NabDirectEntryFile file = new NabDirectEntryFile();

            file.setDetails(debits);

            //We need to add total
            NabFileTotalRecord fileTotal = new NabFileTotalRecord(audBankSimulator.getBsbNumber(),
                totalDebits, totalDebits, 0, debits.size());
            file.setTotalRecord(fileTotal);

            //Write it out
            //Write it to disk
            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");

            NabDirectEntryFileFilter nabFilter = new NabDirectEntryFileFilter();

            File output = new File(
                this.folder + sdf.format(now.getTime()) + nabFilter.getExtension());
            FileWriter fw = new FileWriter(output);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(file.generateFile());
            bw.flush();
            bw.close();
        }
    }

    public void startUp() {
        // TODO Check thread state before startup
        this.shutdown = false;
        this.start();

    }


}
