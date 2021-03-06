package com.payyourself.trading.chain;

// Generated Apr 24, 2010 1:54:05 PM by Hibernate Tools 3.2.5.Beta

import java.util.ArrayList;
import java.util.List;

/**
 * TradeChain generated by hbm2java
 */
public class TradeChain implements java.io.Serializable {

	private int chainid;
	private List<TradeLink> links = new ArrayList<TradeLink>(0);

	public TradeChain() {
	}

	public TradeChain(int chainid) {
		this.chainid = chainid;
	}

	public TradeChain(int chainid, List<TradeLink> links) {
		this.chainid = chainid;
		this.links = links;
	}

	public int getChainid() {
		return this.chainid;
	}

	public void setChainid(int chainid) {
		this.chainid = chainid;
	}

	public List<TradeLink> getLinks() {
		return this.links;
	}

	public void setLinks(List<TradeLink> links) {
		this.links = links;
	}

}
