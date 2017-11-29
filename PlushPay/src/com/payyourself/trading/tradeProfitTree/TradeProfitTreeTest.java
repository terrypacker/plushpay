package com.payyourself.trading.tradeProfitTree;

import java.io.IOException;

public class TradeProfitTreeTest {

	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		
		TradeProfitKey a = new TradeProfitKey(-200,-500);
		TradeProfitKey b = new TradeProfitKey(150,-50);
		
		System.out.println("Gain for a: " + a.getGain());
		System.out.println("Roi for  a: " + a.getRoi());

		System.out.println("Gain for b: " + b.getGain());
		System.out.println("Roi for  b: " + b.getRoi());

		
		if(a.compareTo(b)>0){
			System.out.println("a is better.");
		}else if(a.compareTo(b)<0){
			System.out.println("b is better.");
		}else{
			System.out.println("a and b are the same.");
		}
		
	}
}
