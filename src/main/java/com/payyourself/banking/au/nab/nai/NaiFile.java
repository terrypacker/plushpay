package com.payyourself.banking.au.nab.nai;

import java.util.List;

import com.payyourself.banking.au.nab.nai.records.NaiFileHeader;
import com.payyourself.banking.au.nab.nai.records.NaiTransactionDetail;

/**
 * Container for Nai File Information
 * @author tpacker
 *
 */
public class NaiFile {

	private NaiFileHeader header;
	
	public NaiFile(NaiFileHeader header){
		this.setHeader(header);
	}

	public void setHeader(NaiFileHeader header) {
		this.header = header;
	}

	public NaiFileHeader getHeader() {
		return header;
	}

	public String generateFile() throws Exception{
		return this.header.generateRecord();
	}

	
	/**
	 * List all credits into the account with accountNumber
	 * @param accountNumber
	 * @return
	 */
	public List<NaiTransactionDetail> getCredits(String accountNumber) {
		
		List<NaiTransactionDetail> details = this.header.getCredits(accountNumber);
		
		return details;
		
	}
}
