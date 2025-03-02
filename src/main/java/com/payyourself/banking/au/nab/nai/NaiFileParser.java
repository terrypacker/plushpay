package com.payyourself.banking.au.nab.nai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.payyourself.banking.au.nab.nai.records.NaiAccountSummary;
import com.payyourself.banking.au.nab.nai.records.NaiAccountTrailer;
import com.payyourself.banking.au.nab.nai.records.NaiFileHeader;
import com.payyourself.banking.au.nab.nai.records.NaiFileTrailer;
import com.payyourself.banking.au.nab.nai.records.NaiGroupHeader;
import com.payyourself.banking.au.nab.nai.records.NaiGroupTrailer;
import com.payyourself.banking.au.nab.nai.records.NaiTransactionDetail;
import com.payyourself.banking.bai2.codes.Bai2RecordCode;


/**
 * Parse a file in the NAI Format
 * 
 * 
 * 
 * @author tpacker
 *
 */
public class NaiFileParser {
	
	private List<NaiFile> files;
	
	
	public NaiFileParser(){
		this.files = new ArrayList<NaiFile>();
	}
	
	/**
	 * Parse an NAI file.
	 * 
	 * @param file
	 * @throws Exception
	 */
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
		
		
		//Now break up the file by parts
		
		int i=0;
		String[] fields; //To detect Record code of line
		Bai2RecordCode code; //Line's Code
		
		//Temp records for file
		NaiFile naiFile = null;
		NaiGroupHeader groupHeader = null; //
		NaiFileHeader fileHeader = null; //
		NaiAccountSummary accountSummary = null;
		NaiTransactionDetail transactionDetail = null;
		NaiAccountTrailer accountTrailer = null; //
		NaiGroupTrailer groupTrailer = null;
		NaiFileTrailer fileTrailer = null; 
		
		while(i < lines.size()){
			
			fields = lines.get(i).split(","); //Split the line to find code
			code = Bai2RecordCode.parseCode(Integer.parseInt(fields[0]));
			
			switch(code){
			
			case FILE_HEADER:
				
				//Create a reference to a file header that 
				// will contain all file data.
				fileHeader = new NaiFileHeader(lines.get(i));	
				naiFile = new NaiFile(fileHeader);
				
				this.files.add(naiFile); //Add it to the list
				
				i++;
				break;
			case GROUP_HEADER:
				//Line 2 is the Group Header
				groupHeader = new NaiGroupHeader(lines.get(1));
				
				//Create a new NaiGroup Here and add it to the open NaiFile
				fileHeader.addGroup(groupHeader);
				
				i++;
				break;
			case ACCOUNT_SUMMARY:
				//Line 3 is an Account summary with possible 88 codes.
				List<String> accountSummaryLines = new ArrayList<String>();
				accountSummaryLines.add(lines.get(i)); //Add the 03 code
				
				i++; //Move to next line
				while(lines.get(i).startsWith("88")){
					accountSummaryLines.add(lines.get(i)); //Add all 88 codes 
					i++;
				}
				//Create a new NaiAccountSummary and add it to the open group
				accountSummary = new NaiAccountSummary(accountSummaryLines);
				groupHeader.addAccount(accountSummary);
				
				
				break;
			case TRANSACTION_DETAIL:
				//Line 3 is an Account summary with possible 88 codes.
				List<String> transactionDetailLines = new ArrayList<String>();
				transactionDetailLines.add(lines.get(i)); //Add the 03 code
				
				i++; //Move to next line
				while(lines.get(i).startsWith("88")){
					transactionDetailLines.add(lines.get(i)); //Add all 88 codes 
					i++;
				}
				//Create a transactionDetail and add it to the open account
				transactionDetail = new NaiTransactionDetail(transactionDetailLines);
				accountSummary.addTransaction(transactionDetail);
				
				break;
			case ACCOUNT_TRAILER:
				accountTrailer = new NaiAccountTrailer(lines.get(i));
				
				//Add the account Trailer to the open account
				accountSummary.setTrailer(accountTrailer);
				
				i++;
				break;
			case GROUP_TRAILER:
				groupTrailer = new NaiGroupTrailer(lines.get(i));
				
				//Add the group trailer to the open group
				groupHeader.setTrailer(groupTrailer);
				
				i++;
				break;
			case FILE_TRAILER:
				fileTrailer = new NaiFileTrailer(lines.get(i));
				
				//Add the file trailer to the open file.
				fileHeader.setTrailer(fileTrailer);
				i++;
				break;
			
			}

		}
		
	}
	
	/**
	 * Generate the file
	 * @return
	 * @throws Exception
	 */
	public String generateFile() throws Exception{
		
		String out = "";
		for(int i=0; i<this.files.size(); i++){
			out = out + this.files.get(i).generateFile();
		}
		return out;
	}
	
	
	public List<NaiFile> getFiles(){
		return this.files;
	}
	
	 public static void main(String[] args) throws IOException{
		 
		 ClassLoader loader = Thread.currentThread().getContextClassLoader();
		 
		 
		 try {
			 
			 String path = loader.getResource("com/payyourself/banking/nab/nai/test.nai").toURI().getPath();

			//Load the input
			File file = new File(path);

			System.out.println("Parsing File...");
			NaiFileParser parser = new NaiFileParser();
			parser.parseInputFile(file);
			
			System.out.println("Re-creating file...");
			System.out.println(parser.generateFile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
	 }
	
	
}
