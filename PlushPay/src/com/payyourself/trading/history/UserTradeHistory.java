package com.payyourself.trading.history;

import java.util.List;

import javax.faces.context.FacesContext;

import com.payyourself.trading.trade.Trade;
import com.payyourself.trading.trade.TradeHibernation;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;
import com.payyourself.userManagement.user.User;
import com.payyourself.userManagement.user.UserHibernation;

/**
 * Contains the trade history for the current user
 * 
 * @author tpacker
 *
 */
public class UserTradeHistory {

	private List<Trader> trades;
	
	public UserTradeHistory(){
		
		this.createHistory();
	}
	
	
	
	/**
	 * Load all trade info from Database
	 * @return
	 */
	public String createHistory(){
		

		//Get current user to find Trades
		String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		UserHibernation userh = new UserHibernation();
		User user = userh.findById(username);
		
		//Collect trades
		TraderHibernation th = new TraderHibernation();
		this.setTrades(th.findTradersWithUser(user));
		
		
		return "trade_history";
	}



	private void setTrades(List<Trader> trades) {
		this.trades = trades;
	}



	public List<Trader> getTrades() {
		return trades;
	}




	
}
