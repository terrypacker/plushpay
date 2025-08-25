package com.plushpay;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.plushpay.math.CombinationMath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;

class CombinationMathTest {

    private Log log = LogFactory.getLog(getClass());

    @Test
    void testFactorial() throws Exception {

        CombinationMath comb = new CombinationMath();

        log.debug("10! = " + CombinationMath.factorial(10));
        System.out.println("10! = " + comb.factrl(10));

        long now = System.nanoTime();
        assertThat(2432902008176640000L).isEqualTo(CombinationMath.factorial(20));
        long compTime = (long) ((System.nanoTime() - now) * .000001);
        log.info("Factorial Took: " + compTime + "(ms)");
    }

    @Test
    public void testNumberOfCombinationsOneGroup() {

        CombinationMath comb = new CombinationMath();
        try {

            assertThat(1).isEqualTo(comb.computeNumberOfPossibleCombinations(1));
            assertThat(3).isEqualTo(comb.computeNumberOfPossibleCombinations(2));
            assertThat(7).isEqualTo(comb.computeNumberOfPossibleCombinations(3));
            assertThat(31).isEqualTo(comb.computeNumberOfPossibleCombinations(5));
            assertThat(1023).isEqualTo(comb.computeNumberOfPossibleCombinations(10));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testNumberofCombinationsTwoGroups() {
        CombinationMath comb = new CombinationMath();
        try {
            assertThat(1).isEqualTo(comb.computeNumberOfPossibleCombinations(1, 1));
            assertThat(9).isEqualTo(comb.computeNumberOfPossibleCombinations(2, 2));
            assertThat(49).isEqualTo(comb.computeNumberOfPossibleCombinations(3, 3));
            assertThat(225).isEqualTo(comb.computeNumberOfPossibleCombinations(4, 4));
            assertThat(961).isEqualTo(comb.computeNumberOfPossibleCombinations(5, 5));
            assertThat(3969).isEqualTo(comb.computeNumberOfPossibleCombinations(6, 6));
            assertThat(261121).isEqualTo(comb.computeNumberOfPossibleCombinations(9, 9));
            assertThat(1046529).isEqualTo(comb.computeNumberOfPossibleCombinations(10, 10));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
