package com.payyourself.banking.au.nab.nai.records;

import java.util.List;

import com.payyourself.banking.FundsType;
import com.payyourself.banking.au.nab.nai.codes.NaiTransactionCode;
import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.banking.bai2.records.Bai2Record;

/**
 * 
 * Type 16 Record
 * 
 * NAI Interface Transaction Record
 * @author tpacker
 *
 */
public class NaiTransactionDetail extends Bai2Record {

	private NaiTransactionCode transactionCode;
	private long amount; //Amount for transaction detail
	private FundsType fundsType; //Always 0 Immediate
	private String referenceNumber; //Alphanumeric field defined by originator
	private String text; //Free text with details
	
	
	/**
	 * 
	 * @param transactionCode
	 * @param amount
	 * @param referenceNumber
	 * @param text
	 */
	public NaiTransactionDetail(NaiTransactionCode transactionCode, 
			long amount,
			String referenceNumber, String text) {
		super(Bai2RecordCode.TRANSACTION_DETAIL);
		this.transactionCode = transactionCode;
		this.amount = amount;
		this.fundsType = FundsType.IMMEDIATE;
		this.referenceNumber = referenceNumber;
		this.text = text;
	}

	/**
	 * Parse the record from a list of lines.
	 * 
	 * The fist line should be a TRANSACTION_DETAIL entry
	 * the following can be continouation records 88
	 * that contain free text only.
	 * @param line
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public NaiTransactionDetail(List<String> lines) throws NumberFormatException, Exception{
		super(Bai2RecordCode.TRANSACTION_DETAIL);
	
		
		String[] parts = lines.get(0).split(","); //Split the line on ,
		String[] fields;
		if(lines.get(0).endsWith(",/\n")){
			//This line ends with en empty value ,,/
			//Just copy all but last part (last one is the /\n)
			fields = new String[parts.length-1];
			for(int i=0; i<parts.length-1; i++){
				fields[i] = parts[i];
			}
		}else{
			//The line ends with a value/\n
			//Do we have text at the end?  (no /\n just a \n)
			if(lines.get(0).endsWith("/\n")){
				parts[parts.length-1] = parts[parts.length-1].replace("/\n","");
			}else{
				//It has text so just remove the \n
				parts[parts.length-1] = parts[parts.length-1].replace("\n","");				
			}
			fields = parts;
		}
		
		//Check the code
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[0]));
		if(thisCode != Bai2RecordCode.TRANSACTION_DETAIL){
			throw new Exception("Can't create type " + thisCode + " using Transaction Detail Constructor.");
		}
		
		if(fields[1].equals("")){
			this.transactionCode = NaiTransactionCode.UNKNOWN;
		}else{
			this.transactionCode = NaiTransactionCode.parseCode(Integer.parseInt(fields[1]));
		}
		
		this.amount = Long.parseLong(fields[2]);
		
		if(fields[3].equals("")){
			this.fundsType = FundsType.UNKNOWN;
		}else{
			this.fundsType = FundsType.parseCode(fields[3]);
		}
		
		this.referenceNumber = fields[4];
		
		//Now add the text if we have some in the first line
		if(fields.length > 5){
			
			this.text= fields[5];
			
			
		}else{
			this.text = ""; //Just init it up.
		}
		
		//Now add the extra lines
		for(int i=1; i<lines.size(); i++){
			
			//Check the code
			if(lines.get(i).startsWith("88,")){
				lines.set(i, lines.get(i).replaceFirst("88,","")); //Chop off the leading record matcher
			}else{
				throw new Exception("Additional Lines for Transaction Details were not continuation records: " + lines.get(i));
			}
			
			//Just add the rest as text
			this.text = this.text + lines.get(i);
			
			
		}//end for lines
		
		
		
	}
	
	@Override
	public String generateRecord() throws Exception {
		
		String out = this.code.value() + "," + this.transactionCode.value() + "," + this.amount +
			"," + this.fundsType.value() + "," + this.referenceNumber;
		
		//Now add text if there is any and break into 88 records
		if(this.text.equals("")){
			//No text
			out = out + "/\n";
			
		}else{
			out = out + ","; //Add the separator for the text field
			//Do we need to break it up?
			if(this.text.length() + out.length() > 80){
				int pos = 0; //Position in text string
				String tmpOut = "88,";
				
				//Finish off the 16 record
				while(out.length()<78){
					out = out + this.text.charAt(pos);
					pos++;
				}
				
				out = out + "\n"; //Complete the line
				
				//Add the remaining as 88 records
				while(pos < this.text.length()){
					
					tmpOut = tmpOut +  this.text.charAt(pos);
					pos++;
					if(tmpOut.length()>=79){
						out = out + tmpOut + "\n"; //Add it as a line
						tmpOut = "88,"; //Start the new line
					}
				}
				
				//Add the remaining tmpLine
				out = out + tmpOut + "\n";
				
			}else{
				out = out + ","+ this.text + "\n"; //Terminate the line without a / indicating there is text
			}

		}
		
		return out;
		
		
	}

	public NaiTransactionCode getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(NaiTransactionCode transactionCode) {
		this.transactionCode = transactionCode;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = Long.parseLong(amount);
	}
	
	public void setAmount(long amount){
		this.amount = amount;
	}

	public FundsType getFundsType() {
		return fundsType;
	}

	public void setFundsType(FundsType fundsType) {
		this.fundsType = fundsType;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
