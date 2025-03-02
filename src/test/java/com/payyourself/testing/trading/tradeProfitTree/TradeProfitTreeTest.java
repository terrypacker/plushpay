package com.payyourself.testing.trading.tradeProfitTree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.PyCurrencyUtil;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.trading.beneficiary.Beneficiary;
import com.payyourself.trading.beneficiary.details.BeneficiaryDetails;
import com.payyourself.trading.beneficiary.tradeBeneficiary.TradeBeneficiary;
import com.payyourself.trading.tradeProfitTree.TradeProfitNode;
import com.payyourself.trading.tradeProfitTree.TradeProfitTree;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderStatus;
import com.payyourself.trading.trader.group.TraderGroup;
import com.payyourself.trading.trader.group.TraderGroupUtil;
import com.payyourself.trading.traderSimulation.TradersSimulation;
import com.payyourself.userManagement.user.User;

/**
 * All test on tree assume:
 *  Buyers(buy AUD, sell USD)
 *  Sellers(buy USD, sell AUD)
 *  
 * @author tpacker
 *
 */
public class TradeProfitTreeTest {
	
	
	@Test
	public void testInsertSeller() {

		//Create the currency Types for the test
		PyCurrencyType audType = new PyCurrencyType(CurrencyCodeEnum.AUD,9200,"$",Calendar.getInstance());
		PyCurrencyType usdType = new PyCurrencyType(CurrencyCodeEnum.USD,10000,"$",Calendar.getInstance());

		
		//Create Tree
		TradeProfitTree tree = new TradeProfitTree();
		
		
		//Add the 0 Traders to the system
		PyCurrency buyZero = new PyCurrency(0,audType);
		//buyZero.setType(this.buyingCurrency);
		//buyZero.setValue(0);
		
		PyCurrency sellZero = new PyCurrency(0,usdType);
		//sellZero.setType(this.sellingCurrency);
		//sellZero.setValue(0);
		
		Trader zeroBuyer = new Trader();
		zeroBuyer.setCurrencyToBuy(buyZero);
		zeroBuyer.setCurrencyToSell(sellZero);
		
		Trader zeroSeller = new Trader();
		zeroSeller.setCurrencyToBuy(sellZero);
		zeroSeller.setCurrencyToSell(buyZero);
		
		TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
		TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
		TradeProfitNode zeroNode = new TradeProfitNode(zeroBuyers, zeroSellers);

		tree.put(zeroNode); //Put the 0 node in (Required for tree to work.)
		
		//Create Seller Reqmnts
		
		List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
		
		BeneficiaryDetails details = new BeneficiaryDetails();
		Beneficiary usdBene = new Beneficiary("usdBene", CurrencyCodeEnum.USD,
				details);
		Beneficiary audBene = new Beneficiary("audBene", CurrencyCodeEnum.USD,
				details);
		
		beneficiaries.add(usdBene);
		beneficiaries.add(audBene);
		
		User user = new User("tpacker", "Terry", "Packer",
				"shithead", "tpacker@mail2nebraska.com", true,
				beneficiaries);
		
		
		PyCurrency currencyToBuy = new PyCurrency(10000,audType); //Buying AUD
		PyCurrency currencyToSell = PyCurrencyUtil.createCurrency(10000, audType, usdType); //Selling USD

		List<TradeBeneficiary> tradeBenies = new ArrayList<TradeBeneficiary>();
		TradeBeneficiary tradeBene = new TradeBeneficiary(new PyCurrency(currencyToBuy.getValue(),audType),audBene);
		tradeBenies.add(tradeBene);
		
		TraderGroup group = null;
		
		Trader buyer = new Trader(group,user, currencyToBuy,
				currencyToSell, TraderStatus.CONFIRMED.name(),
				tradeBenies);
		
		try {
			tree.insertBuyer(buyer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Get the best match
		TradeProfitNode best = tree.getBest(); 

		//Remove the 0 trader (Has to happen)
		for(int i=0; i<best.getBuyers().size(); i++){
			//Set all trader status to DEPOSIT, awaiting deposit.
			//best.getBuyers().get(i).setStatus(TraderStatus.DEPOSIT.name());
			if(best.getBuyers().get(i).getCurrencyToBuy().getValue() == 0){
				try {
					best.getBuyers().remove(i);
					i--; //Step back because we just removed one
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
			}

		}

		assertEquals(best.getBuyers().get(0),buyer);
		
		
	}

	@Test
	public void testInsertBuyer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLastKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

	@Test
	public void testPut() {
		fail("Not yet implemented");
	}

	@Test
	public void testTrimTree() {
		fail("Not yet implemented");
	}

	@Test
	public void testCropTree() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetTree() {
		fail("Not yet implemented");
	}

	@Test
	public void testSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBest() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNodes() {
		fail("Not yet implemented");
	}

}
