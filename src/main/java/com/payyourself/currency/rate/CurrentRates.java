package com.payyourself.currency.rate;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;


import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;

public class CurrentRates {
	List<PyCurrencyType> rates; //Base USD
	PyCurrencyType baseCurrency;


	public CurrentRates() throws Exception{

		this.rates = new ArrayList<PyCurrencyType>();
		
		
		PyCurrencyTypeHibernation currencyh = new PyCurrencyTypeHibernation();
		this.baseCurrency = new PyCurrencyType(currencyh.getBaseCurrency());
		
		/* Load most current rate info */
		this.loadCurrentRates();
		
	}

	/**
	 * Need to implement to load rate info from Online Resource
	 * 
	 */
	private void loadCurrentRates(){
		
		//TODO This is now wrong, should get a rate specific to the type
		PyCurrencyTypeHibernation currencyh = new PyCurrencyTypeHibernation();
	
		this.setRates(currencyh.copyAllTypes());
		
		/*Convert each using the baseCurrency */
		for(int i=0; i<this.rates.size(); i++){
			this.rates.get(i).setRateToBase((this.rates.get(i).getRateToBase()*10000)/this.baseCurrency.getRateToBase());
		}

	}
	
	public void changeBaseCurrency(ActionEvent event){
		
		this.loadCurrentRates();

	}
	

	
	public PyCurrencyType getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(PyCurrencyType baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	
	public List<PyCurrencyType> getRates() {
		return rates;
	}

	public void setRates(List<PyCurrencyType> rates) {
		this.rates = rates;
	}



	
	
}
