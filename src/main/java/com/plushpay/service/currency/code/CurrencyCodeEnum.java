package com.plushpay.service.currency.code;


public enum CurrencyCodeEnum {

	USD(0,"United States Dollar", "$"),
	AUD(1, "Australian Dollar", "$");
	
	private final int code;
	private final String description;
	private final String symbol;
	
	private CurrencyCodeEnum(int code, String description, String symbol) {
		this.code = code;
		this.description = description;
		this.symbol = symbol;
	}


	public int getValue(){
		return code;
	}
	
	public String getDescription(){
		return description;
	}

	public String getSymbol(){
		return symbol;
	}

    public static CurrencyCodeEnum valueOf(int id) {
        CurrencyCodeEnum[] list = CurrencyCodeEnum.values();
        for( CurrencyCodeEnum enumItem : list ) {
            if ( enumItem.getValue() == id ){
                return enumItem;
            }
        }

        return null;
    }
    
  
	
	
}
