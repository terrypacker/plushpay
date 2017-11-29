package com.payyourself.currency.rate;

/*
 * Copyright (c) 2007 Thomas Knierim
 * http://www.thomasknierim.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.net.*;
import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;

import java.text.*;
import java.util.*;

/**
 * CurrencyConverter provides an API for accessing the European Central Bank's
 * (ECB) foreign exchange rates. The published ECB rates contain exchange rates
 * for approx. 35 of the world's major currencies. They are updated daily at
 * 14:15 CET. These rates use EUR as reference currency and are specified with a
 * precision of 1/10000 of the currency unit (one hundredth cent). See:
 *
 * http://www.ecb.int/stats/exchange/eurofxref/html/index.en.html
 *
 * The convert() method performs currency conversions using either double values
 * or 64-bit long integer values. Long values are preferred in order to avoid
 * problems associated with floating point arithmetics. A local cache file is
 * used for storing exchange rates to reduce network latency. The cache file is
 * updated automatically when new exchange rates become available. It is
 * created/updated the first time a call to convert() is made.
 *
 * @version 1.0 2008-16-02
 * @author Thomas Knierim
 *
 */
public final class RateCollector {

    private static long rateSimDeviation = 20; //.02 of a cent
    private static RateCollector instance = null;

    
    private final String ecbRatesURL = "http://www.ecb.int/stats/eurofxref/eurofxref-daily.xml";

    
    transient private File cacheFile = null;

    
    private String cacheFileName = null;

    
    private HashMap<String, Long> fxRates = new HashMap<String, Long>(40);

    
    private Date referenceDate = null;

    
    private String lastError = null;

    
    private RateCollector() {}

    /**
     * Returns a singleton instance of CurrencyConverter.
     * @return CurrencyConverter instance
     */
    public static RateCollector getInstance() {
        if (instance == null)
            instance = new RateCollector();
        return instance;
    }

    
    public List<PyCurrencyType> getRates(List<PyCurrencyType> currencies) throws IOException, ParseException{
    	
    	
    
    	
    	long rateToBase; 
    	
    	for(int i=0; i<currencies.size(); i++){
    		rateToBase = this.convert(10000, currencies.get(i).getCode(), "USD");
    		
    		//If not USD then add a little variance in the simulation
    		if(currencies.get(i).getCode() != CurrencyCodeEnum.USD){
    			rateToBase = (long) (rateToBase + (Math.random())*RateCollector.rateSimDeviation);
    		}
    		
    		currencies.get(i).setRateToBase((int)rateToBase);
    		
    	}
    	
    	
    	return currencies;
    }

    
    /**
     * Converts a double precision floating point value from one currency to
     * another. Example: convert(29.95, "USD", "EUR") - converts $29.95 US Dollars
     * to Euro.
     *
     * @param amount
     *                Amount of money (in source currency) to be converted.
     * @param fromCurrency
     *                Three letter ISO 4217 currency code of source currency.
     * @param toCurrency
     *                Three letter ISO 4217 currency code of target currency.
     * @return Amount in target currency
     * @throws IOException
     *                 If cache file cannot be read/written or if URL cannot be
     *                 opened.
     * @throws ParseException
     *                 If an error occurs while parsing the XML cache file.
     * @throws IllegalArgumentException
     *                 If a wrong (non-existing) currency argument was supplied.
     */
    public double convert(double amount, CurrencyCodeEnum fromCurrency, String toCurrency)
            throws IOException, ParseException, IllegalArgumentException {
        if (checkCurrencyArgs(fromCurrency.name(), toCurrency)) {
            amount *= fxRates.get(toCurrency);
            amount /= fxRates.get(fromCurrency);
        }
        return amount;
    }

    /**
     * Converts a long value from one currency to another. Internally long
     * values represent monetary amounts in 1/10000 of the currency unit, e.g.
     * the long value 975573l represents 97.5573 (precision = four digits after
     * comma). Using long values instead of floating point numbers prevents
     * imprecision / calculation errors resulting from floating point
     * arithmetics.
     *
     * @param amount
     *                Amount of money (in source currency) to be converted.
     * @param fromCurrency
     *                Three letter ISO 4217 currency code of source currency.
     * @param toCurrency
     *                Three letter ISO 4217 currency code of target currency.
     * @return Amount in target currency
     * @throws IOException
     *                 If cache file cannot be read/written or if URL cannot be
     *                 opened.
     * @throws ParseException
     *                 If an error occurs while parsing the XML cache file.
     * @throws IllegalArgumentException
     *                 If a wrong (non-existing) currency argument was supplied.
     */
    public long convert(long amount, CurrencyCodeEnum fromCurrency, String toCurrency)
            throws IOException, ParseException, IllegalArgumentException {
        if (checkCurrencyArgs(fromCurrency.name(), toCurrency)) {
            amount *= fxRates.get(toCurrency);
            amount /= fxRates.get(fromCurrency.name());
        }
        return amount;
    }

