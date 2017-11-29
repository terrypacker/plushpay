package com.payyourself.banking.au.nab.nai.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.banking.bai2.records.Bai2Record;

public class NaiAccountTrailer extends Bai2Record {

	private long accountTotalA; //Sum of amounts fields in 03 records, including 965,966,967,968,969 codes and 16 and 88 records
	private long accountTotalB; //	The sum of all amount fields in record types Ô03Õ 
								 //(excluding the amounts for account summary codes 965,966,967,968,969), Ô16Õ and Ô88Õ for the account.
	
	

	/**
	 * Basic constructor
	 */
	public NaiAccountTrailer(long accountTotalA,long accountTotalB) {
		super(Bai2RecordCode.ACCOUNT_TRAILER);
		this.accountTotalA = accountTotalA;
		this.accountTotalB = accountTotalB;
	}
	


	/**
	 * Construct record from a line
	 * @param line
	 * @throws Exception
	 */
	public NaiAccountTrailer(String line) throws Exception{
		super(Bai2RecordCode.ACCOUNT_TRAILER);
		if(!line.endsWith("/")){
			throw new Exception("Line has bad format, didn't find / on the end: " + line);
		}

		String[] parts = line.split(","); //Split the line on ,
		String[] fields;
		if(line.endsWith(",/")){
			//This line ends with en empty value ,,/
			//Just copy all but last part (last one is the /\n)
			fields = new String[parts.length-1];
			for(int i=0; i<parts.length-1; i++){
				fields[i] = parts[i];
			}
		}else{
			//The line ends with a value/\n
			parts[parts.length-1] = parts[parts.length-1].replace("/","");
			fields = parts;
		}
		
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[0]));
		if(thisCode != Bai2RecordCode.ACCOUNT_TRAILER){
			throw new Exception("Can't create type " + thisCode + " using Account Trailer Constructor.");
		}
		
		this.accountTotalA = Long.parseLong(parts[1]);
		this.accountTotalB = Long.parseLong(parts[2]);
		
	}
	


	@Override
	public String generateRecord() throws Exception {
		
		String out = this.code.value() + "," + this.accountTotalA + "," + this.accountTotalB + "/\n";
		return out;
		
	}

	public void setAccountTotalA(String accountTotalA) {
		this.accountTotalA = Long.parseLong(accountTotalA);
	}

	public long getAccountTotalA() {
		return accountTotalA;
	}

	public void setAccountTotalB(String accountTotalB) {
		this.accountTotalB = Long.parseLong(accountTotalB);
	}

	public long getAccountTotalB() {
		return accountTotalB;
	}
	
	public void setAccountTotalA(long accountTotalA) {
		this.accountTotalA = accountTotalA;
	}

	public void setAccountTotalB(long accountTotalB) {
		this.accountTotalB = accountTotalB;
	}

}
