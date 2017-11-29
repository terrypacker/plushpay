package com.payyourself.banking.au.nab.nai.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.banking.bai2.records.Bai2Record;

public class NaiGroupTrailer extends Bai2Record {

	private long groupTotalA; //	The sum of the Account control totals A in all Account trailer (record type Ô49Õ) records in this group.
	private int numberOfAccounts; //	The number of accounts in this group. That is the number of Account identifier and summary status (record type Ô03Õ) records in this group.
	private long groupTotalB; //	The sum of the Account control totals B in all Account trailer (record type Ô49Õ) records in this group
	
	
	
	public NaiGroupTrailer(long groupTotalA,
			int numberOfAccounts, long groupTotalB) {
		super(Bai2RecordCode.GROUP_TRAILER);
		this.groupTotalA = groupTotalA;
		this.numberOfAccounts = numberOfAccounts;
		this.groupTotalB = groupTotalB;
	}


	public NaiGroupTrailer(String line) throws Exception{
		super(Bai2RecordCode.GROUP_TRAILER);
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
		if(thisCode != Bai2RecordCode.GROUP_TRAILER){
			throw new Exception("Can't create type " + thisCode + " using Group Trailer Constructor.");
		}
		
		this.groupTotalA = Long.parseLong(fields[1]);
		this.numberOfAccounts = Integer.parseInt(fields[2]);
		this.groupTotalB = Long.parseLong(fields[3]);
		
	}

	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + "," + this.groupTotalA + "," + this.numberOfAccounts + 
			"," + this.groupTotalB + "/\n";
		return out;
	}



	public long getGroupTotalA() {
		return groupTotalA;
	}



	public void setGroupTotalA(String groupTotalA) {
		this.groupTotalA = Long.parseLong(groupTotalA);
	}



	public int getNumberOfAccounts() {
		return numberOfAccounts;
	}



	public void setNumberOfAccounts(String numberOfAccounts) {
		this.numberOfAccounts = Integer.parseInt(numberOfAccounts);
	}



	public long getGroupTotalB() {
		return groupTotalB;
	}



	public void setGroupTotalB(String groupTotalB) {
		this.groupTotalB = Long.parseLong(groupTotalB);
	}

}
