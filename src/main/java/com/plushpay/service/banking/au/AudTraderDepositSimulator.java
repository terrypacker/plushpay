package com.plushpay.service.banking.au;

import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.trader.TraderStatus;
import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFile;
import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFileFilter;
import com.plushpay.service.banking.au.nab.directEntry.codes.NabDirectEntryIndicator;
import com.plushpay.service.banking.au.nab.directEntry.codes.NabTransactionCode;
import com.plushpay.service.banking.au.nab.directEntry.records.NabDetailRecord;
import com.plushpay.service.banking.au.nab.directEntry.records.NabFileTotalRecord;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.trader.TraderService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


/**
 * Simulator to create a deposit for all traders in DEPOSIT_FUNDS state trades.
 *
 * @author tpacker
 */
@ConditionalOnProperty(value = "com.plushpay.simulation.enabled", havingValue = "true")
@Component
public class AudTraderDepositSimulator extends Thread {

    private final Log log = LogFactory.getLog(getClass());
    private volatile boolean shutdown;
    private long pollPeriod;
    private String folder;

    private List<Long> processedTraders;

    private final AudBankSimulator audBankSimulator;
    private final TraderService traderService;

    public AudTraderDepositSimulator(AudBankSimulator audBankSimulator,
        TraderService traderService) {
        super("Aud Trader Deposit Simulator");
        this.audBankSimulator = audBankSimulator;
        this.traderService = traderService;

        this.shutdown = false;
        this.pollPeriod = 5000;

        //Pick the folder location
        this.folder = Thread.currentThread().getContextClassLoader().getResource("com")
            .getPath(); //Get the directory to com

        //Move up to the nab folder in web root
        this.folder = this.folder + "../../../nab/";

        this.processedTraders = new ArrayList<Long>();

    }

    /**
     * Find all traders that need to deposit AUD funds
     *
     * @return
     */
    private List<Trader> getTradersReadyForDeposit() {
        return traderService.getSelling(TraderStatus.DEPOSIT,
            CurrencyCodeEnum.AUD, this.processedTraders).toList();
    }

    public void run() {

        List<Trader> newTraders;

        while (!this.shutdown) {

            try {
                newTraders = this.getTradersReadyForDeposit();
                this.simulateDeposits(newTraders);

            } catch (Exception e1) {
                this.log.error("Unable to Simulate Trader Deposits.", e1);
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
     * Simulate any deposits that should be made into our account by traders selling AUD
     *
     * @throws Exception
     */
    public void simulateDeposits(List<Trader> traders) throws Exception {

        //TODO Should use Traders Instead of trades
        if (traders.size() > 0) {

            long totalCredits = 0;
            List<NabDetailRecord> credits = new ArrayList<NabDetailRecord>();
            NabDetailRecord credit = null;
            long amount, withholdingTax;

            //Create a DirectEntry File with a credit for each trader
//			for(int i=0; i<trades.size(); i++){

//				//Get the group selling AUD
//				//We need to detect the Seller of AUD as they will be crediting our account
//	 			if(trades.get(i).getBuyerGroup().getCurrencyToSell().getType().getCode() == CurrencyCodeEnum.AUD){
//					traders = trades.get(i).getBuyerGroup().getTraders();
//				}else if(trades.get(i).getSellerGroup().getCurrencyToSell().getType().getCode() == CurrencyCodeEnum.AUD){
//					traders = trades.get(i).getSellerGroup().getTraders();
//				}else{
//					traders = new ArrayList<Trader>(); //Create emtpy list if neither
//				}

            for (int j = 0; j < traders.size(); j++) {
                String bsbNumber = "999-000";
                String accountNumber = "123456789";
                NabDirectEntryIndicator indicator = NabDirectEntryIndicator.NONE;
                NabTransactionCode code = NabTransactionCode.CREDIT;
                amount = traders.get(j).getCurrencyToSell().getValue() / 100; //WATCH ROUNDING!!!!
                totalCredits = totalCredits + amount; //add it to the total
                String accountTitle = "Trader Account Title";
                String lodgementReference = String.valueOf(traders.get(j).getGroup().getId());
                String userBsb = audBankSimulator.getBsbNumber();
                String userAccountNumber = audBankSimulator.getAccountNumber();
                String remitter = traders.get(j).getId() + "";
                withholdingTax = 0;

                //Create a debit for each
                credit = new NabDetailRecord(bsbNumber,
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
                credits.add(credit);
                this.processedTraders.add(traders.get(j).getId());

                this.log.info("Simulating deposit of " + (amount / 100) + " for trader " + remitter
                    + " in group " + traders.get(j).getGroup().getId());
            }
            //Write file to bank
            //Create a file
            NabDirectEntryFile file = new NabDirectEntryFile();
            file.setDetails(credits);

            //We need to add total
            NabFileTotalRecord fileTotal = new NabFileTotalRecord(audBankSimulator.getBsbNumber(),
                totalCredits, totalCredits, 0, credits.size());
            file.setTotalRecord(fileTotal);

            //Write it out
            //Write it to disk
            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");

            NabDirectEntryFileFilter nabFilter = new NabDirectEntryFileFilter();
            this.log.info(
                "Writing Nab File: dep_" + sdf.format(now.getTime()) + nabFilter.getExtension());

            File output = new File(
                this.folder + "dep_" + sdf.format(now.getTime()) + nabFilter.getExtension());
            FileWriter fw = new FileWriter(output);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(file.generateFile());
            bw.flush();
            bw.close();

        }// End if Traders.size > 0


    }


    public void shutDown() {
        //Shut'er down
        this.shutdown = true;
        this.interrupt();

    }

    public void startUp() {
        // TODO Check thread state before starting
        this.shutdown = false;
        this.start();

    }

}
