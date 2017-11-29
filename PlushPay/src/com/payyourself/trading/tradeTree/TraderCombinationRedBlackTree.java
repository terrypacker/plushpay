package com.payyourself.trading.tradeTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.payyourself.log.LogfileFactory;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.group.TraderGroupUtil;


public class TraderCombinationRedBlackTree {

	private Logger log;
	private TreeMap<Long,TraderCombinationNode> tree; //Java Collections Red Black Tree
	private List<TraderCombinationNode> nodes; //All nodes in the tree...
	
	
	public TraderCombinationRedBlackTree(){
		this.tree = new TreeMap<Long, TraderCombinationNode>();
		this.nodes = new ArrayList<TraderCombinationNode>();
		this.log = LogfileFactory.getHTMLLogger(Level.ALL, this.getClass());
		
	}
	
	/**
	 * Basic insert seller, this is the Most tested insert seller.
	 * @param seller
	 * @throws Exception 
	 */
	public void insertSeller(Trader seller) throws Exception{
		int startSize = this.nodes.size();
		
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(this.nodes.get(0).getBuyers());
		
		TraderGroupUtil sellerGroup = new TraderGroupUtil(this.nodes.get(0).getSellers());
		sellerGroup.add(seller);
		
		//The newNode we will be using
		TraderCombinationNode newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
		this.put(newNode); //Ad the new node no matter what
		
		//if(this.tree.size() == 1){
			//Just add it to the 0 buyer

		//}
	
		//Go through tree, adding new nodes for the trader
		//We will walk tree from least to greatest
		// this allows us to add in order..., if we could!
		long now = System.nanoTime();
		for(int i=1; i<startSize; i++){
			
	
			
				//We will Add the group
				//Build current groups
				buyerGroup = new TraderGroupUtil(this.nodes.get(i).getBuyers());
				sellerGroup = new TraderGroupUtil(this.nodes.get(i).getSellers());
				sellerGroup.add(seller);
				newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
				this.put(newNode);
				
		}
		this.log.info("Building and Inserting " + startSize + " nodes took:" + ((System.nanoTime()-now)/1000000) + "ms");
		
	}
	
	/**
	 * 
	 * @param buyer
	 * @throws Exception 
	 */
	public void insertBuyer(Trader buyer) throws Exception{
		
		int startSize = this.nodes.size();
		
		
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(this.nodes.get(0).getBuyers());
		buyerGroup.add(buyer);
		
		TraderGroupUtil sellerGroup = new TraderGroupUtil(this.nodes.get(0).getSellers());
		
		//The newNode we will be using
		TraderCombinationNode newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
		//Just add the buyer (No Matter What)
		this.put(newNode);
		
		
		//if(this.tree.size() == 1){
//
//		}
	
		//Go through tree, adding new nodes for the trader
		//We will walk tree from least to greatest
		// this allows us to add in order..., if we could!
		long now = System.nanoTime();
		for(int i=1; i<startSize; i++){
			
				//We will Add the group
				//Build current groups
				buyerGroup = new TraderGroupUtil(this.nodes.get(i).getBuyers());
				buyerGroup.add(buyer);
				
				sellerGroup = new TraderGroupUtil(this.nodes.get(i).getSellers());
				newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
				this.put(newNode);

			
		}
	
		this.log.info("Building and Inserting " + startSize + " nodes took:" + ((System.nanoTime()-now)/1000000) + "ms");
	}

	public Long lastKey() {
		return this.tree.lastKey();
	}

	public TraderCombinationNode get(Long key) {
		return this.tree.get(key);
	}

	/**
	 * Put a Trader Combination Node into the tree.
	 * @param newNode
	 */
	public void put(TraderCombinationNode newNode) {
		this.tree.put(newNode.getTotalMismatch(), newNode);
		
		if(!this.nodes.contains(newNode)){
			this.nodes.add(newNode);
		}else{
			this.log.info("Trying to double up on a node.");
		}
	}

