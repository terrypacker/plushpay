package com.payyourself.math;

public class CombinationMath {

	private static int NTOP=2000;

	private boolean init =true;
	private double[] a;

	public CombinationMath(){
		
	}
	
	/**
	 * Will only work for n up to ??
	 * 
	 * Tested to 20, works.
	 * @param n
	 * @return
	 */
	public static long factorial(int n){
		if(n<=1)
			return 1;
		else
			return n* CombinationMath.factorial(n-1);
	}
	
	
	
	public double gammln(double xx) throws Exception {
		int j;
		double x,tmp,y,ser;
		
		double[] cof = {57.1562356658629235,-59.5979603554754912,
		14.1360979747417471,-0.491913816097620199,.339946499848118887e-4,
		.465236289270485756e-4,-.983744753048795646e-4,.158088703224912494e-3,
		-.210264441724104883e-3,.217439618115212643e-3,-.164318106536763890e-3,
		.844182239838527433e-4,-.261908384015814087e-4,.368991826595316234e-5};
		
		if (xx <= 0) throw new Exception ("bad arg in gammln");
		
		y=x=xx;
		tmp = x+5.24218750000000000;
		
		tmp = (x+0.5)*Math.log(tmp)-tmp;
		ser = 0.999999999999997092;
		
		for (j=0;j<14;j++) 
			ser += cof[j]/++y;
		return tmp+Math.log(2.5066282746310005*ser/x);
	}
	
	public double factrl(int n) throws Exception {
		
		
		//Init the table
		if (this.init) {
			a = new double[171];
			this.init = false;
			a[0] = 1.;
			for (int i=1;i<171;i++)
				a[i] = i*a[i-1];
		}
		
		
		if (n < 0 || n > 170) throw new Exception("factrl out of range");
		return a[n];
	}
	
	
	public double factln(int n) throws Exception {
		double[] a = new double[CombinationMath.NTOP];
		boolean init=true;
		if (init) {
			init = false;
			for (int i=0;i<CombinationMath.NTOP;i++)
				a[i] = gammln(i+1.);
		}
		if (n < 0) throw new Exception("negative arg in factln");
		if (n < CombinationMath.NTOP) return a[n];
		return gammln(n+1.);
	}
	
	public double bico(int n, int k) throws Exception {
		if (n<0 || k<0 || k>n) throw new Exception("bad args in bico");
		if (n<171) return Math.floor(0.5+factrl(n)/(factrl(k)*factrl(n-k)));
		return Math.floor(0.5+Math.exp(factln(n)-factln(k)-factln(n-k)));
	}
	
	
	public double beta(double z, double w) throws Exception {
		return Math.exp(gammln(z)+gammln(w)-gammln(z+w));
	}
	
	
	/**
	 * Compute the number of unique combinations of one group
	 * to another.
	 * 
	 * Compute a series of binomial coefficients using the
	 * recurrence relationship of (n k+1) = (n-k)/(k+1)* (n k)
	 *  
	 * 
	 * @return
	 * @throws Exception 
	 */
	public long computeNumberOfPossibleCombinations(int groupASize, int groupBSize) throws Exception{

		if((groupASize == 1)&&(groupBSize == 1))
			return 1;
		
		//Number of combinations of groupA
		long groupACombs = this.computeNumberOfPossibleCombinations(groupASize);
		long groupBCombs = this.computeNumberOfPossibleCombinations(groupBSize);

		//Now compute the groups to each other
		
		//There is one new combination for each groupA to every groupB
		

		long totalCombs = groupACombs * groupBCombs;
		
		return totalCombs;
	}
	
	/**
	 * Compute the total number of unique combinations in one group
	 * @param groupSize
	 * @return
	 * @throws Exception 
	 */
	public long computeNumberOfPossibleCombinations(int groupSize) throws Exception{
		
		if(groupSize == 1)
			return 1;
		
		if(groupSize < 1){
			return 0;
		}
		
		double total = 1; //For one group of size groupSize
		
		double baseFact = groupSize; //this.bico(groupSize, 1); //For all size 1 groups
		
		total = total + baseFact;

		//Using recurrence
		//where (n k+1) = (n-k)/(k+1) * (n k)
		//so where (n 1+i) = (n-1)/(1+i)*(n k)
		for(int i=1; i<groupSize-1; i++){
			
			baseFact = baseFact * (groupSize-i)/(i+1);
			
			total = total + baseFact; 
		}
		
		return (long)total;
	}
	
	
}
