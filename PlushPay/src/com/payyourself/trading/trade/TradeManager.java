package com.payyourself.trading.trade;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * A bean to manage trades from a webpage
 * @author tpacker
 *
 */
public class TradeManager {

	private String username;
	
	public TradeManager(){
		
	}
	
	public List<Trade> getActiveTrades(){
		List<Trade> active = new ArrayList<Trade>();
		
		return active;
	}
	
}