	/**
	 * Trim the tree by removing anyone within 1-delta of the previous node.
	 * 
	 * Smaller delta, less nodes removed.
	 * 
	 * Larger delta, more nodes removed.
	 * 
	 * @param delta
	 */
	public int trimTree(double delta){
		int i = 0;
		Set<Entry<Long, TraderCombinationNode>> keys = this.tree.entrySet();
		
		Iterator<Entry<Long, TraderCombinationNode>> it = keys.iterator();
		
		double trim = (1-delta);
		
		Entry<Long,TraderCombinationNode> predecessor = it.next();
		Entry<Long,TraderCombinationNode> current;
		
		while(it.hasNext()){
			current = it.next();
	
			long test = (long)(trim*current.getValue().getTotalMismatch());
			
			if(predecessor.getValue().getTotalMismatch()>(long)(trim*current.getValue().getTotalMismatch())){
					//Yes trim it					//System.out.println("Removing node: " + current);
				//System.out.println("Because: " + predecessor + " > " + (long)((1-delta)*current));
				if(!this.nodes.remove(current.getValue())){
					this.log.info("Unable to remove node.");
				}
				it.remove();
				i++;
				
			}
			predecessor = current;
		}
		
		this.log.info("Trimmed " + i + " members from tree.");
		return i;
		
	}

	/**
	 * Crop the tree by removing
	 * any node with mismatch above cropAbove% of the worst match.
	 * 
	 * This will not allow removal of the best group, so 
	 * if the computed removal point is below best group,
	 * we will move it to just above best group
	 * 
	 * The larger the value of cropAbove the more 
	 * nodes removed from the tree.
	 * 
	 * smaller cropAbove less nodes removed
	 * 
	 * larger cropAbove more nodes removed
	 * 
	 * 
	 * NOT FULLY TESTED
	 * 
	 * @param cropAbove
	 */
	public void cropTree(double cropAbove){
		Set<Entry<Long, TraderCombinationNode>> keys = this.tree.entrySet();
		
		Iterator<Entry<Long, TraderCombinationNode>> it = keys.iterator();
		
		long cropAboveValue = tree.get(this.tree.lastKey()).getTotalMismatch();
		int i= 0;
		if(this.tree.size() > 2){
			cropAboveValue = (long) (cropAboveValue*(1-cropAbove));
		}
		
		//Check to make sure we can't crop the best solution
		if(cropAboveValue < this.getBest().getTotalMismatch()){
			cropAboveValue = this.getBest().getTotalMismatch();
		}
		
		Entry<Long,TraderCombinationNode> current;
		
		while(it.hasNext()){
			current = it.next();
			if(current.getValue().getTotalMismatch() > cropAboveValue){
				this.nodes.remove(current.getValue());
				it.remove();
				i++;
			}
		}//end while
		this.log.info("Cropped " + i + " members of tree.");
	}

	/**
	 * Reset the nodes and the tree to empty
	 */
	public void resetTree() {
		this.nodes = new ArrayList<TraderCombinationNode>();
		this.tree = new TreeMap<Long,TraderCombinationNode>();
		
	}

	public int size() {
		return this.tree.size();
	}

	/**
	 * Print the keys of the tree, in order
	 */
	public void printKeys() {
		
		Set<Long> keys = this.tree.keySet();
		
		Iterator<Long> it = keys.iterator();
		it.next(); //Burn the 0 traderNode
		while(it.hasNext()){
			Long value = it.next();
			
			System.out.println(value);
		}
		
	}

	/**
	 * Return the closest match in the tree.
	 * @return
	 */
	public TraderCombinationNode getBest() {
		
		Set<Entry<Long, TraderCombinationNode>> values = this.tree.entrySet();
		
		Iterator<Entry<Long, TraderCombinationNode>> it = values.iterator();
		
		
		if(this.size() > 1){
			it.next(); //The 0 trader
			return (TraderCombinationNode)it.next().getValue();
		}else{
			return null; //we won't return the 0 trader ever.
		}
		
	}

	/**
	 * Get the list of nodes for this tree
	 * @return
	 */
	public List<TraderCombinationNode> getNodes(){
		return this.nodes;
	}

	/**
	 * Return the entry set of the tree
	 * @return
	 */
	@Deprecated
	public Set<Map.Entry<Long, TraderCombinationNode>> entrySet() {
		return tree.entrySet();
		
	}

