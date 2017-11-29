package com.payyourself.trading.tradeProfitTree;

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


public class TradeProfitTree {

	private Logger log;
	private TreeMap<TradeProfitKey,TradeProfitNode> tree; //Java Collections Red Black Tree
	
	
	public TradeProfitTree(){
		
		this.tree = new TreeMap<TradeProfitKey, TradeProfitNode>();
		this.log = LogfileFactory.getHTMLLogger(Level.ALL, this.getClass());
		
	}
	
	/**
	 * Basic insert seller, this is the Most tested insert seller.
	 * @param seller
	 * @throws Exception 
	 */
	public void insertSeller(Trader seller) throws Exception{
		
		
		long now = System.nanoTime();
		
		//Get a copy of the Tree
		TreeMap<TradeProfitKey,TradeProfitNode> cloned = (TreeMap<TradeProfitKey, TradeProfitNode>) this.tree.clone();
		
		Iterator<Entry<TradeProfitKey, TradeProfitNode>> it = cloned.entrySet().iterator();
		
		TradeProfitNode currentNode = it.next().getValue();
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(currentNode.getBuyers());
		
		TraderGroupUtil sellerGroup = new TraderGroupUtil(currentNode.getSellers());
		sellerGroup.add(seller);
		
		//The newNode we will be using
		TradeProfitNode newNode = new TradeProfitNode(buyerGroup,sellerGroup);
		this.put(newNode); //Ad the new node no matter what
		
		//Now walk the cloned tree and add the members
		while(it.hasNext()){
			newNode = it.next().getValue();
			//We will Add the group
			//Build current groups
			buyerGroup = new TraderGroupUtil(newNode.getBuyers());
			sellerGroup = new TraderGroupUtil(newNode.getSellers());
			sellerGroup.add(seller);
			newNode = new TradeProfitNode(buyerGroup,sellerGroup);
			this.put(newNode);
			
		}
		
		
		this.log.debug("Building and Inserting " + cloned.size() + " nodes took:" + ((System.nanoTime()-now)/1000000) + "ms");

	}
	
	/**
	 * 
	 * @param buyer
	 * @throws Exception 
	 */
	public void insertBuyer(Trader buyer) throws Exception{
		
		long now = System.nanoTime();
		
		//Get a copy of the Tree
		TreeMap<TradeProfitKey,TradeProfitNode> cloned = (TreeMap<TradeProfitKey, TradeProfitNode>) this.tree.clone();
		
		Iterator<Entry<TradeProfitKey, TradeProfitNode>> it = cloned.entrySet().iterator();
		
		TradeProfitNode currentNode = it.next().getValue();
		//The groups
		TraderGroupUtil buyerGroup = new TraderGroupUtil(currentNode.getBuyers());
		buyerGroup.add(buyer);

		TraderGroupUtil sellerGroup = new TraderGroupUtil(currentNode.getSellers());
		
		//The newNode we will be using
		TradeProfitNode newNode = new TradeProfitNode(buyerGroup,sellerGroup);
		//Just add the buyer (No Matter What)
		this.put(newNode);
		
		//Now walk the cloned tree and add the members
		while(it.hasNext()){
			newNode = it.next().getValue();
			//We will Add the group
			//Build current groups
			buyerGroup = new TraderGroupUtil(newNode.getBuyers());
			buyerGroup.add(buyer);

			sellerGroup = new TraderGroupUtil(newNode.getSellers());

			newNode = new TradeProfitNode(buyerGroup,sellerGroup);
			this.put(newNode);
			
		}
	
		this.log.debug("Building and Inserting " + cloned.size() + " nodes took:" + ((System.nanoTime()-now)/1000000) + "ms");
		
		
	}

	public TradeProfitKey lastKey() {
		return this.tree.lastKey();
	}

	public TradeProfitNode get(TradeProfitKey key) {
		return this.tree.get(key);
	}

