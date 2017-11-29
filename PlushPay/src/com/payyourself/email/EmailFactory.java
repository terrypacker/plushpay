package com.payyourself.email;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class EmailFactory {
	
	private String emailFrom;
	private String emailTo;
	private String emailSubject;
	private String emailBody;


	
	public static void sendHTMLEmail(Email email) throws MessagingException, URISyntaxException, IOException{
		

		try{
			/*Load the properties*/
	
	    	InputStream input = null;
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			input = new FileInputStream(loader.getResource("com/payyourself/email/email.properties").toURI().getPath());
			Properties emailProperties = new Properties();			
	        emailProperties.load( input );
			        
			/*Create a session using the properites.*/
	        
			Session session = Session.getDefaultInstance( emailProperties, null);
		    MimeMessage msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress(email.getFrom()));
		    
		    /*break up the email address field by ';' and removing the spaces.*/
		    
		    ArrayList<Address[]> addresses = new ArrayList<Address[]>(0);
		    String to = email.getTo();
		    while (to.indexOf(";")!=-1){
		    	
		    	String tempString;
		    	tempString = to.substring(0, to.indexOf(";")).trim();
		    	Address[] tempAddress = {new InternetAddress(tempString)};
		    	addresses.add(tempAddress);
		    	to = to.substring(to.indexOf(";")+1);
		    		    	
		    }
		    
		    /*add the last address or the only address - depending on how many there are*/
		    
		    Address[] address = {new InternetAddress(to.trim())};
		    addresses.add(address);
		    
		    for (int i = 0; i<addresses.size(); i++){
		    	//msg.addRecipients(Message.RecipientType.TO, addresses);
		    	 msg.addRecipients(Message.RecipientType.TO, addresses.get(i));
		    }
		    
		    //msg.setRecipients(Message.RecipientType.TO, address.toString());
		    msg.setSubject(email.getSubject());
		    
		    /*create and fill the first message part*/
		    
		    MimeBodyPart mbp1 = new MimeBodyPart();
		    
		    /*For HTML Display*/
		    mbp1.setContent(email.getBody(),"text/html");
		    
		    //mbp1.setText(this.emailBody); //For text Display
		    if ( input != null ) input.close();
		    
		    /*create the second message part*/
		    
		   // MimeBodyPart mbp2 = new MimeBodyPart();
	
		    /*attach the file to the message*/
		    
		  	/* For this we will need to implement a DataContentHandler TBD*/		
				    
		    //String attachmentFilename = this.reportName + ".pdf";
		    
		    //mbp2.setDataHandler(new DataHandler(new ByteArrayDataSource(baos.toByteArray(),"application/pdf")));
			//mbp2.setFileName(attachmentFilename);
		     
		    /*
		     * Use the following approach instead of the above line if
		     * you want to control the MIME type of the attached file.
		     * Normally you should never need to do this.
		     *
		    FileDataSource fds = new FileDataSource(filename) {
			public String getContentType() {
			    return "application/octet-stream";
			}
		    };
		    mbp2.setDataHandler(new DataHandler(fds));
		    mbp2.setFileName(fds.getName());
		     */
	
		    /*create the Multipart and add its parts to it */
		    Multipart mp = new MimeMultipart();
		    mp.addBodyPart(mbp1);
		    //mp.addBodyPart(mbp2);
	
		    /* add the Multipart to the message*/
		    
		    msg.setContent(mp);
	
		    /*set the Date: header*/
		    
		    msg.setSentDate(new Date());
	
	
		    /*send the message*/
		    
		    Transport.send(msg);
	    
		} catch (MessagingException mex) {
		    mex.printStackTrace();
		    Exception ex = null; //Not sure about this...
		    
		    if ((ex = mex.getNextException()) != null) {
		    	
		    	throw new MessagingException(mex.getLocalizedMessage());
		    }
		    
		}
		
	}
	

	/**
	 * @return the emailFrom
	 */
	public String getEmailFrom() {
		return emailFrom;
	}

	/**
	 * @param emailFrom the emailFrom to set
	 */
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	/**
	 * @return the emailTo
	 */
	public String getEmailTo() {
		return emailTo;
	}

	/**
	 * @param emailTo the emailTo to set
	 */
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	/**
	 * @return the emailSubject
	 */
	public String getEmailSubject() {
		return emailSubject;
	}

	/**
	 * @param emailSubject the emailSubject to set
	 */
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	/**
	 * @return the emailBody
	 */
	public String getEmailBody() {
		return emailBody;
	}

	/**
	 * @param emailBody the emailBody to set
	 */
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}


	

}
