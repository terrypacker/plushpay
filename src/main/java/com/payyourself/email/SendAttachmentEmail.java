/**
 * 
 */
package com.payyourself.email;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
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
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.payyourself.log.LogfileFactory;

/**
 * 
 * Class to send Email with an pdf attachment from any bean
 *
 */
public class SendAttachmentEmail implements ServletContextListener{
	
	private String emailFrom;
	private String emailTo;
	private String emailSubject;
	private String emailBody;
	private String reportName;
	private ByteArrayOutputStream baos;
	private Logger log;
	//private Object report;
	
	
	/**
	 * Default constructor - must pass all elements in order to send an email
	 * @param from
	 * @param to
	 * @param subject
	 * @param body
	 * @param name
	 * @param baos
	 */
	public SendAttachmentEmail(String from, String to, String subject, String body, String name, ByteArrayOutputStream baos){
		
		this.emailFrom = from;
		this.emailTo = to;
		this.emailSubject = subject;
		this.emailBody = body;
		this.baos = baos;
		this.reportName = name;
	}
	
	public void sendEmail(){
		

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
		    msg.setFrom(new InternetAddress(this.emailFrom));
		    
		    /*break up the email address field by ';' and removing the spaces.*/
		    
		    ArrayList<Address[]> addresses = new ArrayList<Address[]>(0);
		    
		    while (this.emailTo.indexOf(";")!=-1){
		    	
		    	String tempString;
		    	tempString = this.emailTo.substring(0, this.emailTo.indexOf(";")).trim();
		    	Address[] tempAddress = {new InternetAddress(tempString)};
		    	addresses.add(tempAddress);
		    	this.emailTo = this.emailTo.substring(this.emailTo.indexOf(";")+1);
		    		    	
		    }
		    
		    /*add the last address or the only address - depending on how many there are*/
		    
		    Address[] address = {new InternetAddress(this.emailTo.trim())};
		    addresses.add(address);
		    
		    for (int i = 0; i<addresses.size(); i++){
		    	//msg.addRecipients(Message.RecipientType.TO, addresses);
		    	 msg.addRecipients(Message.RecipientType.TO, addresses.get(i));
		    }
		    
		    //msg.setRecipients(Message.RecipientType.TO, address.toString());
		    msg.setSubject(this.emailSubject);
		    
		    /*create and fill the first message part*/
		    
		    MimeBodyPart mbp1 = new MimeBodyPart();
		    
		    /*For HTML Display*/
		    mbp1.setContent(this.emailBody,"text/html");
		    
		    //mbp1.setText(this.emailBody); //For text Display
		    if ( input != null ) input.close();
		    
		    /*create the second message part*/
		    
		    MimeBodyPart mbp2 = new MimeBodyPart();
	
		    /*attach the file to the message*/
		    
		  	/* For this we will need to implement a DataContentHandler TBD*/		
				    
		    String attachmentFilename = this.reportName + ".pdf";
		    
		    mbp2.setDataHandler(new DataHandler(new ByteArrayDataSource(baos.toByteArray(),"application/pdf")));
			mbp2.setFileName(attachmentFilename);
		     
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
		    mp.addBodyPart(mbp2);
	
		    /* add the Multipart to the message*/
		    
		    msg.setContent(mp);
	
		    /*set the Date: header*/
		    
		    msg.setSentDate(new Date());
	
	
		    /*send the message*/
		    
		    Transport.send(msg);
	    
		} catch (MessagingException mex) {
		    mex.printStackTrace();
		    Exception ex = null;
		    
		    if ((ex = mex.getNextException()) != null) {
		    	
		    	this.log.error("Email Send Failed: ", ex);
		    }
		    
		} catch (Exception e) {
			
			e.printStackTrace();
		}	
		
	}
	


	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Start up logger
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		
		this.log.info("Started Send Email Logger.");
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

	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * @return the baos
	 */
	public ByteArrayOutputStream getBaos() {
		return baos;
	}

	/**
	 * @param baos the baos to set
	 */
	public void setBaos(ByteArrayOutputStream baos) {
		this.baos = baos;
	}



}
