package com.payyourself.banking.au.nab.nai.records;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.payyourself.banking.bai2.records.Bai2AccountTrailer;
import com.payyourself.banking.bai2.records.Bai2FileTrailer;
import com.payyourself.banking.bai2.records.Bai2GroupTrailer;

public class NaiRecordTest {

	 /**
	  * Basic main to test functionality
	  * @param args
	  * @throws IOException
	  */
	 public static void main(String[] args) throws IOException{
	 
		 //TEST THE HEADER
		 String fileHeaderLine = "01,,BBBW,970619,1450,1,78,78/";
		 NaiFileHeader testHeader;
		try {
			testHeader = new NaiFileHeader(fileHeaderLine);
			 System.out.println("Testing Header: " + fileHeaderLine);
			 System.out.println("Parsed Output : " + testHeader.generateRecord());

			 NaiFileHeader testHeader2 = new NaiFileHeader(
					 testHeader.getRecieverId(), testHeader.getCreationDate(),testHeader.getCreationTime(), 
					 testHeader.getRecordLength(), testHeader.getBlockSize());
			 System.out.println("Constructor1  : " + testHeader2.generateRecord() );
			 
			 testHeader2 = new NaiFileHeader(
					 testHeader.getRecieverId(), Calendar.getInstance(),
					 testHeader.getRecordLength(), testHeader.getBlockSize());
			 System.out.println("Constructor2  : " + testHeader2.generateRecord() );
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		//String groupHeaderLine = "02,BBBW,NATAAU3M,1,970321,0000/";
		String groupHeaderLine = "02,,,,,,/";
		
		NaiGroupHeader testGroupHeader;
		try{
			testGroupHeader = new NaiGroupHeader(groupHeaderLine);
			System.out.println("Testing Group Header: " + groupHeaderLine);
			System.out.println("Parsed Output       : " + testGroupHeader.generateRecord());
			
			NaiGroupHeader testGroupHeader2 = new NaiGroupHeader(testGroupHeader.getUltimateReciever(),
					testGroupHeader.getOriginatorId(),testGroupHeader.getAsOfDate(), testGroupHeader.getAsOfTime());
			 
			System.out.println("Constructor1        : " + testGroupHeader2.generateRecord());
			
			testGroupHeader2 = new NaiGroupHeader(testGroupHeader.getUltimateReciever(),
					testGroupHeader.getOriginatorId(),Calendar.getInstance());
			 
			System.out.println("Constructor2        : " + testGroupHeader2.generateRecord());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Test a type 03
		

		String accountDetailsLine1 = "03,111111111,AUD,015,10000011,100,000,102,000,400/";
		String accountDetailsLine2 = "88,000,402,000,500,40011-,501,50011,502/";
		String accountDetailsLine3 = "88,200011,503,200011,965,000,966,050/";
		String accountDetailsLine4 = "88,967,075,968,006,969,017/";

		/*String accountDetailsLine1 = "03,,,,,,,,,,/";
		String accountDetailsLine2 = "88,,,,,,,,,/";
		String accountDetailsLine3 = "88,200011,503,200011,965,000,966,050/";
		String accountDetailsLine4 = "88,967,075,968,006,969,017/";
		*/
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
			NaiAccountSummary accountSummary = new NaiAccountSummary(lines);
			System.out.println("Parsed Output         :   " + accountSummary.generateRecord());

			NaiAccountSummary accountSummary2 = new NaiAccountSummary(accountSummary.getAccountNumber(),
					accountSummary.getCurrencyCode(),
					accountSummary.getDetailsSection(),
					accountSummary.getTransactions());
			
			System.out.println("Constructor           : " + accountSummary2.generateRecord());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//Transaction Details
		String details1 = "16,475,200000,0,0000546/";
		String details2 = "88,YOUR REF=0:B FST COMM PEA, REC FROM=FIRST COMMUNITY BANK PEARLAND TX, FED ID";
		String details3 = "88,=113117767,B:0 CUSTOMER=:8138091 CENIZO PROPERTIES & INVESTMENTS 2121 SAGE";
		String details4 = "88,STE 370 HOU TX 77056,REMAKR=GF 400732 ADVICE: JANET KARR 713-653-6105,FED T";
		String details5 = "88, IME=10:06,REC GFP=06301408,MRN SEQ=003,FED REF=0630 K3Q0CJOD 000003 **VIA F";
		
		
		String simpleDetails = "16,475,200000,0,0000546/";
		lines = new ArrayList<String>();
		lines.add(simpleDetails);
		

		
		try {
			System.out.println("Testing Transaction Details: " + simpleDetails);
			NaiTransactionDetail details = new NaiTransactionDetail(lines);
			System.out.println("Parsed Output              : " + details.generateRecord());
			
			NaiTransactionDetail transDetails2 = new NaiTransactionDetail(lines);

			System.out.println("Constructor                : " + transDetails2.generateRecord());
			
		//Complex Transaction Details
			lines = new ArrayList<String>();
			lines.add(details1);
			lines.add(details2);
			lines.add(details3);
			lines.add(details4);
			lines.add(details5);
			NaiTransactionDetail transDetail3 = new NaiTransactionDetail(lines);
			
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
		String trailer = "49,10741625,10741555/";

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
		trailer = "98,1045040086,39,288/";

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
		trailer = "49,10688141,6/";

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
		trailer = "99,5285018446,2,315/";

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