    /**
     * Check whether currency arguments are valid and not equal.
     *
     * @param fromCurrency
     *                ISO 4217 source currency code.
     * @param toCurrency
     *                ISO 4217 target currency code.
     * @return true if both currency arguments are not equal.
     * @throws IOException
     *                 If cache file cannot be read/written or if URL cannot be
     *                 opened.
     * @throws ParseException
     *                 If an error occurs while parsing the XML cache file.
     * @throws IllegalArgumentException
     *                 If a wrong (non-existing) currency argument was supplied.
     */
    private boolean checkCurrencyArgs(String fromCurrency, String toCurrency)
            throws IOException, ParseException, IllegalArgumentException {
        update();
        if (!fxRates.containsKey(fromCurrency))
            throw new IllegalArgumentException(fromCurrency
                    + " currency is not available.");
        if (!fxRates.containsKey(toCurrency))
            throw new IllegalArgumentException(toCurrency
                    + " currency is not available.");
        return (!fromCurrency.equals(toCurrency));
    }

    /**
     * Check whether the exchange rate for a given currency is available.
     *
     * @param currency
     *                Three letter ISO 4217 currency code of source currency.
     * @return True if exchange rate exists, false otherwise.
     */
    public boolean isAvailable(String currency) {
        return (fxRates.containsKey(currency));
    }

    /**
     * Returns currencies for which exchange rates are available.
     *
     * @return String array with ISO 4217 currency codes.
     * @throws IOException
     *                 If cache file cannot be read/written or if URL cannot be
     *                 opened.
     * @throws ParseException
     *                 If an error occurs while parsing the XML cache file.
     */
    public String[] getCurrencies() throws IOException, ParseException {
        if (fxRates.isEmpty())
            update();
        String[] currencies = fxRates.keySet().toArray(
                new String[fxRates.size()]);
        return currencies;
    }

    /**
     * Get the reference date for the exchange rates as a Java Date. The time
     * part is always 14:15 Central European Time (CET).
     *
     * @return Date for which currency exchange rates are valid, or null if the
     *         data structure has not yet been initialised.
     *
     */
    public Date getReferenceDate() {
        return referenceDate;
    }

    /**
     * Get the name of the fully qualified path name of the XML cache file. By
     * default this is a file named "ExchangeRates.xml" located in the system's
     * temporary file directory. The cache file can be shared by multiple
     * threads/applications.
     *
     * @return Path name of the XML cache file.
     */
    public String getCacheFileName() {
        return cacheFileName;
    }

    /**
     * Set the location where the XML cache file should be stored.
     *
     * @param cacheFileName
     * @see #getCacheFileName() Fully qualified path name of the XML cache file.
     */
    public void setCacheFileName(String cacheFileName) {
        this.cacheFileName = cacheFileName;
    }

    /**
     * Delete XML cache file and reset internal data structure. Calling
     * clearCache() before the convert() method forces a fresh download of the
     * currency exchange rates.
     */
    public void clearCache() {
        initCacheFile();
        cacheFile.delete();
        cacheFile = null;
        referenceDate = null;
    }

    /**
     * Check whether cache is initialised and up-to-date. If not, re-download
     * cache file and parse data into internal data structure.
     *
     * @throws IOException
     *                 If cache file cannot be read/written or if URL cannot be
     *                 opened.
     * @throws ParseException
     *                 If an error occurs while parsing the XML cache file.
     */
    private void update() throws IOException, ParseException {
        if (referenceDate == null) {
            initCacheFile();
            if (!cacheFile.exists()) {
                refreshCacheFile();
            }
            parse();
        }
        if (cacheIsExpired()) {
            refreshCacheFile();
            parse();
        }
    }

    /**
     * Initialises cache file member variable if not already initialised.
     */
    private void initCacheFile() {
        if (cacheFile == null) {
            if (cacheFileName == null || cacheFileName.equals(""))
                cacheFileName = System.getProperty("java.io.tmpdir")
                        + "ExchangeRates.xml";
            cacheFile = new File(cacheFileName);
        }
    }

