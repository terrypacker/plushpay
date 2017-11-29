package com.payyourself.validator;


import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.payyourself.registration.RegistrationInfo;
import com.payyourself.registration.RegistrationInfoHibernation;
import com.payyourself.userManagement.user.User;
import com.payyourself.userManagement.user.UserHibernation;


	public class UserNameValidator implements Validator{
	
	public UserNameValidator() throws Exception{
		
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object object) throws ValidatorException{
		String username = (String)object;
		UserHibernation uh = new UserHibernation();
		User newUser = uh.findById(username);
		
		boolean proceed = false;
		
		if(newUser == null){
			proceed = true;
		}

		/* Now check to see if they are in the RegistrationInfo table 
		 * This username could be reserved by someone who is not confirmed yet.
		 * 
		 * */
		RegistrationInfoHibernation rih = new RegistrationInfoHibernation();
		List<RegistrationInfo> users = rih.getUsingUsername(username);
		
		if(users.size()>0){
			FacesMessage message = new FacesMessage();
			message.setSummary("Username exists.");
			throw new ValidatorException(message);
		}
		

		if (!proceed) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Username exists.");
			throw new ValidatorException(message);
		}
		
		if (username.length()<8){
			FacesMessage message = new FacesMessage();
			message.setSummary("Username must be a minimum of 8 characters.");
			throw new ValidatorException(message);
			
		}

	}

}