	/**
	 * Insert a buyer into the tree 
	 * Crop and Trim Tree in the process
	 * 
	 * NOT FULLY TESTED
	 * 
	 * @param buyer
	 * @param delta
	 * @param cropAbove
	 * @throws Exception
	 */
	@Deprecated
	public void insertBuyer(Trader buyer, float delta, float cropAbove) throws Exception{
		
		Set<Entry<Long, TraderCombinationNode>> keys = this.tree.entrySet();
		
		Iterator<Entry<Long, TraderCombinationNode>> it = keys.iterator();
		
		float trim = (1-delta);
		
		long cropAboveValue = tree.get(this.tree.lastKey()).getTotalMismatch();
		
		if(this.tree.size() > 1){
			cropAboveValue = (long) (cropAboveValue*(1-cropAbove));
		}
		
		Entry<Long,TraderCombinationNode> predecessor = it.next();
		Entry<Long,TraderCombinationNode> current;
		
		//The List, since we can't edit the tree while we are iterating.
		List<TraderCombinationNode> newNodes = new ArrayList<TraderCombinationNode>(this.tree.size());
		
		
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(predecessor.getValue().getBuyers());
		buyerGroup.add(buyer);
		
		TraderGroupUtil sellerGroup = new TraderGroupUtil(predecessor.getValue().getSellers());
		
		//The newNode we will be using
		TraderCombinationNode newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
		
		if(this.tree.size() == 1){
			//Just add the buyer
			this.put(newNode);
		}else{
			//Do the first node outside the loop
			if(newNode.getTotalMismatch() > cropAboveValue){
				//Don't add it
			}else if(predecessor.getValue().getTotalMismatch()>(long)(trim*newNode.getTotalMismatch())){
				//Dont add it
			}else{
				//We will Add the group
				newNodes.add(newNode);
			}
		}
		long currentMismatch;
	
		//Go through tree, adding new nodes for the trader
		//We will walk tree from least to greatest
		// this allows us to add in order..., if we could!
		long now = System.nanoTime();
		while(it.hasNext()){
			
			//Move to the next node
			current = it.next();
			
			//Compute the mismatch with the new buyer to see if we can even add it
			currentMismatch = (Math.abs(current.getValue().getBuyers().getCurrencyToBuy().getValue()+buyer.getCurrencyToBuy().getValue()
					-current.getValue().getSellers().getCurrencyToSell().getValue()) +
					Math.abs(current.getValue().getBuyers().getCurrencyToSell().getValue()+buyer.getCurrencyToSell().getValue() - current.getValue().getSellers().getCurrencyToBuy().getValue()));
			
			if(currentMismatch > cropAboveValue){
				//Not adding it
			}else{
				
				//Do the trimming here not really a function of adding a buyer, but to same time as  we are looping over the list anyway
				if(predecessor.getValue().getTotalMismatch()>(long)(trim*current.getValue().getTotalMismatch())){
					it.remove();
					this.nodes.remove(current); //Probably won't need this anymore
				}
				
				//We will Add the group
				//Build current groups
				buyerGroup = new TraderGroupUtil(current.getValue().getBuyers());
				buyerGroup.add(buyer);
				
				sellerGroup = new TraderGroupUtil(current.getValue().getSellers());
				newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
				newNodes.add(newNode);
				
				//Move predecessor
				predecessor = current;
			}
			
			
		}
		System.out.println("Building " + newNodes.size() + " nodes took:" + (System.nanoTime()-now));
		
		
		now = System.nanoTime();
		//Ok we have the list of nodes to insert, now insert them
		for(int i=0; i<newNodes.size(); i++){
			this.put(newNodes.get(i));
		}
	
		System.out.println("Inserting " + newNodes.size() + " nodes took:" + (System.nanoTime()-now));
	}

