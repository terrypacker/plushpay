package com.payyourself.trading.trade;

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
public class TradeConverter implements Converter {

	/**
	 * Convert "currencyId value basevalue type"
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {

		TradeHibernation th = new TradeHibernation();
		return th.findById(Long.parseLong(arg2));
	}

	
	/**
	 * Convert "currencyId value basevalue type"
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		
		Trade trade = (Trade)arg2;
		String trd = new String();

		if(trade != null){
			trd = trade.getTradeId() + "";
		}
		return trd;
	}

}
