package com.plushpay.service.trading.exact;

import java.util.List;

import com.plushpay.math.CombinationMath;
import com.plushpay.repository.trading.trader.Trader;

public class DoubleArrayCombinationGenerator {

    private Trader[][] sellerCombinations;
    private boolean init;

    public DoubleArrayCombinationGenerator() {
        this.init = false;
    }

    /**
     * Compute all combinations of 2 sets
     *
     * @param buyers
     * @param sellers
     * @return
     * @throws Exception
     */
    public Trader[][] getAllCombinations(List<Trader> buyers, List<Trader> sellers)
        throws Exception {

        ArrayCombinationGenerator genBuyers;
        ArrayCombinationGenerator genSellers;
        //CombinationGenerator<E> genAll;

        CombinationMath math = new CombinationMath();
        //Final list of lists
        int totalCombs = (int) math.computeNumberOfPossibleCombinations(buyers.size(),
            sellers.size());
        Trader[][] solution = new Trader[totalCombs][];

        Trader[] buyerComb;
        Trader[] sellerComb;

        //Init the seller combs list
        //TODO compute the numberSellerCombs accurately
        int numberSellerCombs = (int) math.computeNumberOfPossibleCombinations(sellers.size());
        this.sellerCombinations = new Trader[numberSellerCombs][];

        //Generate each buyer combination
        for (int i = 0; i < buyers.size(); i++) {

        }

        return solution;
    }

}