	/**
	 * Put a Trader Combination Node into the tree.
	 * @param newNode
	 */
	public void put(TradeProfitNode newNode) {
	
		
		
		//When we put a node into the map if it will return the key if it already exists,
		// so we check for that here, to keep the Nodes and Tree in synch.
		if(	this.tree.put(newNode.getKey(), newNode) == null ){
			
		}else{
			this.log.debug("Key already exists in map!.");
			
		}
	}

	
	
	/**
	 * Trim the tree by removing anyone within 1-delta of the previous node.
	 * 
	 * The tree is in Descending Order
	 * 
	 * Smaller delta, less nodes removed.
	 * 
	 * Larger delta, more nodes removed.
	 * 
	 * @param delta
	 */
	public int trimTree(double delta){
		int i = 0;
		Set<Entry<TradeProfitKey, TradeProfitNode>> keys = this.tree.entrySet();
		
		Iterator<Entry<TradeProfitKey, TradeProfitNode>> it = keys.iterator();
		
		double trim = (1-delta);
		
		Entry<TradeProfitKey,TradeProfitNode> predecessor = it.next();
		Entry<TradeProfitKey,TradeProfitNode> current;
		
		while(it.hasNext()){
			current = it.next();
	
			//TODO Speed this up when we are sure it is working
			
			//Is the current node > trim*predecessor?  if so drop it they are close enough to be an estimate of each other
			// note the sign as multiplying a - number by a number < 1 will increase its value
			long predGain = (long)(trim*predecessor.getKey().getGain());
			if(predGain < 0){
				predGain = -predGain;
			}
			
			long curGain = current.getKey().getGain();
			if(curGain < 0 ){
				curGain = -curGain;
			}
			
			
			if(curGain>predGain){
				//Yes trim it
				it.remove();
				i++;
				
			}
			predecessor = current;
			
		}
		
		this.log.debug("Trimmed " + i + " members from tree.");
		return i;
		
	}

	/**
	 * Reset the nodes and the tree to empty
	 */
	public void resetTree() {
		this.tree = new TreeMap<TradeProfitKey,TradeProfitNode>();
		
	}

	public int size() {
		return this.tree.size();
	}

	/**
	 * Print the keys of the tree, in order
	 */
	public void printKeys() {
		
		Set<TradeProfitKey> keys = this.tree.keySet();
		
		Iterator<TradeProfitKey> it = keys.iterator();
		it.next(); //Burn the 0 traderNode
		while(it.hasNext()){
			TradeProfitKey value = it.next();
			
			System.out.println(value);
		}
		
	}

	/**
	 * Return the closest match in the tree.
	 * @return
	 */
	public TradeProfitNode getBest() {
		
		Set<Entry<TradeProfitKey, TradeProfitNode>> values = this.tree.entrySet();
		
		Iterator<Entry<TradeProfitKey, TradeProfitNode>> it = values.iterator();
		
		
		if(this.size() > 1){
			//it.next(); //The 0 trader
			return (TradeProfitNode)it.next().getValue();
		}else{
			return null; //we won't return the 0 trader ever.
		}
		
	}

	/**
	 * CONCEPT 
	 * 
	 * Return the node with a gain > gain and cost < cost
	 * @return
	 */
	public TradeProfitNode getBest(long gain, long cost) {
		
		Set<Entry<TradeProfitKey, TradeProfitNode>> values = this.tree.entrySet();
		
		Iterator<Entry<TradeProfitKey, TradeProfitNode>> it = values.iterator();
		
		
		if(this.size() > 1){
			Entry<TradeProfitKey, TradeProfitNode> entry;
			
			//We will walk from highest gain to lowest
			while(it.hasNext()){
				entry = it.next();
				
				//Compare to see
				if(entry.getKey().getGain() < gain){
					return null; //Didn't find one good enough
				}
				
				if(entry.getKey().getCost() < cost){
					return entry.getValue();
				}
				
			}
			
		}
		
		return null;
		
	}
	

	/**
	 * Return the entry set of the tree
	 * @return
	 */
	@Deprecated
	public Set<Map.Entry<TradeProfitKey, TradeProfitNode>> entrySet() {
		return tree.entrySet();
		
	}




}


