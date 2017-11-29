package com.payyourself.validator;


import javax.faces.validator.*;
import javax.faces.application.*;
import javax.faces.component.*;
import javax.faces.context.*;
import java.util.regex.*;

public class EmailValidator implements Validator {

	public EmailValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent,
			Object value) throws ValidatorException {

		String enteredEmail = (String) value;

		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(enteredEmail);
		boolean matchFound = m.matches();

		if (!matchFound) {
			FacesMessage message = new FacesMessage();
			message.setSummary("The email address you have entered is invalid");
			throw new ValidatorException(message);
		}

	}
}