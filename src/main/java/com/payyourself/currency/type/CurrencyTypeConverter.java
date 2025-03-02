package com.payyourself.currency.type;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.hibernate.Session;

import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;

/**
 * Convert a PyCurrency to and from string/obj representation.
 * 
 * @author tpacker
 *
 */
public class CurrencyTypeConverter implements Converter {

	//TODO fix this class its garbage
	
	/**
	 * Convert "currencyId type ratetobase"
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {

		PyCurrencyTypeHibernation curh = new PyCurrencyTypeHibernation();
		Session session = curh.getSessionFactory().getCurrentSession();
		//session.beginTransaction();
		PyCurrencyType currency = (PyCurrencyType)session.get(PyCurrencyType.class, arg2);
		//CurrencyType currency = new CurrencyType();
		//currency.setSymbol(arg2);
				
		return currency;
	}

	
	/**
	 * Convert "currencyId type ratetobase"
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		
		if(arg2 == null){
			return null;
		}
		
		//Should check for arg2 to be of type CurrencyType
		
		PyCurrencyType type = (PyCurrencyType)arg2;
		String cur = new String();
		cur = type.getCode().name();
		return cur;
	}

}
