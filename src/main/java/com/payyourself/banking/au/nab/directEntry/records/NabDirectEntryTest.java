package com.payyourself.banking.au.nab.directEntry.records;

import java.io.IOException;
import java.text.ParseException;

import com.payyourself.banking.au.nab.directEntry.codes.NabDirectEntryIndicator;
import com.payyourself.banking.au.nab.directEntry.codes.NabTransactionCode;

public class NabDirectEntryTest {

	
	 public static void main(String[] args) throws IOException{
		 
		 String descriptiveLine = "0484-009    15999 500000020000COOK J R                        880468            083-001015014261NAT ONLINE LTD  00000000";
		 try {
			//NabDescriptiveRecord descriptiveRecord = new NabDescriptiveRecord(descriptiveLine);
		
			String detailLine1 = "1083-001100697777 500000077852CLARK N D                       861115            083-001015014261NAT ONLINE LTD  00000000";
			String detailLine0 = "1063-009     7777 500000064000HILLS M C                       851119            083-001015014261NAT ONLINE LTD  00000000";
			
			NabDetailRecord detailRecord = new NabDetailRecord(detailLine1);
			System.out.println("Detail Record Input : " + detailLine1);
			System.out.println("Detail Record Output: " + detailRecord.getRecord());
			NabDetailRecord detailRecord1 = new NabDetailRecord(detailRecord.getBsbNumber(),
					detailRecord.getAccountNumber(), detailRecord.getIndicator(),
					detailRecord.getCode(), detailRecord.getAmount(), 
					detailRecord.getAccountTitle(),
					detailRecord.getLodgementReference(),
					detailRecord.getUserBsb(),
					detailRecord.getUserAccountNumber(),
					detailRecord.getRemitter(),
					detailRecord.getWithholdingTax());
			System.out.println("New Detail Record   : " + detailRecord1.getRecord());
			
			
			
			String fileTotalLine = "7999-999            000000000000035095910003509591                        000049                                        ";
			NabFileTotalRecord fileTotal = new NabFileTotalRecord(fileTotalLine);
			System.out.println("File Total Input :" + fileTotalLine);
			System.out.println("File Total Output:" + fileTotal.getRecord());
			
			NabFileTotalRecord fileTotal1 = new NabFileTotalRecord(fileTotal.getBsbNumber(),
					fileTotal.getFileNetAmount(),
					fileTotal.getFileCreditAmount(),
					fileTotal.getFileDebitAmount(),
					fileTotal.getRecord1Count());
			System.out.println("New Total Record :" + fileTotal1.getRecord());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
		
	 }
}