	/**
	 * Insert a buyer into the tree 
	 * Crop and Trim Tree in the process
	 * 
	 * NOT FULLY TESTED
	 * 
	 * @param buyer
	 * @param delta
	 * @param cropAbove
	 * @throws Exception
	 */
	@Deprecated
	public void insertBuyer(Trader buyer, float cropAbove) throws Exception{
		
		int startSize = this.nodes.size();
		
		long cropAboveValue = tree.get(this.tree.lastKey()).getTotalMismatch();
		
		if(this.tree.size() > 1){
			cropAboveValue = (long) (cropAboveValue*(1-cropAbove));
		}
		
		
		
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(this.nodes.get(0).getBuyers());
		buyerGroup.add(buyer);
		
		TraderGroupUtil sellerGroup = new TraderGroupUtil(this.nodes.get(0).getSellers());
		
		//The newNode we will be using
		TraderCombinationNode newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
		
		if(this.tree.size() == 1){
			//Just add the buyer
			this.put(newNode);
		}else{
			//Do the first node outside the loop
			if(newNode.getTotalMismatch() > cropAboveValue){
				//Don't add it
			}else{
				//We will Add the group
				this.put(newNode);
			}
		}
		long currentMismatch;
	
		//Go through tree, adding new nodes for the trader
		//We will walk tree from least to greatest
		// this allows us to add in order..., if we could!
		long now = System.nanoTime();
		for(int i=1; i<startSize; i++){
			
			
			//Compute the mismatch with the new buyer to see if we can even add it
			currentMismatch = (Math.abs(this.nodes.get(i).getBuyers().getCurrencyToBuy().getValue()+buyer.getCurrencyToBuy().getValue()
					-this.nodes.get(i).getSellers().getCurrencyToSell().getValue()) +
					Math.abs(this.nodes.get(i).getBuyers().getCurrencyToSell().getValue()+buyer.getCurrencyToSell().getValue() - this.nodes.get(i).getSellers().getCurrencyToBuy().getValue()));
			
			if(currentMismatch > cropAboveValue){
				//Not adding it
			}else{
				
				
				//We will Add the group
				//Build current groups
				buyerGroup = new TraderGroupUtil(this.nodes.get(i).getBuyers());
				buyerGroup.add(buyer);
				
				sellerGroup = new TraderGroupUtil(this.nodes.get(i).getSellers());
				newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
				this.put(newNode);
				
			}
			
			
		}
	
		this.log.info("Building and Inserting " + startSize + " nodes took:" + ((System.nanoTime()-now)/1000000) + "ms");
		
		
	}

	/**
	 * Put a node into the Tree, this also adds the node
	 * to the List of nodes that are in the tree, to speed up some operations 
	 * we keep a list AND a map.
	 * 
	 * Please use put(TraderCombinationNode) from now on.
	 * 
	 * @param totalMismatch
	 * @param newNode
	 */
	@Deprecated 
	public void put(long totalMismatch, TraderCombinationNode newNode) {
		this.tree.put(totalMismatch, newNode);
		if(!this.nodes.contains(newNode)){
			this.nodes.add(newNode);
		}else{
			this.log.info("Trying to double up on a node.");
		}
		
		
	}

