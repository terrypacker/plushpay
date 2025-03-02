package com.payyourself.math;

import junit.framework.Assert;

import org.junit.Test;


public class CombinationMathTest {
	
	
	@Test
	public void testFactorial() throws Exception{
		
		CombinationMath comb = new CombinationMath();
		
		System.out.println("10! = " + CombinationMath.factorial(10));
		System.out.println("10! = " + comb.factrl(10));
		
		long now = System.nanoTime();
		Assert.assertEquals(2432902008176640000L,CombinationMath.factorial(20));
		long compTime = (long) ((System.nanoTime() - now)*.000001);
		
		System.out.println("Factorial Took: " + compTime + "(ms)");
		
	}
	
	@Test
	public void testNumberOfCombinationsOneGroup(){
		
		CombinationMath comb = new CombinationMath();
		try {
			Assert.assertEquals(1,comb.computeNumberOfPossibleCombinations(1));
			Assert.assertEquals(3,comb.computeNumberOfPossibleCombinations(2));
			Assert.assertEquals(7,comb.computeNumberOfPossibleCombinations(3));
			
			Assert.assertEquals(31,comb.computeNumberOfPossibleCombinations(5));
			
			
			Assert.assertEquals(1023,comb.computeNumberOfPossibleCombinations(10));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	

	@Test
	public void testNumberofCombinationsTwoGroups(){
		CombinationMath comb = new CombinationMath();
		try {
			Assert.assertEquals(1,comb.computeNumberOfPossibleCombinations(1,1));
			Assert.assertEquals(9,comb.computeNumberOfPossibleCombinations(2,2));
			Assert.assertEquals(49,comb.computeNumberOfPossibleCombinations(3,3));
			Assert.assertEquals(225,comb.computeNumberOfPossibleCombinations(4,4));
			Assert.assertEquals(961,comb.computeNumberOfPossibleCombinations(5,5));
			Assert.assertEquals(3969,comb.computeNumberOfPossibleCombinations(6,6));
			Assert.assertEquals(261121, comb.computeNumberOfPossibleCombinations(9,9));
			
			Assert.assertEquals(1046529,comb.computeNumberOfPossibleCombinations(10,10));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
