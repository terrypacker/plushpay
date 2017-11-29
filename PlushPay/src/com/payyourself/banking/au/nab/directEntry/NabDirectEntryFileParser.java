package com.payyourself.banking.au.nab.directEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.payyourself.banking.au.nab.directEntry.records.NabDescriptiveRecord;
import com.payyourself.banking.au.nab.directEntry.records.NabDetailRecord;
import com.payyourself.banking.au.nab.directEntry.records.NabDirectEntryRecordType;
import com.payyourself.banking.au.nab.directEntry.records.NabFileTotalRecord;

public class NabDirectEntryFileParser {

	private List<NabDirectEntryFile> files;
	
	public NabDirectEntryFileParser(){
		this.files = new ArrayList<NabDirectEntryFile>();
	}
	
	public void parseInputFile(File file) throws Exception{
		 
		FileReader reader = new FileReader(file);
		BufferedReader bufReader = new BufferedReader(reader);
		
		//Load in the data (should be one record per line)
		List<String> lines = new ArrayList<String>();

		//Read in the file
		while(bufReader.ready()){
			lines.add(bufReader.readLine());

		}
		
		bufReader.close();
		reader.close();
		
		NabDirectEntryFile newFile = new NabDirectEntryFile();
		
		//Create the File(s)
		for(int i=0; i<lines.size(); i++){
			
			NabDirectEntryRecordType type = NabDirectEntryRecordType.parseCode(Integer.parseInt(lines.get(i).substring(0,1)));
			
			switch(type){
			case DESCRIPTIVE: //Start of a file record 
				newFile = new NabDirectEntryFile();
				NabDescriptiveRecord desc = new NabDescriptiveRecord(lines.get(i));
				newFile.setDescription(desc);
				break;
			case DETAIL:
				NabDetailRecord detail = new NabDetailRecord(lines.get(i));
				newFile.addDetail(detail);
				
				break;
			case TOTAL:
				NabFileTotalRecord total = new NabFileTotalRecord(lines.get(i));
				newFile.setTotalRecord(total);
				this.files.add(newFile);
				newFile = null;
				break;
			}//end case
			
		}//end for
		
		//In case there was no total record at end, we should add the file anyway
		if(newFile != null){
			this.files.add(newFile);
		}
	
	}
	
	
	public List<NabDirectEntryFile> getFiles(){
		return this.files;
	}
	
	
	
}
