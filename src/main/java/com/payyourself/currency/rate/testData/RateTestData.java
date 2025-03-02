package com.payyourself.currency.rate.testData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.payyourself.currency.type.PyCurrencyType;

/**
 * Class to allow rate data to be loaded into DB
 * @author tpacker
 *
 */
public class RateTestData {
	
	private List<RateTestDataEntry> data;
	
	public RateTestData(){
		this.data = new ArrayList<RateTestDataEntry>();
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
	 * format: TICKER,YYYYMMDD,TIME(0-240000),OPEN,HIGH,LOW,CLOSE\n
	 * @param line
	 * @return
	 * @throws ParseException
	 */
	private RateTestDataEntry parseLine(String line) throws ParseException{
		
		String[] parts = line.split(",");
		
		String ticker = parts[0];
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		DecimalFormat decimalFormat = new DecimalFormat("000000");
		String time = decimalFormat.format(Integer.parseInt(parts[2]));
		Calendar date = Calendar.getInstance();
		String dateString = parts[1] + time;
		date.setTime(sdf.parse(dateString));
		
		float open = Float.parseFloat(parts[3]);
		
		float high = Float.parseFloat(parts[4]);
		
		float low = Float.parseFloat(parts[5]);
		
		float close= Float.parseFloat(parts[6]);
		
		return new RateTestDataEntry(ticker, date, open, high,
			low, close);
		
	}


	public List<RateTestDataEntry> getData() {
		return this.data;
	}
	
	
	
	
}
