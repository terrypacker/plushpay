package com.payyourself.currency;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;

/**
 * Convert a PyCurrency to and from string/obj representation.
 * @author tpacker
 *
 */
public class PyCurrencyConverter implements Converter {

	//TODO this class is garbage, need to rethink the getAsObject method
	/**
	 * Convert "currencyId value typeId"
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {
		String cyparts[] = arg2.split(" ");
		PyCurrency currency = new PyCurrency();
		
		long currencyId = Long.decode(cyparts[0]);
		currency.setCurrencyId(currencyId);
		
		long value = Long.parseLong(cyparts[1]);
		currency.setValue(value);
		
		PyCurrencyTypeHibernation currencyh = new PyCurrencyTypeHibernation();
	    PyCurrencyType type = currencyh.getType(Long.parseLong(cyparts[2]));		
		currency.setType(type);
		
		return currency;
	}

	
	/**
	 * Convert "currencyId value typeId"
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		
		//TODO Test this method
		PyCurrency type = (PyCurrency)arg2;
		String cur = new String();
		cur = type.getCurrencyId() + " " + type.getValue() + " "  + type.getType().getId(); 
		return cur;
	}

}
