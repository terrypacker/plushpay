package com.payyourself.banking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.payyourself.banking.bai2.codes.Bai2AccountCode;
import com.payyourself.banking.bai2.records.Bai2AccountSummary;
import com.payyourself.banking.bai2.records.Bai2AccountTrailer;
import com.payyourself.banking.bai2.records.Bai2FileHeader;
import com.payyourself.banking.bai2.records.Bai2FileTrailer;
import com.payyourself.banking.bai2.records.Bai2GroupHeader;
import com.payyourself.banking.bai2.records.Bai2GroupTrailer;
import com.payyourself.banking.bai2.records.Bai2TransactionDetail;

public class UsdBankSimulator {

	
	 /**
	  * Basic main to test functionality
	  * @param args
	  * @throws IOException
	  */
	 public static void main(String[] args) throws IOException{
	 
		 //TEST THE HEADER
		 String fileHeaderLine = "01,021000021,G5702364 COL,060202,1301,001,,,2/\n";
		 Bai2FileHeader testHeader;
		try {
			testHeader = new Bai2FileHeader(fileHeaderLine);
			 System.out.println("Testing Header: " + fileHeaderLine);
			 System.out.println("Parsed Output : " + testHeader.generateRecord());

			 Bai2FileHeader testHeader2 = new Bai2FileHeader(testHeader.getSenderId(),
					 testHeader.getRecieverId(), testHeader.getCreationDate(), 
					 testHeader.getFileId(), testHeader.getRecordLength(), testHeader.getBlockSize());
			 System.out.println("Constructor   : " + testHeader2.generateRecord() );
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		String groupHeaderLine = "02,G5702364 COL,044000037,1,060201,1301,USD,3/\n";
		Bai2GroupHeader testGroupHeader;
		try{
			testGroupHeader = new Bai2GroupHeader(groupHeaderLine);
			System.out.println("Testing Group Header: " + groupHeaderLine);
			System.out.println("Parsed Output       : " + testGroupHeader.generateRecord());
			
			Bai2GroupHeader testGroupHeader2 = new Bai2GroupHeader(testGroupHeader.getUltimateReciever(),
					testGroupHeader.getOriginatorId(), testGroupHeader.getGroupStatus(), 
					testGroupHeader.getAsOfDate(), testGroupHeader.getCurrencyCode(),
					testGroupHeader.getAsOfDateModifier());
			 
			System.out.println("Constructor         : " + testGroupHeader2.generateRecord());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Test a type 03
		
		//Overview
		//Customer Account number: 000001234567890
		//CurrencyCode: USD
		
		//****Detail 1****
		//Type Code: 010 - Opening Ledger
		//Amount: $8944.48
		//Item Count: null
		//Funds Type: null
		//***************
		
		//****Detial 2****
		//Type Code: 015
		//Amount: -$415063.52
		//Item Count: null
		//Funds Type: null
		//*****************
		
		//****Detail 3****
		//Type Code: 040
		//Amount: $84944.48
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 4****
		//Type Code: 043
		//Amount: -$208271.18
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 5****
		//Type Code: 045
		//Amount: -$3047940.52
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 6****
		//Type Code: 050
		//Amount: -$3047940.52
		//Item Count: null
		//Funds Type: null
		//****************
		
	
		//****Detail 7****
		//Type Code: 055
		//Amount: -$297010.85
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 8****
		//Type Code: 063
		//Amount: $2632877.00
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 9****
		//Type Code: 072
		//Amount: $2632877.00
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 10***
		//Type Code:074
		//Amount: $0
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 11***
		//Type Code:075
		//Amount: $0
		//Item Count: null
		//Funds Type: null
		//****************
		
		//****Detail 12***
		//Type Code:100
		//Amount: $0
		//Item Count: null
		//Funds Type: null
		//****************

		//****Detail 13***  NOT SURE HOW IT WORKS FROM HERE ON
		//Type Code:400
		//Amount: $500,008.00
		//Item Count: 2
		//Funds Type: null
		//****************

		//****Detail 14***
		//Type Code: 510
		//Amount: $500,000.00
		//Item Count: 1
		//Funds Type: null
		//****************

		//****Detail 15***
		//Type Code: 690
		//Amount: $8.00
		//Item Count: 1
		//Funds Type: null
		//****************

		
		
		String accountDetailsLine1 = "03,000001234567890,USD,010,894448,,,015,-41506352,,,040,8494448,,,/\n";
		String accountDetailsLine2 = "88,043,-20827118,,,045,-304794052,,,050,-304794052,,,055,-29701085,,,/\n"; //There was an error in the documentation on this line!!!
		String accountDetailsLine3 = "88,063,263287700,,,072,263287700,,,074,0,,,075,0,,,100,0,,,400,50000800,2,,/\n";
		String accountDetailsLine4 = "88,510,50000000,1,,690,800,1,,/\n"; //There was another error on this line!!!
		
		List<String> lines = new ArrayList<String>();
		lines.add(accountDetailsLine1);
		lines.add(accountDetailsLine2);
		lines.add(accountDetailsLine3);
		lines.add(accountDetailsLine4);
		try {
			
			System.out.println("Account Details Header: " + accountDetailsLine1);
			System.out.println("Account Details Header: " + accountDetailsLine2);
			System.out.println("Account Details Header: " + accountDetailsLine3);
			System.out.println("Account Details Header: " + accountDetailsLine4);	
			Bai2AccountSummary accountSummary = new Bai2AccountSummary(lines);
			System.out.println("Parsed Output         :   " + accountSummary.generateRecord());

			Bai2AccountSummary accountSummary2 = new Bai2AccountSummary(accountSummary.getCustomerAccountNumber(),
					accountSummary.getCurrencyCode(),
					accountSummary.getDetailsSection());
			
			System.out.println("Constructor           : " + accountSummary2.generateRecord());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//Transaction Details
		String details1 = "16,165,140427495,S,140427495,0,0,0322817181ZT,0:B FST COMM PEA/\n";
		String details2 = "88,YOUR REF=0:B FST COMM PEA, REC FROM=FIRST COMMUNITY BANK PEARLAND TX, FED ID";
		String details3 = "88,=113117767,B:0 CUSTOMER=:8138091 CENIZO PROPERTIES & INVESTMENTS 2121 SAGE";
		String details4 = "88,STE 370 HOU TX 77056,REMAKR=GF 400732 ADVICE: JANET KARR 713-653-6105,FED T";
		String details5 = "88, IME=10:06,REC GFP=06301408,MRN SEQ=003,FED REF=0630 K3Q0CJOD 000003 **VIA F";
		
		
		String simpleDetails = "16,175,83124608,S,17647808,64097300,1379500,0010000959TS,0000000000,/\n";
		lines = new ArrayList<String>();
		lines.add(simpleDetails);
		

		
		try {
			System.out.println("Testing Transaction Details: " + simpleDetails);
			Bai2TransactionDetail details = new Bai2TransactionDetail(lines);
			System.out.println("Parsed Output              : " + details.generateRecord());
			
			//Create the text input
			String text = "";
			for(int i=0; i<details.getText().size(); i++){
				text = text + details.getText().get(i);
			}
			
			Bai2TransactionDetail transDetails2 = new Bai2TransactionDetail(details.getTypeCode(),details.getAmount(),
					details.getType(),details.getImmediateAmount(),details.getOneDayAmount(),details.getTwoDayAmount(),
					details.getBankReferenceNumber(), details.getCustomerReferenceNumber(),
					text);

			System.out.println("Constructor                : " + transDetails2.generateRecord());
			
		//Complex Transaction Details
			lines = new ArrayList<String>();
			lines.add(details1);
			lines.add(details2);
			lines.add(details3);
			lines.add(details4);
			lines.add(details5);
			Bai2TransactionDetail transDetail3 = new Bai2TransactionDetail(lines);
			
			System.out.println("Testing Transaction Details: " + details1);
			System.out.println("Testing Transaction Details: " + details2);
			System.out.println("Testing Transaction Details: " + details3);
			System.out.println("Testing Transaction Details: " + details4);
			System.out.println("Testing Transaction Details: " + details5);

			System.out.println("Parsed Output              : " + transDetail3.generateRecord());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	 
		
		//Test the Account Trailer
		String trailer = "49,10688141,6/\n";

		 try {
				System.out.println("Testing Acct Trailer: " + trailer);
				Bai2AccountTrailer acctTrailer = new Bai2AccountTrailer(trailer);
				System.out.println("Parsed Output       : " + acctTrailer.generateRecord());
				Bai2AccountTrailer trailer2 = new Bai2AccountTrailer(acctTrailer.getAccountControlTotal(),acctTrailer.getNumberOfRecords());
				System.out.println("Constructor         : " + trailer2.generateRecord() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test the Group Trailer
		trailer = "98,1045040086,39,288/\n";

		 try {
				System.out.println("Testing Group Trailer : " + trailer);
				Bai2GroupTrailer groupTrailer = new Bai2GroupTrailer(trailer);
				System.out.println("Parsed Output         : " + groupTrailer.generateRecord());
				Bai2GroupTrailer trailer2 = new Bai2GroupTrailer(groupTrailer.getGroupControlTotal(),
						groupTrailer.getNumberOfAccounts(), groupTrailer.getNumberOfRecords());
				System.out.println("Constructor           : " + trailer2.generateRecord() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test the Account Trailer
		trailer = "49,10688141,6/\n";

		 try {
				System.out.println("Testing Acct Trailer: " + trailer);
				Bai2AccountTrailer acctTrailer = new Bai2AccountTrailer(trailer);
				System.out.println("Parsed Output       : " + acctTrailer.generateRecord());
				Bai2AccountTrailer trailer2 = new Bai2AccountTrailer(acctTrailer.getAccountControlTotal(),acctTrailer.getNumberOfRecords());
				System.out.println("Constructor         : " + trailer2.generateRecord() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test the File Trailer
		trailer = "99,5285018446,2,315/\n";

		 try {
				System.out.println("Testing File Trailer : " + trailer);
				Bai2FileTrailer fileTrailer = new Bai2FileTrailer(trailer);
				System.out.println("Parsed Output        : " + fileTrailer.generateRecord());
				Bai2FileTrailer trailer2 = new Bai2FileTrailer(fileTrailer.getFileControlTotal(),
						fileTrailer.getNumberOfGroups(), fileTrailer.getNumberOfRecords());
				System.out.println("Constructor          : " + trailer2.generateRecord() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }
}
