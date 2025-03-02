package com.payyourself.registration;

import java.io.IOException;
import java.net.URISyntaxException;


import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.payyourself.email.SendEmail;


public class RegistrationBean {
	
	private RegistrationInfo info;
	private boolean registrationConfirmed;
	private Logger log;
	
	public RegistrationBean(){
		this.info = new RegistrationInfo();
		this.registrationConfirmed = false;
	}
	
	
	/**
	 * Check new user details and send confirmation email
	 * @return
	 */
	public String register(){
		
		this.log.info("Registering new user and sending email.");
		
		RegistrationInfoHibernation rih = new RegistrationInfoHibernation();
	
		rih.persist(this.info);
		
		//Create an email and send it
		String body = "Dear " + this.info.getFirstname() + ",<br> Welcome to Pay Yourself.  Please click the following link to acitivate you account.<br>";
		body = body + "<a href='http://localhost:8080/PayYourself/app/registration_area/confirm_registration?confirm=" + this.info.getRegistrationid() + "*" + this.info.getUsername();
		body = body + "'>Activate Account</a><br>Thank You,<br>Pay Yourself Team";
		String from = "pyRegistration@terrypacker.com";
		String subject = "Pay Yourself Registration.";

		SendEmail email = new SendEmail(from,this.info.getEmail(),subject,body);
		
		try {
			email.sendEmail();
		} catch (MessagingException e) {
			this.log.error("Problem sending confirmation email.", e);
			this.registrationConfirmed = false;
			return "register";
		} catch (URISyntaxException e) {
			this.log.error("Problem sending confirmation email.", e);
			this.registrationConfirmed = false;
			return "register";
		} catch (IOException e) {
			this.log.error("Problem sending confirmation email.", e);
			this.registrationConfirmed = false;
			return "register";
		}
		
		this.registrationConfirmed = true;
		
		return "register";
	}


	public RegistrationInfo getInfo() {
		return info;
	}


	public void setInfo(RegistrationInfo info) {
		this.info = info;
	}


	public boolean isRegistrationConfirmed() {
		return registrationConfirmed;
	}


	public void setRegistrationConfirmed(boolean registrationConfirmed) {
		this.registrationConfirmed = registrationConfirmed;
	}

}
