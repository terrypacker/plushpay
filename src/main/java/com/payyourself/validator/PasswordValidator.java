package com.payyourself.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;






public class PasswordValidator implements Validator {

		public PasswordValidator() {
		}

		public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        
			String passwordId = (String) component.getAttributes().get("passwordId");
			UIInput passwordInput = (UIInput) facesContext.getViewRoot().findComponent(passwordId);
			UIInput confirmInput = (UIInput) component;
			String password = (String) passwordInput.getValue();
			String confirm = (String) value;

			if (!confirm.equals(password)) {

            // Reset value of the both fields so that user is forced to retype passwords.
				passwordInput.resetValue();
				confirmInput.resetValue();
				
				FacesMessage message = new FacesMessage();
				message.setSummary("The passwords you entered do not match");
				throw new ValidatorException(message);

                       
            
        }
			if ((confirm.equals(password))&& password.length()<8 && password.length()>45) {

	            // Reset value of the both fields so that user is forced to retype passwords.
					passwordInput.resetValue();
					confirmInput.resetValue();
					
					FacesMessage message = new FacesMessage();
					message.setSummary("Password must be between 8 & 45 characters and may contain no spaces");
					throw new ValidatorException(message);

	                       
	            
	        }
			
    }

}
