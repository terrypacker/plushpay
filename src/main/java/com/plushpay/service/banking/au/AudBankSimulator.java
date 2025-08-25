package com.plushpay.service.banking.au;

import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFile;
import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFileFilter;
import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFileParser;
import com.plushpay.service.banking.au.nab.directEntry.codes.NabTransactionCode;
import com.plushpay.service.banking.au.nab.nai.NaiFile;
import com.plushpay.service.banking.au.nab.nai.NaiFileFilter;
import com.plushpay.service.banking.au.nab.nai.codes.NaiAccountSummaryCode;
import com.plushpay.service.banking.au.nab.nai.codes.NaiTransactionCode;
import com.plushpay.service.banking.au.nab.nai.records.NaiAccountSummary;
import com.plushpay.service.banking.au.nab.nai.records.NaiAccountSummaryDetail;
import com.plushpay.service.banking.au.nab.nai.records.NaiAccountTrailer;
import com.plushpay.service.banking.au.nab.nai.records.NaiFileHeader;
import com.plushpay.service.banking.au.nab.nai.records.NaiFileTrailer;
import com.plushpay.service.banking.au.nab.nai.records.NaiGroupHeader;
import com.plushpay.service.banking.au.nab.nai.records.NaiGroupTrailer;
import com.plushpay.service.banking.au.nab.nai.records.NaiTransactionDetail;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.currency.type.PyCurrencyType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


/**
 * Singleton class for one simulator
 *
 * @author tpacker
 */
@ConditionalOnProperty(value = "com.plushpay.simulation.enabled", havingValue = "true")
@Component
public class AudBankSimulator extends Thread {

    private final Log log = LogFactory.getLog(getClass());
    private final String bsbNumber;
    private final String accountNumber;
    private final String pyCustomerId;
    private final String bankId;
    private final String accountTitle;

    private PyCurrency accountBalance; //In Aud cents
    private final PyCurrencyType type;


    private final File folder; //Location on server where files will be dropped and picked up
    private boolean shutdown; //Flag to shutdown thread;

    private final List<NaiTransactionDetail> transactions;
    private final List<NaiTransactionDetail> transactionHistory;

    private final long pollPeriod;

    public AudBankSimulator(@Value("${com.plushpay.banking.storage}") Resource bankingDirectory,
        @Value("${com.plushpay.simulation.banking.aud.rateToBase}") long rateToBase,
        @Value("${com.plushpay.simulation.banking.aud.startingBalance}") long startingBalance,
        @Value("${com.plushpay.simulation.banking.aud.bsb}") String bsb,
        @Value("${com.plushpay.simulation.banking.aud.account}") String account,
        @Value("${com.plushpay.simulation.banking.aud.customerId}") String customerId,
        @Value("${com.plushpay.simulation.banking.aud.bank}") String bank,
        @Value("${com.plushpay.simulation.banking.aud.accountName}") String accountName,
        @Value("${com.plushpay.simulation.banking.period}") long pollPeriod)
        throws IOException {
        super("Aud Bank Simulator");

        //Pick the folder location
        this.folder = bankingDirectory.getFile();
        this.bsbNumber = bsb;
        this.accountNumber = account;
        this.pyCustomerId = customerId;
        this.bankId = bank;
        this.accountTitle = accountName;

        this.type = new PyCurrencyType(CurrencyCodeEnum.AUD, rateToBase, ZonedDateTime.now());
        this.accountBalance = new PyCurrency(startingBalance, type);

        this.transactions = new ArrayList<>();
        this.transactionHistory = new ArrayList<>();
        this.shutdown = false;
        this.pollPeriod = pollPeriod;
    }


