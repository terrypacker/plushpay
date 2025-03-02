package com.payyourself.trading.exact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class ExactMethodsTest {

	@Test
	public void testCombinationGenerator(){
		
		int setSize = 20;
		List<Long> set = new ArrayList<Long>();
		long initialValue = 0;
		for(int i=0; i<setSize; i++){
			set.add(initialValue + i);
		}
		
		List<List<Long>> combs = new ArrayList<List<Long>>();
		
		
		long now = System.nanoTime();
		for(int i=1; i<=setSize; i++){
			CombinationGenerator<Long> gen = new CombinationGenerator<Long>(set,i);			
			for(List<Long> comb : gen){
				combs.add(comb);
			}
		}
		long later = System.nanoTime();
		System.out.println("Execution Time: " + (later - now)*.000001 + "ms");
		
		
	}
	
}
