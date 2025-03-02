package com.payyourself.trading.traderSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TraderTestData {

	private List<TraderTestDataEntry> data;
	
	public TraderTestData(){
		this.data = new ArrayList<TraderTestDataEntry>();
	}
	
	/**
	 * Load the data in from a file
	 * @param file
	 * @throws ParseException
	 * @throws IOException
	 */
	public void loadData(File file) throws ParseException, IOException{
		
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		
		//Burn first line, headers
		reader.readLine();
		
		//Read in the file
		while(reader.ready()){
			data.add(this.parseLine(reader.readLine()));
		}
		
		reader.close();
		fr.close();
		
		
	}
	
	/**
	 * Parse a line from a test rate file
	 * format: Date,amount(1.00)
	 * @param line
	 * @return
	 * @throws ParseException
	 */
	private TraderTestDataEntry parseLine(String line) throws ParseException{
		
		String[] parts = line.split(",");
		
		//Date is first part
		
		long amount = (long)(Float.parseFloat(parts[1])*10000);
		
		return new TraderTestDataEntry(amount);
		
	}
	
	public List<TraderTestDataEntry> getData(){
		return this.data;
	}
	
}