    public NaiFile generateNaiFile() {

        int recordLength = 0;
        int blockSize = 0;

        //Setup the file Header
        NaiFileHeader header = new NaiFileHeader(pyCustomerId,
            Calendar.getInstance(),
            recordLength, blockSize);

        //Create the Account Groups (1)
        NaiGroupHeader groupHeader = new NaiGroupHeader(pyCustomerId,
            bankId,
            Calendar.getInstance());

        //Create an Account Summary
        NaiAccountSummary accountSummary = new NaiAccountSummary(accountNumber,
            CurrencyCodeEnum.AUD);

        //Add transactions to account summary
        accountSummary.setTransactions(this.transactions);

        //TODO Should add the old transaction to a history.
        for (int i = 0; i < this.transactions.size(); i++) {
            this.transactionHistory.add(transactions.get(i));
        }
        //Clear out the transactions now
        this.transactions.clear();

        //Add details to the account summary
        long sumTotA = 0;
        long sumTotB = 0;
        NaiAccountSummaryDetail closingBalance = new NaiAccountSummaryDetail(
            NaiAccountSummaryCode.CLOSING_BALANCE, this.accountBalance.getValue());
        accountSummary.addSummaryDetail(closingBalance);
        sumTotA = sumTotA + this.accountBalance.getValue();

        //Add an account Trailer
        long accountTotalA = sumTotA; //Sum of amounts fields in 03 records, including 965,966,967,968,969 codes and 16 and 88 records
        long accountTotalB = sumTotB; //	The sum of all amount fields in record types ???
        //(excluding the amounts for account summary codes 965,966,967,968,969), ??? and ??? for the account.
        NaiAccountTrailer accountTrailer = new NaiAccountTrailer(accountTotalA, accountTotalB);
        accountSummary.setTrailer(accountTrailer);

        //Add accountSummary
        groupHeader.addAccount(accountSummary);

        //Add a group trailer
        long groupTotalA = accountTotalA; //	The sum of the Account control totals A in all Account trailer (record type ???) records in this group.
        int numberOfAccounts = 1; //	The number of accounts in this group. That is the number of Account identifier and summary status (record type 03) records in this group.
        long groupTotalB = accountTotalA; //	The sum of the Account control totals B in all Account trailer (record type ???) records in this group

        //Account Trailer
        NaiGroupTrailer groupTrailer = new NaiGroupTrailer(groupTotalA, numberOfAccounts,
            groupTotalB);
        groupHeader.setTrailer(groupTrailer);

        header.addGroup(groupHeader);

        long fileTotalA = groupTotalA;//	The sum of the Group control totals A in all  Group Trailer (record type ???) records in this file.
        int numberOfGroups = 1;//	The number of groups in this file. That is, the number of Group header (record type ???) records in this file.
        int numberOfRecords = 7
            + this.transactions.size();//	The total number of records in this file. This includes the File header and File trailer records but excludes any device-oriented or job control records
        long fileTotalB = groupTotalB;  //	The sum of the Group control totals B in all Group Trailer (record type ???) records in this file.

        NaiFileTrailer fileTrailer = new NaiFileTrailer(fileTotalA, numberOfGroups, numberOfRecords,
            fileTotalB);
        header.setTrailer(fileTrailer);

        NaiFile file = new NaiFile(header);

        return file;

    }

