package com.payyourself.trading.trade;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.payyourself.currency.PyCurrency;

import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;

import com.payyourself.trading.beneficiary.Beneficiary;
import com.payyourself.trading.beneficiary.BeneficiaryHibernation;
import com.payyourself.trading.beneficiary.details.AudDetails;
import com.payyourself.trading.beneficiary.details.UsdDetails;
import com.payyourself.trading.beneficiary.tradeBeneficiary.TradeBeneficiary;

import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;
import com.payyourself.trading.trader.TraderStatus;

import com.payyourself.userManagement.user.User;
import com.payyourself.userManagement.user.UserHibernation;


/**
 * this class needs cleaning because we now use the Trade Generator Thread.
 * @author tpacker
 *
 */
public class CreateTrade {
	
	private Trader newTrader;
	private List<PyCurrencyType> availableCurrencies;
	private List<String> availableBeneficiaries;
	private String selectedBeneficiary;

	private List<TradeBeneficiary> selectedBeneficiaries; //Beneficiaries for Trade
	private float beneficiaryAmount; //Amount for bene to be added to trade
	private float beneficiaryMaxAllowed; 
	
	private String status; //Status for trade
	
	private boolean created; //Flag to avoid accessing wrong page
	private boolean beneficiaryCorrect; //Flag to ensure the beneis add up to full trade amount
	
	
	private float toBuy; //Value of currency to Buy
	private long desiredRate; //Exchange Rate for Trade
	private String beneficiaryName;
	private long beneficiaryId;
	private float beneficiaryEditAmount;
	
	
	/**
	 * Setup for a session request bean trade instance.
	 */
	public CreateTrade(){

		this.newTrade();

	}
	
	
	
	/**
	 * Helper to be able to edit Bene in modal window
	 * @param event
	 */
	public void beneficiaryEdit(ActionEvent event){
		
		TradeBeneficiary beneToEdit = (TradeBeneficiary)event.getComponent().getAttributes().get("beneToEdit");
		this.setBeneficiaryName(beneToEdit.getBeneficiary().getName());
		this.setBeneficiaryId(beneToEdit.getId());
		this.setBeneficiaryEditAmount(beneToEdit.getAmount().getValue()/10000);
		
	}
	
	/**
	 * Update Beneficiary for trade
	 * @param event
	 */
	public void updateBeneficiary(ActionEvent event){
		
		for(int i=0; i<this.selectedBeneficiaries.size(); i++){
			
			//Find the one to edit
			if(this.selectedBeneficiaries.get(i).getId() == this.beneficiaryId){
				PyCurrency amount = new PyCurrency();
				PyCurrencyType type = new PyCurrencyType(this.newTrader.getCurrencyToBuy().getType());
				amount.setType(type);
				amount.setValue((long)(this.beneficiaryEditAmount*10000L));
				
				this.selectedBeneficiaries.get(i).setAmount(amount);
				break;
			}
		}
		
		//Recompute the allowed amounts.
		this.calculateBeneficiaryMaxAllowed();
		this.checkBeneficiary(); //Check to see if we are still sweet.
	}
	
	/**
	 * Add a beneficary to the trade
	 * @param event
	 */
	public void addBeneficiary(ActionEvent event){

		//Check that we aren't adding too much
		
		for(int i=0; i<this.newTrader.getUser().getBeneficiaries().size(); i++){
			
			if(this.newTrader.getUser().getBeneficiaries().get(i).getName().equals(this.selectedBeneficiary)){
				PyCurrency amount = new PyCurrency();
				PyCurrencyType type = new PyCurrencyType(this.newTrader.getCurrencyToBuy().getType());
				amount.setType(type);
				amount.setValue((long)(this.beneficiaryAmount*10000L));
				//Create a new TradeBeneficiary
				TradeBeneficiary newBene = new TradeBeneficiary(amount,this.newTrader.getUser().getBeneficiaries().get(i));
				this.selectedBeneficiaries.add(newBene);
				
				break;
			}
		}
		
		//Now remove him from the available list
		for(int i=0; i<this.availableBeneficiaries.size(); i++){
			if(this.availableBeneficiaries.get(i).equals(this.selectedBeneficiary)){
				this.availableBeneficiaries.remove(i);
				break;
			}
		}

		//Recompute the allowable amount
		this.calculateBeneficiaryMaxAllowed();
		this.selectedBeneficiary = new String();
		
	}
	