	/**
	 * 
	 * Crop Above - larger value means smaller tree size
	 * 
	 * This method is NOT fully tested.
	 * 
	 * @param seller
	 * @param cropAbove
	 * @throws Exception 
	 * @throws Exception
	 */
	@Deprecated
	public void insertSeller(Trader seller, float cropAbove) throws Exception {
		
		int startSize = this.nodes.size();
		
		long cropAboveValue = tree.get(this.tree.lastKey()).getTotalMismatch();
		
		if(this.tree.size() > 1){
			cropAboveValue = (long) (cropAboveValue*(1-cropAbove));
		}
		
		
	
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(this.nodes.get(0).getBuyers());
		
		TraderGroupUtil sellerGroup = new TraderGroupUtil(this.nodes.get(0).getSellers());
		sellerGroup.add(seller);
		
		//The newNode we will be using
		TraderCombinationNode newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
		
		if(this.tree.size() == 1){
			//Just add it to the 0 buyer
			this.put(newNode);
		}else{
			//Do the first node outside the loop
			if(newNode.getTotalMismatch() > cropAboveValue){
				//Don't add it
			}else{
				//We will Add the group
				this.put(newNode);
			}
		}
		long currentMismatch;
	
		//Go through tree, adding new nodes for the trader
		//We will walk tree from least to greatest
		// this allows us to add in order..., if we could!
		long now = System.nanoTime();
		for(int i=1; i<startSize; i++){
			
		
			//Compute the mismatch with the new buyer to see if we can even add it
			currentMismatch = (Math.abs(this.nodes.get(i).getBuyers().getCurrencyToBuy().getValue()
					-this.nodes.get(i).getSellers().getCurrencyToSell().getValue() + seller.getCurrencyToSell().getValue()) +
					Math.abs(this.nodes.get(i).getBuyers().getCurrencyToSell().getValue() -
							this.nodes.get(i).getSellers().getCurrencyToBuy().getValue() +seller.getCurrencyToBuy().getValue()));
			
			if(currentMismatch > cropAboveValue){
				//Not adding it
			}else{
				
			
				//We will Add the group
				//Build current groups
				buyerGroup = new TraderGroupUtil(this.nodes.get(i).getBuyers());
				sellerGroup = new TraderGroupUtil(this.nodes.get(i).getSellers());
				sellerGroup.add(seller);
				newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
				this.put(newNode);
				
			}
			
			
		}
		this.log.info("Building and Inserting " + startSize + " nodes took:" + ((System.nanoTime()-now)/1000000) + "ms");
		
		
	}

	/**
	 * Insert seller and crop and trim tree in process.
	 * 
	 * This method is NOT fully tested.
	 * 
	 * @param seller
	 * @param delta
	 * @param cropAbove
	 * @throws Exception
	 */
	@Deprecated
	public void insertSeller(Trader seller, float delta, float cropAbove) throws Exception{
		
		Set<Entry<Long, TraderCombinationNode>> keys = this.tree.entrySet();
		
		Iterator<Entry<Long, TraderCombinationNode>> it = keys.iterator();
		
		float trim = (1-delta);
		
		long cropAboveValue = tree.get(this.tree.lastKey()).getTotalMismatch();
		
		if(this.tree.size() > 1){
			cropAboveValue = (long) (cropAboveValue*(1-cropAbove));
		}
		
		Entry<Long,TraderCombinationNode> predecessor = it.next();
		Entry<Long,TraderCombinationNode> current;
		
		//The List, since we can't edit the tree while we are iterating.
		List<TraderCombinationNode> newNodes = new ArrayList<TraderCombinationNode>(this.tree.size());
		
		
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(predecessor.getValue().getBuyers());
		
		TraderGroupUtil sellerGroup = new TraderGroupUtil(predecessor.getValue().getSellers());
		sellerGroup.add(seller);
		
		//The newNode we will be using
		TraderCombinationNode newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
		
		if(this.tree.size() == 1){
			//Just add it to the 0 buyer
			this.put(newNode);
		}else{
			//Do the first node outside the loop
			if(newNode.getTotalMismatch() > cropAboveValue){
				//Don't add it
			}else if(predecessor.getValue().getTotalMismatch()>(long)(trim*newNode.getTotalMismatch())){
				//Dont add it
			}else{
				//We will Add the group
				newNodes.add(newNode);
			}
		}
		long currentMismatch;
	
		//Go through tree, adding new nodes for the trader
		//We will walk tree from least to greatest
		// this allows us to add in order..., if we could!
		long now = System.nanoTime();
		while(it.hasNext()){
			
			//Move to the next node
			current = it.next();
			
			//Compute the mismatch with the new buyer to see if we can even add it
			currentMismatch = (Math.abs(current.getValue().getBuyers().getCurrencyToBuy().getValue()
					-current.getValue().getSellers().getCurrencyToSell().getValue() + seller.getCurrencyToSell().getValue()) +
					Math.abs(current.getValue().getBuyers().getCurrencyToSell().getValue() -
							current.getValue().getSellers().getCurrencyToBuy().getValue() +seller.getCurrencyToBuy().getValue()));
			
			if(currentMismatch > cropAboveValue){
				//Not adding it
			}else{
				
				//Do the trimming here not really a function of adding a buyer, but to same time as  we are looping over the list anyway
				if(predecessor.getValue().getTotalMismatch()>(long)(trim*current.getValue().getTotalMismatch())){
					it.remove();
					this.nodes.remove(current); //Probably won't need this anymore
				}
				
				//We will Add the group
				//Build current groups
				buyerGroup = new TraderGroupUtil(current.getValue().getBuyers());
				sellerGroup = new TraderGroupUtil(current.getValue().getSellers());
				sellerGroup.add(seller);
				newNode = new TraderCombinationNode(buyerGroup,sellerGroup);
				newNodes.add(newNode);
				
				//Move predecessor
				predecessor = current;
			}
			
			
		}
		System.out.println("Building " + newNodes.size() + " nodes took:" + (System.nanoTime()-now));
		
		
		now = System.nanoTime();
		//Ok we have the list of nodes to insert, now insert them
		for(int i=0; i<newNodes.size(); i++){
			this.put(newNodes.get(i));
		}
	
		System.out.println("Inserting " + newNodes.size() + " nodes took:" + (System.nanoTime()-now));
	}

