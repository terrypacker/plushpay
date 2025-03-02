package com.plushpay.service.trading.exact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.plushpay.math.CombinationMath;
import com.plushpay.repository.trading.trader.Trader;

public class OptimizedArrayTradeCombinationGenerator {

    private List<List<Trader>> sellerCombinations;
    private List<Trader> buyers;
    private List<Trader> sellers;
    private boolean init;
    private Combination best;
    private int size;

    public OptimizedArrayTradeCombinationGenerator(List<Trader> buyers, List<Trader> sellers) {
        this.init = false;
        this.buyers = buyers;
        this.sellers = sellers;
        this.size = 0;
    }

    /**
     * Compute all combinations of 2 sets
     *
     * @return
     * @throws Exception
     */
    public Combination getBest() throws Exception {

        CombinationMath math = new CombinationMath();
        //Final list of lists
        //int totalCombs = (int) math.computeNumberOfPossibleCombinations(buyers.size(), sellers.size());

        //Init the seller combs list
        int numberSellerCombs = (int) math.computeNumberOfPossibleCombinations(sellers.size());
        this.sellerCombinations = new ArrayList<List<Trader>>(numberSellerCombs);

        Combination comb;

        //This loop gets a combination for each buyer group i -> buyers.size();
        //Then gets a combination for each seller group, size j -> seller.size()
        //At each j it combines the buyercombs and sellercombs into one list
        // and computes the number of combinations for groups of size i + j
        int m = 0;

        for (int i = 1; i < buyers.size() + 1; i++) {
            //Get a generator for i size group in setA
            ArrayCombinationGenerator genBuyers = new ArrayCombinationGenerator(
                buyers.toArray(new Trader[buyers.size()]), i);
            //List each combination of size i for group A
            while (genBuyers.hasNext()) {
                Trader[] buyerComb = genBuyers.next();
                if (this.init) {
                    this.size++;
                    for (List<Trader> sellerComb : this.sellerCombinations) {
                        comb = new Combination(buyerComb,
                            sellerComb.toArray(new Trader[sellerComb.size()]));
                        if (this.best.compareTo(comb) < 1) {
                            //We have a new best
                            this.best = comb;
                        }
                    }
                } else {
                    //Get a generator for groups of size j in setB

                    for (int j = 1; j < sellers.size() + 1; j++) {
                        Trader[] tempSeller = new Trader[sellers.size()];
                        ArrayCombinationGenerator genSellers = new ArrayCombinationGenerator(
                            sellers.toArray(tempSeller), j);

                        //Take each of the sets, and produce all possible combinations
                        // between the 2 sets
                        while (genSellers.hasNext()) {
                            Trader[] sellerComb = genSellers.next();
                            this.size++;
                            //Start with the first seller comb
                            this.sellerCombinations.add(Arrays.stream(sellerComb).toList());
                            List<Trader> concat = new ArrayList<Trader>(
                                sellerComb.length + buyerComb.length);
                            concat.addAll(Arrays.stream(sellerComb).toList());
                            concat.addAll(Arrays.stream(buyerComb).toList()); //Add the buyers to it

                            comb = new Combination(concat);

                            if (this.best == null) {
                                this.best = comb;
                            } else if (this.best.compareTo(comb) < 1) {
                                //We have a new best
                                this.best = comb;

                            }
                        }
                    }
                    this.init = true;
                }
            }
        }
        return this.best;
    }

    public int size() {
        return this.size;
    }
}
