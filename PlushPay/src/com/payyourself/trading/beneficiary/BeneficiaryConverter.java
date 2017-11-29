package com.payyourself.trading.beneficiary;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;

/**
 * Convert a Beneficiary to and from string/obj representation.
 * @author tpacker
 *
 */
public class BeneficiaryConverter implements Converter {

	/**
	 * Convert "currencyId value basevalue type"
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {

		
		BeneficiaryHibernation bh = new BeneficiaryHibernation();
		return bh.findById(Long.parseLong(arg2));
	}

	
	/**
	 * Convert "currencyId value basevalue type"
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		
		Beneficiary bene = (Beneficiary)arg2;
		
		String trd = new String();

		if(bene != null){
			trd = bene.getBeneficiaryId() + "";
		}
		return trd;
	}

}