    /**
     * Checks whether XML cache file needs to be updated. The cache file is up
     * to date for 24 hours after the reference date (plus a certain tolerance).
     * On weekends, it is 72 hours because no rates are published during
     * weekends.
     *
     * @return true if cache file needs to be updated, false otherwise.
     */
    private boolean cacheIsExpired() {
        final int tolerance = 12;
        if (referenceDate == null)
            return true;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long hoursOld = (cal.getTimeInMillis() - referenceDate.getTime())
                / (1000 * 60 * 60);
        int hoursValid = 24 + tolerance;
        cal.setTime(referenceDate);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
            hoursValid = 72;
        else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            hoursValid = 48; // hypothetical... rates are never published on
        // Saturdays
        if (hoursOld > hoursValid)
            return true;
        return false;
    }

    /**
     * (Re-) download the XML cache file and store it in a temporary location.
     *
     * @throws IOException
     *                 If (1) URL cannot be opened, or (2) if cache file cannot
     *                 be opened, or (3) if a read/write error occurs.
     */
    private void refreshCacheFile() throws IOException {
        lastError = null;
        initCacheFile();
        InputStreamReader in;
        FileWriter out;
        try {
            URL ecbRates = new URL(ecbRatesURL);
            in = new InputStreamReader(ecbRates.openStream());
            out = new FileWriter(cacheFile);
            try {
                int c;
                while ((c = in.read()) != -1)
                    out.write(c);
            } catch (IOException e) {
                lastError = "Read/Write Error: " + e.getMessage();
            } finally {
                out.flush();
                out.close();
                in.close();
            }
        } catch (IOException e) {
            lastError = "Connection/Open Error: " + e.getMessage();
        }
        if (lastError != null) {
            throw new IOException(lastError);
        }
    }

    /**
     * Convert a numeric string to a long value with a precision of four digits
     * after the decimal point without rounding. E.g. "123.456789" becomes
     * 1234567l.
     *
     * @param str
     *                Positive numeric string expression.
     * @return Value representing 1/10000th of a currency unit.
     * @throws NumberFormatException
     *                 If "str" argument is not numeric.
     */
    private long stringToLong(String str) throws NumberFormatException {
        int decimalPoint = str.indexOf('.');
        String wholePart = "";
        String fractionPart = "";
        if (decimalPoint > -1) {
            if (decimalPoint > 0)
                wholePart = str.substring(0, decimalPoint);
            fractionPart = str.substring(decimalPoint + 1);
            String padString = "0000";
            int padLength = 4 - fractionPart.length();
            if (padLength > 0)
                fractionPart += padString.substring(0, padLength);
            else if (padLength < 0)
                fractionPart = fractionPart.substring(0, 4);
        } else {
            wholePart = str;
            fractionPart = "0000";
        }
        return (Long.parseLong(wholePart + fractionPart));
    }

    /**
     * Parse XML cache file and create internal data structures containing
     * exchange rates and reference dates.
     *
     * @throws ParseException
     *                 If XML file cannot be parsed.
     */
    private void parse() throws ParseException {
        try {
            FileReader input = new FileReader(cacheFile);
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            DefaultHandler handler = new DefaultHandler() {
                public void startElement(String uri, String localName,
                        String qName, Attributes attributes) {
                    if (localName.equals("Cube")) {
                        String date = attributes.getValue("time");
                        if (date != null) {
                            SimpleDateFormat df = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm z");
                            try {
                                referenceDate = df.parse(date + " 14:15 CET");
                            } catch (ParseException e) {
                                lastError = "Cannot parse reference date: "
                                        + date;
                            }
                        }
                        String currency = attributes.getValue("currency");
                        String rate = attributes.getValue("rate");
                        if (currency != null && rate != null) {
                            try {
                                fxRates.put(currency, stringToLong(rate));
                            } catch (Exception e) {
                                lastError = "Cannot parse exchange rate: "
                                        + rate + ". " + e.getMessage();
                            }
                        }
                    }
                }
            };
            lastError = null;
            fxRates.clear();
            fxRates.put("EUR", 10000L);
            saxReader.setContentHandler(handler);
            saxReader.setErrorHandler(handler);
            saxReader.parse(new InputSource(input));
            input.close();
        } catch (Exception e) {
            lastError = "Parser error: " + e.getMessage();
        }
        if (lastError != null) {
            throw new ParseException(lastError, 0);
        }
    }

}

