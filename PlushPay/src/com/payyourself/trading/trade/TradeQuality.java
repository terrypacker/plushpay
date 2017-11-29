package com.payyourself.trading.trade;

public enum TradeQuality {
	
	Perfect,
	Good,
	Average,
	Poor,
	Bad;
	
	public static TradeQuality convert(float value){
		
		if((value <= 100)&&(value > 50)){
			return Poor;
		}else if((value <= 50)&&(value > 10)){
			return Average;
		}else if((value <= 10) && (value > 0)){
			return Good;
		}else if(value == 0){
			return Perfect;
		}else{
			return Bad;
		}
	}
}
