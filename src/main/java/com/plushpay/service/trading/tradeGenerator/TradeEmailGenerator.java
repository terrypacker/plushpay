package com.plushpay.service.trading.tradeGenerator;

import com.plushpay.repository.trading.trade.Trade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class TradeEmailGenerator {

    private Log log = LogFactory.getLog(getClass());
    private static boolean SEND_MAIL = false;
    private Trade trade;


    /**
     * Send emails about the trade to all parties involved
     *
     * @param trade
     */
    public TradeEmailGenerator(Trade trade) {
        this.trade = trade;
    }

    /**
     * Start a thread to send emails.
     */
    public void sendEmails() {
        //No Op for now
    }

}