	/**
	 * Remove the selected Beneficiary
	 * @param event
	 */
	public void removeBeneficiary(ActionEvent event){
		
		TradeBeneficiary beneToEdit = (TradeBeneficiary)event.getComponent().getAttributes().get("beneToRemove");
		//Remove from the list
		this.selectedBeneficiaries.remove(beneToEdit);
		
		//Add back to the available list
		this.availableBeneficiaries.add(beneToEdit.getBeneficiary().getName());
		
		this.calculateBeneficiaryMaxAllowed();
		this.checkBeneficiary(); //Check if we are still good, we won't be
	}
	
	
	/**
	 * Prepare a trade for confirmation.
	 * @return
	 * @throws Exception 
	 */
	public void create(ActionEvent event) throws Exception{

		//Make sure we are going to trade the values that are on the page
		// if something went wrong or wierd.
		//This is a workaround for the richinputnumberspinner submitting
		// the from on a enter keypress
		this.computeExchangeRate(null);

		//Flag that we have data for the confimration pages
		//Check to see that we have correct beneficiary-ness
		if((this.beneficiaryMaxAllowed == 0f)&&(this.selectedBeneficiaries.size() > 0)){
			this.created = true;
			
			
		}
		
	}
	
	
	
	public void confirm(ActionEvent event){
	
		/*This trader needs to be added to DB, but make sure we don't add twice */
		TraderHibernation th = new TraderHibernation();
		this.newTrader.setBeneficiaries(this.selectedBeneficiaries);
		this.newTrader.setStatus(TraderStatus.CONFIRMED.name());
		this.newTrader = th.merge(this.newTrader);
		
		this.newTrade(); //Reset Trade 
		

	}
	
	/**
	 * Cancel the current trade and reset inputs.
	 * @param event
	 */
	public void cancel(ActionEvent event){
		
	}

	/**
	 * Create a new trade with all new parts.
	 * @return
	 */
	public String newTrade(){
		
		this.selectedBeneficiaries = new ArrayList<TradeBeneficiary>();
		this.selectedBeneficiary = new String();
		this.beneficiaryAmount = 0f;
		
		this.checkBeneficiary();
		
		this.setStatus(new String());
		this.newTrader = new Trader();
		this.created = false;

		PyCurrencyTypeHibernation currencyh = new PyCurrencyTypeHibernation();

		PyCurrency currencyToBuy = new PyCurrency(1,currencyh.getBaseCurrency());
		currencyToBuy.setValue(1);
		
		PyCurrency currencyToSell = new PyCurrency(0,currencyh.getCurrentAud());

		this.newTrader.setCurrencyToBuy(currencyToBuy);
		this.newTrader.setCurrencyToSell(currencyToSell);
		this.toBuy = 1.0f;

		this.computeExchangeRate(null);
		
		//Get current user for trade
		String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		UserHibernation userh = new UserHibernation();
		this.newTrader.setUser((User)userh.findById(username));
		
		/* Compute the exchange rate */
		this.computeExchangeRate(null);
		
		
		this.availableCurrencies = currencyh.loadAllTypes();//currencyh.loadAllSelectItems();
		
		/*Deal with the beneficiary */
		this.findAvailableBeneficiaries();


		
		return "create_trade";
	}
	
	/**
	 * 
	 * @param event
	 */
	public void selectedCurrencyToBuy(ActionEvent event){
		
		//Compute new Exchange rate and set values
		this.computeExchangeRate(null);
		
		//Modify the beneficiaries List
		this.findAvailableBeneficiaries();
		
		this.selectedBeneficiaries = new ArrayList<TradeBeneficiary>();
	}
	
	/**
	 * 
	 * @param event
	 */
	public void selectedCurrencyToSell(ActionEvent event){
		
		//Compute new Exchange rate and set values
		this.computeExchangeRate(null);
		
	}
	
	/**
	 * 
	 */
	private void findAvailableBeneficiaries() {
		
		BeneficiaryHibernation bh = new BeneficiaryHibernation();
		List<Beneficiary> benies = bh.findAllOfTypeForTrader(this.newTrader.getUser().getUsername(),this.newTrader.getCurrencyToBuy().getType().getCode());
		
		this.availableBeneficiaries = new ArrayList<String>();
		
		for(int i=0; i<benies.size(); i++){
			
			this.availableBeneficiaries.add(benies.get(i).getName());
		}
		
		//Check to see if selected benie has correct currency
		for(int i=0; i<this.newTrader.getUser().getBeneficiaries().size(); i++){
			if(this.newTrader.getUser().getBeneficiaries().get(i).getName().equals(this.selectedBeneficiary)){
				//This is the one we have
				if(this.newTrader.getUser().getBeneficiaries().get(i).getType() != this.newTrader.getCurrencyToBuy().getType().getCode()){
					this.selectedBeneficiary = this.availableBeneficiaries.get(0); //Set him to the first because it was not the type we need.
				}
			}
		}
		
	}

