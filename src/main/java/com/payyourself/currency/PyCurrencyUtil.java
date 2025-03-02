package com.payyourself.currency;


import com.payyourself.currency.type.PyCurrencyType;

/**
 * To convert between currencies
 * 
 * (1AUD*1USD)/1AUD = 1USD
 * 
 * 
 * 
 * 
 * @author tpacker
 *
 */
public class PyCurrencyUtil {

	/**
	 * Convert Currency
	 * toTypeAmount/toTypeRate = fromTypeAmount/fromTypeRate
	 * 
	 * toTypeAmount = (fromTypeAmount/fromTypeRate)*toTypeRate
	 * @param amount
	 * @param fromType
	 * @param toType
	 * @return
	 */
	public static PyCurrency createCurrency(long amount, PyCurrencyType fromType,
			PyCurrencyType toType) {
		
		//Above calculation
		long newAmount = amount * fromType.getRateToBase();
		newAmount = newAmount/(toType.getRateToBase());
		
		return new PyCurrency(newAmount,toType);
	}
	
	
	/**
	 * Create a new currency that is rounded to the 
	 * cents value. (Making the 2 extra places used in the system
	 *  = 00)
	 * @param currency
	 * @return
	 */
	public static PyCurrency roundCurrency(PyCurrency currency){
		
		return new PyCurrency(100*(currency.getValue()/100),currency.getType());
		
	}
	
	
}