    /**
     * Process the direct entry file make transactions
     *
     * @throws Exception
     */
    public void processTransactions(NabDirectEntryFile file) throws Exception {

        NaiTransactionDetail transaction;

        for (int i = 0; i < file.getDetails().size(); i++) {

            if (file.getDetails().get(i).getUserAccountNumber()
                .equals(accountNumber)) {
                //Is it for our account?
                NabTransactionCode code = file.getDetails().get(i).getCode();

                //Do any of the following codes
                switch (code) {
                    case DEBIT:
                        this.log.info(
                            "Debiting Account: " + (file.getDetails().get(i).getAmount() / 100)
                                + " for trader " +
                                file.getDetails().get(i).getRemitter() + " in trade "
                                + file.getDetails().get(i).getLodgementReference());
                        //Remove from account total
                        //multiply by 100
                        this.accountBalance = this.accountBalance.minus(
                            new PyCurrency(file.getDetails().get(i).getAmount() * 100, this.type));

                        //Add a transaction
                        transaction = new NaiTransactionDetail(NaiTransactionCode.TRANSFER_DEBIT,
                            file.getDetails().get(i).getAmount(),
                            file.getDetails().get(i).getRemitter(),
                            file.getDetails().get(i).getLodgementReference());

                        this.transactions.add(transaction);

                        break;
                    case CREDIT:
                        this.log.info(
                            "Crediting Account: " + (file.getDetails().get(i).getAmount() / 100)
                                + " for trader " +
                                file.getDetails().get(i).getRemitter() + " in group "
                                + file.getDetails().get(i).getLodgementReference());
                        //Add to account total (multiply by 100)
                        this.accountBalance = this.accountBalance.add(
                            new PyCurrency(file.getDetails().get(i).getAmount() * 100, this.type));
                        //Add a transaction
                        transaction = new NaiTransactionDetail(NaiTransactionCode.TRANSFER_CREDIT,
                            file.getDetails().get(i).getAmount(),
                            file.getDetails().get(i).getRemitter(),
                            file.getDetails().get(i).getLodgementReference());

                        this.transactions.add(transaction);

                        break;
                }//end case


            }//end if

        }//end for

        if (file.getDetails().size() > 0) {
            this.log.info("Account Balance: " + this.getAccountBalance());
        }

    }

    public PyCurrency getAccountBalance() {
        return this.accountBalance;
    }


    /**
     * Generate NaiFile whenever there isn't one in the folder and we have transactions to output.
     *
     * @throws Exception
     */
    public void outputNaiFile() throws Exception {

        if (this.transactions.size() == 0) {
            return;
        }
        //Find if there is a Nai File
        NaiFileFilter naiFilter = new NaiFileFilter();
        File[] naiFiles = folder.listFiles(naiFilter);

        //If there isn't one then generate one
        if (naiFiles.length == 0) {
            NaiFile file = this.generateNaiFile();

            //Write it to disk
            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");

            File output = new File(
                this.folder + sdf.format(now.getTime()) + naiFilter.getExtension());
            FileWriter fw = new FileWriter(output);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(file.generateFile());
            bw.flush();
            bw.close();
            this.log.info(
                "Generated new Nai File: " + sdf.format(now.getTime()) + naiFilter.getExtension());

        }
    }

    public void shutDown() {
        this.shutdown = true; //Shut'er down Mike

        this.interrupt(); //Interrupt execution
    }

    public void run() {

        while (true) {
            try {
                //Check for new files to process
                this.checkAndProcessNewFile();

                this.outputNaiFile();

                Thread.sleep(this.pollPeriod);


            } catch (InterruptedException e) {
                if (this.shutdown) {
                    this.log.info("Shutting down Aud Bank Simulator");
                    return;
                } else {
                    this.log.error("Interrupted Exception: ", e);
                }
            } catch (Exception e) {
                this.log.error("Bank Simulator Error", e);
            }
        }

    }


    public void checkAndProcessNewFile() throws Exception {
        //Now do the direct entry files

        NabDirectEntryFileFilter directEntryFilter = new NabDirectEntryFileFilter();
        File[] directEntryFiles = folder.listFiles(directEntryFilter);

        NabDirectEntryFileParser deParser = new NabDirectEntryFileParser();
        //Process each file
        for (int i = 0; i < directEntryFiles.length; i++) {

            //Parse it in
            try {
                deParser.parseInputFile(directEntryFiles[i]);
            } catch (Exception e) {
                this.log.error("Unable to parse file: " + directEntryFiles[i].getName(), e);
            }
            //Delete it
            if (!directEntryFiles[i].delete()) {
                this.log.error("Unable to delete Nab File: " + directEntryFiles[i].getName());
            }
        }

        //now process the files
        for (int i = 0; i < deParser.getFiles().size(); i++) {
            this.log.info("Processing File " + directEntryFiles[i].getName());
            this.processTransactions(deParser.getFiles().get(i));
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBsbNumber() {
        return bsbNumber;
    }

    public String getAccountTitle() {
        return this.accountTitle;
    }

    public void startUp() {
        this.shutdown = false;
        this.start();
    }


}
