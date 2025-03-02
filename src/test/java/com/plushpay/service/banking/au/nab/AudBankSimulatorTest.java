package com.plushpay.service.banking.au.nab;

import com.plushpay.service.banking.au.AudBankSimulator;
import com.plushpay.service.banking.au.nab.directEntry.NabDirectEntryFile;
import com.plushpay.service.banking.au.nab.directEntry.codes.NabDirectEntryIndicator;
import com.plushpay.service.banking.au.nab.directEntry.codes.NabTransactionCode;
import com.plushpay.service.banking.au.nab.directEntry.records.NabDetailRecord;
import com.plushpay.service.banking.au.nab.directEntry.records.NabFileTotalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Terry Packer
 */
@SpringBootTest
public class AudBankSimulatorTest {

    @Autowired
    private AudBankSimulator audBankSimulator;

    public void test() {
        AudBankSimulator sim = new AudBankSimulator();

        System.out.println("Account Balance: " + sim.getAccountBalance());
        NabDirectEntryFile file = new NabDirectEntryFile();

        String bsbNumber = audBankSimulator.getBsbNumber();
        String accountNumber = audBankSimulator.getAccountNumber();
        NabDirectEntryIndicator indicator = NabDirectEntryIndicator.NONE;
        NabTransactionCode code = NabTransactionCode.DEBIT;
        long amount = 1000000; //Amount in cents
        String accountTitle = audBankSimulator.getAccountTitle();

        String lodgementReference = "Stuff";
        String userBsb = "888-000";
        String userAccountNumber = "890890";
        String remitter = "Terry Packer";
        long withholdingTax = 0;

        //Create a transaction
        NabDetailRecord detail = new NabDetailRecord(bsbNumber,
            accountNumber, indicator,
            code, amount, accountTitle,
            lodgementReference, userBsb, userAccountNumber,
            remitter, withholdingTax);

        file.addDetail(detail);

        detail = new NabDetailRecord(bsbNumber,
            accountNumber, indicator,
            NabTransactionCode.DEBIT, 2000000, accountTitle,
            lodgementReference, userBsb, userAccountNumber,
            remitter, withholdingTax);

        file.addDetail(detail);

        //SEtup the total
        long fileNetAmount = 30000000;
        long fileCreditAmount = 0;
        long fileDebitAmount = 30000000;
        int record1Count = 2;
        NabFileTotalRecord totalRecord = new NabFileTotalRecord(bsbNumber,
            fileNetAmount, fileCreditAmount,
            fileDebitAmount, record1Count);
        file.setTotalRecord(totalRecord);

        try {
            sim.processTransactions(file);
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        System.out.println("Account Balance: " + sim.getAccountBalance());

        try {
            System.out.println(
                "Nai File Output:\n" + sim.generateNaiFile().getHeader().generateRecord());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            System.out.println("Nab Direct Entry File Output:\n" + file.generateFile());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        /*Start the thread */
        //sim.contextInitialized(null);

        //try {
        //	Thread.sleep(60000);
        //} catch (InterruptedException e) {
        // TODO Auto-generated catch block
        //	e.printStackTrace();
        //} //for 60s

        //Shutdown
        //sim.contextDestroyed(null);
    }
}
