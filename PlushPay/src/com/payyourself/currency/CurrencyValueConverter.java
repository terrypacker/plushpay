package com.payyourself.currency;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;

public class CurrencyValueConverter implements Converter{
	/**
	 * Convert a String rep of currency to an Object
	 * 
	 * symbol value code
	 * 
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {

		String[] currency = arg2.split(" ");
		PyCurrency newCurrency = new PyCurrency();
		PyCurrencyType newType = new PyCurrencyType();
		newType.setSymbol(currency[0]);
		newType.setCode(CurrencyCodeEnum.valueOf(currency[2]));
		newCurrency.setType(newType);

		//Convert the Value
		DecimalFormat out = new DecimalFormat("###,##0.00 ");
		Number value;
		try {
			value = out.parse(currency[1]);
			newCurrency.setValue(value.longValue());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newCurrency;
	}

	
	/**
	 * Convert a PyCurrency Object to a string
	 * 
	 * symbol value code
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		
		if(arg2 == null){
			return null;
		}
		
		//Should check for arg2 to be of type CurrencyType
		
		PyCurrency currency = (PyCurrency)arg2;
		//DecimalFormat out = new DecimalFormat(currency.getType().getSymbol() + "###,##0.00 " + currency.getType().getCode());
		DecimalFormat out = new DecimalFormat("###,##0.00 ");
		String output = currency.getType().getSymbol();
		
		output  = output + " " + out.format(currency.getValue()/10000f);
		output = output + " " + currency.getType().getCode();
		return output;
	}

}
