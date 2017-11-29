package com.payyourself.trading.exact;


import java.util.NoSuchElementException;
import com.payyourself.trading.trader.Trader;

public class ArrayCombinationGenerator{
  
	private Trader[] set;
    private int[] currentIdxs;
    private int[] lastIdxs;
    
    public ArrayCombinationGenerator(Trader[] set, int r) {
        if(r < 1 || r > set.length) {
            throw new IllegalArgumentException("r < 1 || r > set.size()");
        }
        this.set = new Trader[set.length];
        
        this.currentIdxs = new int[r];
        this.lastIdxs = new int[r];
        for(int i = 0; i < r; i++) {
            this.currentIdxs[i] = i;
            this.lastIdxs[i] = set.length - r + i;
        }
    }

    public boolean hasNext() {
        return currentIdxs != null;
    }

    
    
    public Trader[] next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        Trader[] currentCombination = new Trader[this.currentIdxs.length];
        
        for(int i : currentIdxs) {
            currentCombination[i] = set[i];
        }
        //Setup for the next indexes
        this.setNextIndexes();
        return currentCombination;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    
    private void setNextIndexes() {
    	
        for(int i = currentIdxs.length-1, j = set.length-1; i >= 0; i--, j--) {
            if(currentIdxs[i] != j) {
                currentIdxs[i]++;
                for(int k = i+1; k < currentIdxs.length; k++) {
                    currentIdxs[k] = currentIdxs[k-1]+1;
                }
                return;
            }
        }
        currentIdxs = null;
    }
    
}