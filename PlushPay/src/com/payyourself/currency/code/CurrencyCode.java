package com.payyourself.currency.code;

import com.payyourself.enumeration.EnumUserType;

/**
 * Used to simplify the ENUM issue in Hibernate
 * 
 * @author tpacker
 *
 */
public class CurrencyCode extends EnumUserType<CurrencyCodeEnum>{

	public CurrencyCode() {
		super(CurrencyCodeEnum.class);
		
	}

}
