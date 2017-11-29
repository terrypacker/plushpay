package com.payyourself.trading.exact.binary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Implemenataion from Matters Computational Jorg Arndt  pp 213
 * @author tpacker
 *
 * @param <E>
 */
public class BinarySubsetGenerator<E> implements Iterator<List<E>>, Iterable<List<E>> {

	private List<E> set;
	private int size;
	private long bits;
	
	private int k,kmin,kmax,j;
	private int[] s;
	int n;
	
	public BinarySubsetGenerator(List<E> set, int r){
		
		this.set = set;
		this.n = r;
		this.s = new int[r];
		
		//Shift this many 0s into the position
		this.bits = 0;
		
	}
		
	
	public List<E> next(){
		

		if((j&1)==1)
			this.prev_even();
		else
			this.prev_odd();
		
		if(j<kmin){
			k = kmin;
		}else{
			k=j;
		}
		
		return k;
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
	
	private int last(){
		this.s[1] = 1;
		k = kmin;
		if(kmin==1){
			j=1;
		}else{
			for(int i=2; i<kmin; i++){
				s[i] =  n - kmin + i;
			}
			j=2;
		}
		return k;
		
	}
	
	public int first(){
		k=kmin;
		for(int i=0; i<kmin; i++){
			s[i] = n - kmin + i;
		}
		j=1;
		return k;
	}
	
	
	void prev_even(){
		if(s[j-1] == s[j] -1 ){
			s[j-1] = s[j];
			if(j>kmin){
				if(s[kmin] == n){
					j=j-2;
				}else{
					j= j-1;
				}
			}else{
				s[j]=n - kmin + j;
				if(s[j-1]==s[j]-1){
					j=j-2;
				}
			}
		}else{
			s[j] = s[j] -1;
			if(j<kmax){
				s[j+1] = s[j] + 1;
				if(j>=kmin-1){
					j=j+1;
				}else{
					j= j+2;
				}
			}
		}
	}
	
	
	
	
	void prev_odd(){
		if(s[j] == n){
			j= j-1;
		}else{
			if(j<kmax){
				s[j+1] = n;
				j= j+1;
			}else{
				s[j] = s[j] + 1;
				if(s[kmin] == n){
					j = j-1;
				}
			}
		}
	}
	
}