	public void computeExchangeRate(){
		this.computeExchangeRate(null);
	}
	/**
	 * Value change listener to compute the exchange rates
	 * and fill the values of the new trader's currency
	 */
	public void computeExchangeRate(ActionEvent event){
		
		//First check the amount to buy, if > trader's limit then reset it
		//SHould do a DB lookup of User limits here
		if(this.toBuy>50000){
			this.toBuy= 50000;
		}
		
		this.desiredRate = (this.newTrader.getCurrencyToBuy().getType().getRateToBase()*10000)/this.newTrader.getCurrencyToSell().getType().getRateToBase();
		
		//Convert the value to a long
		this.newTrader.getCurrencyToBuy().setValue((long)this.toBuy*10000);
		
		long sell = (this.newTrader.getCurrencyToBuy().getValue()*this.desiredRate)/10000;
		this.newTrader.getCurrencyToSell().setValue(sell);
		
		//Compute the max allowable for a beneficiary
		this.calculateBeneficiaryMaxAllowed();
		
		
	}
	
	private void checkBeneficiary() {
		//Flag that we have data for the confimration pages
		//Check to see that we have correct beneficiary-ness
		if((this.beneficiaryMaxAllowed == 0f)&&(this.selectedBeneficiaries.size() > 0)){
			this.beneficiaryCorrect = true;
			
			
		}else{
			this.beneficiaryCorrect = false;
		}
		
	}



	/**
	 * Determine how much is left to add to a beneficiary
	 */
	private void calculateBeneficiaryMaxAllowed() {
		
		long total = 0;
		//Total up all beneficiaries
		for(int i= 0; i<this.selectedBeneficiaries.size(); i++){
			total = total + this.selectedBeneficiaries.get(i).getAmount().getValue();
		}
		
		float totalF = total/10000;
		this.beneficiaryMaxAllowed = this.toBuy - totalF;
		
		this.beneficiaryAmount = this.beneficiaryMaxAllowed;
		
		this.checkBeneficiary(); //Check to see if we can do a trade yet.

	}



	public Trader getNewTrader(){
		return this.newTrader;
	}
	
	public void setNewTrader(Trader trader){
		this.newTrader = trader;
	}


	public void setAvailableCurrencies(List<PyCurrencyType> availableCurrencies) {
		this.availableCurrencies = availableCurrencies;
	}


	public List<PyCurrencyType> getAvailableCurrencies() {
		return availableCurrencies;
	}

	public void setDesiredRate(long desiredRate) {
		this.desiredRate = desiredRate;
	}

	public long getDesiredRate() {
		return desiredRate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public boolean isCreated() {
		return created;
	}

	public void setToBuy(float toBuy) {
		this.toBuy = toBuy;
	}

	public float getToBuy() {
		return toBuy;
	}

	public void setSelectedBeneficiary(String selectedBeneficiary) {
		this.selectedBeneficiary = selectedBeneficiary;
	}

	public String getSelectedBeneficiary() {
		return selectedBeneficiary;
	}

	public List<String> getAvailableBeneficiaries() {
		return availableBeneficiaries;
	}

	public void setAvailableBeneficiaries(List<String> availableBeneficiaries) {
		this.availableBeneficiaries = availableBeneficiaries;
	}

	public void setSelectedBeneficiaries(List<TradeBeneficiary> selectedBeneficiaries) {
		this.selectedBeneficiaries = selectedBeneficiaries;
	}

	public List<TradeBeneficiary> getSelectedBeneficiaries() {
		return selectedBeneficiaries;
	}

	public void setBeneficiaryAmount(float beneficiaryAmount) {
		this.beneficiaryAmount = beneficiaryAmount;
	}

	public float getBeneficiaryAmount() {
		return beneficiaryAmount;
	}



	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}



	public String getBeneficiaryName() {
		return beneficiaryName;
	}



	public void setBeneficiaryId(long beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}



	public long getBeneficiaryId() {
		return beneficiaryId;
	}



	public void setBeneficiaryMaxAllowed(float beneficiaryMaxAllowed) {
		this.beneficiaryMaxAllowed = beneficiaryMaxAllowed;
	}



	public float getBeneficiaryMaxAllowed() {
		return beneficiaryMaxAllowed;
	}



	public void setBeneficiaryEditAmount(float beneficiaryEditAmount) {
		this.beneficiaryEditAmount = beneficiaryEditAmount;
	}



	public float getBeneficiaryEditAmount() {
		return beneficiaryEditAmount;
	}



	public void setBeneficiaryCorrect(boolean beneficiaryCorrect) {
		this.beneficiaryCorrect = beneficiaryCorrect;
	}



	public boolean isBeneficiaryCorrect() {
		return beneficiaryCorrect;
	}
	


}
