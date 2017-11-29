package com.payyourself.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class FloatingPointValidator implements Validator {

	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		
		//Looking for a floating point number
		try{
			Float.valueOf((Float)arg2);
		}catch(Exception e){
			FacesMessage message = new FacesMessage();
			message.setSummary("The number entered is invalid.");
			throw new ValidatorException(message);
		}
	}

}
