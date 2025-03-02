package com.payyourself.currency;

import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.hibernate.Session;


public class CurrencyConverter implements Converter{
	/**
	 * Convert "currencyId type ratetobase"
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {

		PyCurrencyHibernation curh = new PyCurrencyHibernation();
		Session session = curh.getSessionFactory().getCurrentSession();
		//session.beginTransaction();
		PyCurrency currency = (PyCurrency)session.get(PyCurrency.class, arg2);
		//CurrencyType currency = new CurrencyType();
		//currency.setSymbol(arg2);
				
		return currency;
	}

	
	/**
	 * Convert "currencyId type ratetobase"
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		//Should be or |(arg2.getClass()!=PyCurrency.class) but javaassist thing fucks it?
		if((arg2 == null)){
			return null;
		}
		
		//Should check for arg2 to be of type CurrencyType
		
		PyCurrency currency = (PyCurrency)arg2;
		
		DecimalFormat out = new DecimalFormat(currency.getType().getSymbol() + "###,##0.00 " + currency.getType().getCode());
		
		return out.format(currency.getValue()/10000f);
	}

}
