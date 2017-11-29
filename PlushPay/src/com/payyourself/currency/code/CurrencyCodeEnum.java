package com.payyourself.currency.code;


public enum CurrencyCodeEnum {

	USD(0,"United States Dollar"),
	AUD(1, "Australian Dollar");
	
	private final int code;
	private final String description;
	
	private CurrencyCodeEnum(int code, String description){
		this.code = code;
		this.description = description;
	}


	public int getValue(){
		return code;
	}
	
	public String getDescription(){
		return description;
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
