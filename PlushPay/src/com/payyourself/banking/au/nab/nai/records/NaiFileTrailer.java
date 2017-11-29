package com.payyourself.banking.au.nab.nai.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.banking.bai2.records.Bai2Record;

/**
 * Record type 99
 * @author tpacker
 *
 */
public class NaiFileTrailer extends Bai2Record {




	private long fileTotalA; //	The sum of the Group control totals A in all  Group Trailer (record type Ô98Õ) records in this file.
	private int numberOfGroups; //	The number of groups in this file. That is, the number of Group header (record type Ô02Õ) records in this file.
	private int numberOfRecords; //	The total number of records in this file. This includes the File header and File trailer records but excludes any device-oriented or job control records
	private long fileTotalB; //	The sum of the Group control totals B in all Group Trailer (record type Ô98Õ) records in this file.
	
	
	
	
	public NaiFileTrailer(long fileTotalA,
			int numberOfGroups, int numberOfRecords, long fileTotalB) {
		super(Bai2RecordCode.FILE_TRAILER);
		this.fileTotalA = fileTotalA;
		this.numberOfGroups = numberOfGroups;
		this.numberOfRecords = numberOfRecords;
		this.fileTotalB = fileTotalB;
	}


	public NaiFileTrailer(String line) throws Exception{
		super(Bai2RecordCode.FILE_TRAILER);
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
		if(thisCode != Bai2RecordCode.FILE_TRAILER){
			throw new Exception("Can't create type " + thisCode + " using File Trailer Constructor.");
		}
		
		this.fileTotalA = Long.parseLong(fields[1]);
		this.numberOfGroups = Integer.parseInt(fields[2]);
		this.numberOfRecords = Integer.parseInt(fields[3]);
		this.fileTotalB = Long.parseLong(fields[4]);
		
	}


	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + "," + this.fileTotalA + "," +
			this.numberOfGroups + "," + this.numberOfRecords + "," +
			this.fileTotalB + "/\n";
		return out;
	}




	public long getFileTotalA() {
		return fileTotalA;
	}




	public void setFileTotalA(String fileTotalA) {
		this.fileTotalA = Long.parseLong(fileTotalA);
	}




	public int getNumberOfGroups() {
		return numberOfGroups;
	}




	public void setNumberOfGroups(String numberOfGroups) {
		this.numberOfGroups = Integer.parseInt(numberOfGroups);
	}




	public int getNumberOfRecords() {
		return numberOfRecords;
	}




	public void setNumberOfRecords(String numberOfRecords) {
		this.numberOfRecords = Integer.parseInt(numberOfRecords);
	}




	public long getFileTotalB() {
		return fileTotalB;
	}




	public void setFileTotalB(String fileTotalB) {
		this.fileTotalB = Long.parseLong(fileTotalB);
	}
	
	public void setFileTotalA(long fileTotalA) {
		this.fileTotalA = fileTotalA;
	}


	public void setNumberOfGroups(int numberOfGroups) {
		this.numberOfGroups = numberOfGroups;
	}


	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}


	public void setFileTotalB(long fileTotalB) {
		this.fileTotalB = fileTotalB;
	}


	

}
