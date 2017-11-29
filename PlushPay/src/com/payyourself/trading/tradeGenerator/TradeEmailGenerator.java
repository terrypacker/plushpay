package com.payyourself.trading.tradeGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.payyourself.currency.CurrencyValueConverter;
import com.payyourself.email.Email;
import com.payyourself.email.EmailFactory;
import com.payyourself.log.LogfileFactory;
import com.payyourself.trading.trade.Trade;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;


public class TradeEmailGenerator implements Runnable{
	
	private static boolean SEND_MAIL = false;
	private Trade trade;
	private Logger log;
	
	/**
	 * Send emails about the trade to all parties involved
	 * 
	 * @param trade
	 */
	public TradeEmailGenerator(Trade trade){
		
		this.trade = trade;
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		
		
	}

	/**
	 * Start a thread to send emails.
	 */
	public void sendEmails() {
		
		
		if(SEND_MAIL){
			Thread mailThread = new Thread(this);
			mailThread.start();
		}
		
	}

	private Email createEmail(Trade trade, Trader trader) {
		
		CurrencyValueConverter converter = new CurrencyValueConverter();
		
		String from = "trading@payyourself.com";
 		String to = trader.getUser().getEmail();
		String subject = "Please Deposit Funds for Trade: " + trade.getTradeId();
		String body = "<html><body><h1>Please Deposit Funds</h1>";
		
		body = body + "<hr></hr>";
		body = body + "Buying: "  + converter.getAsString(null, null, trader.getCurrencyToBuy()) + "<br>";
		body = body + "Selling: " + converter.getAsString(null, null, trader.getCurrencyToSell()) + "<br>";
		
		for(int i=0; i<trader.getBeneficiaries().size(); i++){
			body = body + "Beneficiary " + trader.getBeneficiaries().get(i).getBeneficiary().getName()
					+ " will get: ";
			body = body + converter.getAsString(null, null,trader.getBeneficiaries().get(i).getAmount()) + "<br>";
		}
		body = body + "</body></html>";
		
		Email newEmail = new Email(from,to,subject,body);
		
		return newEmail;
		
	}

	/**
	 * Run a separate thread that sends emails
	 */
	public void run() {
		try {
			TraderHibernation th = new TraderHibernation();
			
			List<Trader> buyers = th.getTradersInGroup(this.trade.getBuyerGroup().getGroupid());
			List<Trader> sellers = th.getTradersInGroup(this.trade.getSellerGroup().getGroupid());
			
			//For each trader, generate an email
			for(int i=0; i<buyers.size(); i++){
				Email newEmail = this.createEmail(this.trade,buyers.get(i));
				EmailFactory.sendHTMLEmail(newEmail);
			}

			for(int i=0; i<sellers.size(); i++){
				Email newEmail = this.createEmail(this.trade,sellers.get(i));
				EmailFactory.sendHTMLEmail(newEmail);
			}
		} catch (MessagingException e) {
			this.log.error("Unable to send message",e);
		} catch (URISyntaxException e) {
			this.log.error("Unable to send message",e);
		} catch (IOException e) {
			this.log.error("Unable to send message",e);
		}
		
	}

}
