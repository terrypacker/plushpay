package com.payyourself.banking.au.nab.directEntry;

import java.util.ArrayList;
import java.util.List;

import com.payyourself.banking.au.nab.directEntry.records.NabDescriptiveRecord;
import com.payyourself.banking.au.nab.directEntry.records.NabDetailRecord;
import com.payyourself.banking.au.nab.directEntry.records.NabFileTotalRecord;

public class NabDirectEntryFile {

	private NabDescriptiveRecord description; //Start record (Maybe not required)
	private List<NabDetailRecord> details; //
	private NabFileTotalRecord totalRecord; //End record
	
	
	
	public NabDirectEntryFile(){
		
		this.details = new ArrayList<NabDetailRecord>();
		
	}
	
	
	public void addDetail(NabDetailRecord detail){
		this.details.add(detail);
	}
	
	/**
	 * Generate the file as a string
	 * @return
	 * @throws Exception
	 */
	public String generateFile() throws Exception{
		
		String out;
		if(this.description != null){
			out = this.description.getRecord() + "\n";
		}else{
			out = "";
		}
		
		for(int i=0; i<this.details.size(); i++){
			out = out + this.details.get(i).getRecord() + "\n";
		}
		
		if(this.totalRecord != null)
			out = out + this.totalRecord.getRecord() + "\n";
		return out;
	}


	public NabDescriptiveRecord getDescription() {
		return description;
	}


	public void setDescription(NabDescriptiveRecord description) {
		this.description = description;
	}


	public List<NabDetailRecord> getDetails() {
		return details;
	}


	public void setDetails(List<NabDetailRecord> details) {
		this.details = details;
	}


	public NabFileTotalRecord getTotalRecord() {
		return totalRecord;
	}


	public void setTotalRecord(NabFileTotalRecord totalRecord) {
		this.totalRecord = totalRecord;
	}
	
}
