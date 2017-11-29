package com.payyourself.currency.rate;

import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;


public class CurrencyRateConverter implements Converter{
	/**
	 * Convert a float * 10000 to a long
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {

		float value = Float.valueOf(arg2);
		value = value * 10000;		
		return (long)value;
	}

	
	/**
	 * Convert a Long to a formatted String with 4 decimal places
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		
		if(arg2 == null){
			return null;
		}
		
		//Should check for arg2 to be of type CurrencyType
		
		long value = (Long)arg2;
		
		//DecimalFormat out = new DecimalFormat(currency.getType().getSymbol() + "###,##0.00 " + currency.getType().getCode());
		DecimalFormat out = new DecimalFormat("###,##0.0000 ");
		return out.format(value/10000f);
	}

}
