package com.payyourself.userManagement.user;

import java.util.List;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.trading.beneficiary.Beneficiary;
import com.payyourself.trading.beneficiary.details.AudDetails;
import com.payyourself.trading.beneficiary.details.BeneficiaryDetails;
import com.payyourself.trading.beneficiary.details.UsdDetails;

public class UserPreferences {
	
	private boolean updateConfirmed;
	private User user; //Where all user prefs are stored (in DB)
	

	
	/**
	 * Default constructor to load preferences of
	 * current user from DB.
	 */
	public UserPreferences(){
		this.setUpdateConfirmed(false);
		//Get current user for trade
		String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		UserHibernation userh = new UserHibernation();
		this.setUser((User)userh.findById(username));
		
		

		
	}
	
	
	public String updatePreferences(){
		/*Flag for confirmed message on page*/
		this.setUpdateConfirmed(true);
		
		/*Commit to DB */
		UserHibernation userh = new UserHibernation();
		userh.persist(this.user);
		
		return null;
	}



	public void setUser(User user) {
		this.user = user;
	}


	public User getUser() {
		return user;
	}


	public void setUpdateConfirmed(boolean updateConfirmed) {
		this.updateConfirmed = updateConfirmed;
	}


	public boolean isUpdateConfirmed() {
		return updateConfirmed;
	}




}
