package com.plushpay;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.plushpay.math.CombinationMath;
import org.junit.jupiter.api.Test;

public class CombinationMathTest {
	
	
	@Test
	public void testFactorial() throws Exception{
		
		CombinationMath comb = new CombinationMath();
		
		System.out.println("10! = " + CombinationMath.factorial(10));
		System.out.println("10! = " + comb.factrl(10));
		
		long now = System.nanoTime();
		assertThat(2432902008176640000L).equals(CombinationMath.factorial(20));
		long compTime = (long) ((System.nanoTime() - now)*.000001);
		
		System.out.println("Factorial Took: " + compTime + "(ms)");
		
	}
	
	@Test
	public void testNumberOfCombinationsOneGroup(){
		
		CombinationMath comb = new CombinationMath();
		try {

			assertThat(1).equals(comb.computeNumberOfPossibleCombinations(1));
			assertThat(3).equals(comb.computeNumberOfPossibleCombinations(2));
			assertThat(7).equals(comb.computeNumberOfPossibleCombinations(3));
			assertThat(31).equals(comb.computeNumberOfPossibleCombinations(5));
			assertThat(1023).equals(comb.computeNumberOfPossibleCombinations(10));
		
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testNumberofCombinationsTwoGroups(){
		CombinationMath comb = new CombinationMath();
		try {
			assertThat(1).equals(comb.computeNumberOfPossibleCombinations(1,1));
			assertThat(9).equals(comb.computeNumberOfPossibleCombinations(2,2));
			assertThat(49).equals(comb.computeNumberOfPossibleCombinations(3,3));
			assertThat(225).equals(comb.computeNumberOfPossibleCombinations(4,4));
			assertThat(961).equals(comb.computeNumberOfPossibleCombinations(5,5));
			assertThat(3969).equals(comb.computeNumberOfPossibleCombinations(6,6));
			assertThat(261121).equals(comb.computeNumberOfPossibleCombinations(9,9));
			assertThat(1046529).equals(comb.computeNumberOfPossibleCombinations(10,10));
		
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
