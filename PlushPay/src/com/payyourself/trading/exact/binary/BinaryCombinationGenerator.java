package com.payyourself.trading.exact.binary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Not much faster if at all than CombinationGenerator
 * @author tpacker
 *
 * @param <E>
 */
public class BinaryCombinationGenerator<E> implements Iterator<List<E>>, Iterable<List<E>> {

	private List<E> set;
	private int size;
	private long bits;
	
	public BinaryCombinationGenerator(List<E> set, int r){
		
		this.set = set;
		this.size = r;
		
		//Shift this many 1s into the position
		this.bits = 1;
		for(int i=1; i<r; i++){
			this.bits = this.bits << 1;
			this.bits = this.bits | 1;
		}
		
		
	}
	
	
	
	public List<E> next(){
		

		List<E> next = new ArrayList<E>();
		long tmp = this.bits;
		
		//int numPossibleBits = (int) Math.ceil(Math.log((double)this.bits)/Math.log(2));
		
		//TODO find a way to limit the searching for ones
		for(int i=0; i<this.size; i++){
			
			if((tmp & 1) == 1)
			next.add(this.set.get(i));
			tmp = tmp >> 1;
		}
		
		
		
		//Increment the combination
		this.bits--;

		
		return next;
	}



	public boolean hasNext() {
		
		//So long as not all of the bits are 0 we have a next
		if(this.bits == 0)
			return false;
		else
			return true;
		
	}



	public void remove() {
		 throw new UnsupportedOperationException();
		
	}



	public Iterator<List<E>> iterator() {
		return this;
	}
	
	
	
}