	/**
	 * Trim Tree Using keys instead of a map
	 * Careful, this does NOT remove nodes from the nodes list... :(
	 * @param delta
	 */
	@Deprecated
	public void trimTreeUsingKeys(float delta) {
		
		Set<Long> keys = this.tree.keySet();
		
		Iterator<Long> it = keys.iterator();
		
		float trim = (1-delta);
		
		Long predecessor = it.next();
		Long current = 0L;
		
		while(it.hasNext()){
			current = it.next();
			if(predecessor>(long)(trim*current)){
				//Yes trim it
				//System.out.println("Removing node: " + current);
				//System.out.println("Because: " + predecessor + " > " + (long)((1-delta)*current));
				it.remove();
			}
			predecessor = current;
		}
		
	}

	/**
	 * Does not remove any nodes from Node list. :(
	 * 
	 * @param delta
	 * @param cropAbove
	 */
	@Deprecated
	public void trimAndCropTreeUsingKeys(float delta, long cropAbove){
		Set<Long> keys = this.tree.keySet();
		
		Iterator<Long> it = keys.iterator();
		
		float trim = (1-delta);
		
		Long predecessor = it.next();
		Long current = 0L;
		
		while(it.hasNext()){
			current = it.next();
			if(current > cropAbove){
				it.remove();
			}else{
				if(predecessor>(long)(trim*current)){
					//Yes trim it
					//System.out.println("Removing node: " + current);
					//System.out.println("Because: " + predecessor + " > " + (long)((1-delta)*current));
					it.remove();
					
				}
			}
			predecessor = current;
		}
	}

	/**
	 * 
	 * Trim and Crop Tree at same time.  
	 * 
	 * NOT FULLY TESTED
	 * 
	 * @param delta
	 * @param cropAbove
	 */
	@Deprecated
	public void trimAndCropTree(float delta, float cropAbove){
		Set<Entry<Long, TraderCombinationNode>> keys = this.tree.entrySet();
		
		Iterator<Entry<Long, TraderCombinationNode>> it = keys.iterator();
		
		float trim = (1-delta);
		
		long cropAboveValue = tree.get(this.tree.lastKey()).getTotalMismatch();
		
		if(this.tree.size() > 2){
			cropAboveValue = (long) (cropAboveValue*(1-cropAbove));
		}
		
		Entry<Long,TraderCombinationNode> predecessor = it.next();
		Entry<Long,TraderCombinationNode> current;
		
		while(it.hasNext()){
			current = it.next();
			if(current.getValue().getTotalMismatch() > cropAboveValue){
				this.nodes.remove(current.getValue());
				it.remove();
				//predecessor doesn't change, the whole tree just shifted down.
			}else if(predecessor.getValue().getTotalMismatch()>(long)(trim*current.getValue().getTotalMismatch())){
					//Yes trim it
					//System.out.println("Removing node: " + current);
					//System.out.println("Because: " + predecessor + " > " + (long)((1-delta)*current));
					this.nodes.remove(current.getValue());
					it.remove();
					//predecessor doesn't change
			}else{
				predecessor = current;
			}
		}
	}



}


