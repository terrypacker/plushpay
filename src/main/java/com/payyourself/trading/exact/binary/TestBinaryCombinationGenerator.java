package com.payyourself.trading.exact.binary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.payyourself.trading.exact.CombinationGenerator;


public class TestBinaryCombinationGenerator {

	
	@Test
	public void viewOutput(){
        List<Character> set = Arrays.asList('A', 'B', 'C','D','E','F','G');
		List<List<Character>> test1 = new ArrayList<List<Character>>();
		List<List<Character>> test2 = new ArrayList<List<Character>>();
		
        long now,later,avg=0;
		int numIterations = 10;
		
		for(int i=0; i<numIterations; i++){
			now = System.nanoTime();
			for(int n=1; n<=set.size(); n++){
		        CombinationGenerator<Character> cg = new CombinationGenerator<Character>(set, n);
		        for(List<Character> combination : cg) {
		            test1.add(combination);
		            System.out.println(combination);
		        } 
			}
			System.out.println("------");
	        later = System.nanoTime();
	        avg = avg + later - now;
	        
		}
        
		avg = avg/numIterations;
		avg = (long) ((float)avg * .000001); //in ms
		
        System.out.println("CombinationGenerator: " + avg + "ns");
        
		for(int i=0; i<numIterations; i++){
			now = System.nanoTime();
	        BinaryCombinationGenerator<Character> cg = new BinaryCombinationGenerator<Character>(set, set.size());

	        for(List<Character> combination : cg) {
	        	
	        	test2.add(combination);
	        	System.out.println(combination);
	        }
	        
	        System.out.println("-------");
	        
	        later = System.nanoTime();
	        avg = avg + later - now;
	        
		}
        
		avg = avg/numIterations;
		avg = (long) ((float)avg * .000001); //in ms
	
        System.out.println("BinaryCombinationGenerator: " + avg + "ns");

	}
	
	@Test
	public void compareExecutionTime(){

		System.out.println("Execution Time Test");
        List<Character> set = Arrays.asList('A', 'B', 'C','D','E','F','G','H','I','J','K','L','M');
		List<List<Character>> test1 = new ArrayList<List<Character>>();
		List<List<Character>> test2 = new ArrayList<List<Character>>();
		
        long now,later,avg=0;
		int numIterations = 20;
		
		for(int i=0; i<numIterations; i++){
			now = System.nanoTime();
			for(int n=1; n<=set.size(); n++){
		        CombinationGenerator<Character> cg = new CombinationGenerator<Character>(set, n);
		        for(List<Character> combination : cg) {
		            test1.add(combination);
		        } 
			}
	        later = System.nanoTime();
	        avg = avg + later - now;
	        
		}
        
		avg = avg/numIterations;
		//avg = (long) ((float)avg * .000001); //in ms
		
        System.out.println("CombinationGenerator: " + avg + "ns");
        
		for(int i=0; i<numIterations; i++){
			now = System.nanoTime();
	        BinaryCombinationGenerator<Character> cg = new BinaryCombinationGenerator<Character>(set, set.size());

	        for(List<Character> combination : cg) {
	        	
	        	test2.add(combination);
	        }
	        
	        
	        later = System.nanoTime();
	        avg = avg + later - now;
	        
		}
        
		avg = avg/numIterations;
		
        System.out.println("BinaryCombinationGenerator: " + avg + "ns");
		
		
		
		
	}
	
	
}
