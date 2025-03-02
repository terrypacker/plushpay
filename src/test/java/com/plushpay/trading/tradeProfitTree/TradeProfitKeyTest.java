package com.plushpay.trading.tradeProfitTree;

import com.plushpay.service.trading.tradeProfitTree.TradeProfitKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Terry Packer
 */
@SpringBootTest
public class TradeProfitKeyTest {

    @Test
    public void test() {
        TradeProfitKey a = new TradeProfitKey(-200, -500);
        TradeProfitKey b = new TradeProfitKey(150, -50);

        System.out.println("Gain for a: " + a.getGain());
        System.out.println("Roi for  a: " + a.getRoi());

        System.out.println("Gain for b: " + b.getGain());
        System.out.println("Roi for  b: " + b.getRoi());

        if (a.compareTo(b) > 0) {
            System.out.println("a is better.");
        } else if (a.compareTo(b) < 0) {
            System.out.println("b is better.");
        } else {
            System.out.println("a and b are the same.");
        }
    }

}
