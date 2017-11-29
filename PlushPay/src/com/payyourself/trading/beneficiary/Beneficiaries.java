package com.payyourself.trading.beneficiary;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;


import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.trading.beneficiary.details.AudDetails;
import com.payyourself.trading.beneficiary.details.UsdDetails;
import com.payyourself.userManagement.user.User;
import com.payyourself.userManagement.user.UserHibernation;

/**
 * Bean to handle adding and removing and updating
 * 
 * @author tpacker
 *
 */
public class Beneficiaries {

	private boolean updateConfirmed;
	
	private User user; //Where all user prefs are stored (in DB)
	
	//Beneficiary Inputs
	private long id;
	private String name;
	private UsdDetails usdDetails;
	private AudDetails audDetails;


	public Beneficiaries(){
		
		this.setUpdateConfirmed(false);
		//Get current user for trade
		String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		UserHibernation userh = new UserHibernation();
		this.setUser((User)userh.findById(username));
		
		this.name = "";
		this.audDetails = new AudDetails();
		this.usdDetails = new UsdDetails();

	}
	
	
	/**
	 * Add the new Beneficiary if one with that name doesn't already exist
	 * the validation on the form will
	 * ensure the new Benficiary is complete with info
	 * @return
	 */
	public void addUsdBeneficiary(ActionEvent event){

		boolean okToAdd = true;
		
		/*Check to see if this name already exists*/
		for(int i=0; i<this.user.getBeneficiaries().size(); i++){
			if(this.user.getBeneficiaries().get(i).getName().equals(this.name)){
				okToAdd = false;
				break;
			}
		}
		
		if(okToAdd){
			Beneficiary newBeneficiary = new Beneficiary(this.name,CurrencyCodeEnum.USD,this.usdDetails);

			this.user.getBeneficiaries().add(newBeneficiary);
			
			UserHibernation uh = new UserHibernation();
			uh.persist(this.user);
			
			this.name = "";
			this.usdDetails = new UsdDetails();			
		}else{
			//SHOULD SHOW SOME STATUS THAT THE NAME ALREADY EXISTS
		}
		
	}

	/**
	 * Update a Beneficiary
	 * @param event
	 */
	public void updateUsdBeneficiary(ActionEvent event){
		
		BeneficiaryHibernation bh = new BeneficiaryHibernation();
	
		Beneficiary editedBene = bh.findById(this.id);
		
		editedBene.setDetails(this.usdDetails);
		editedBene.setName(this.name);
		bh.merge(editedBene);
		
	}
	
	/**
	 * Helper to be able to edit Bene in modal window
	 * @param event
	 */
	public void beneficiaryEdit(ActionEvent event){
		
		Beneficiary beneToEdit = (Beneficiary)event.getComponent().getAttributes().get("beneToEdit");
		this.name = beneToEdit.getName();
		this.id = beneToEdit.getBeneficiaryId();
		this.setId(beneToEdit.getBeneficiaryId());
		if(beneToEdit.getType().equals("USD")){
			this.usdDetails = (UsdDetails) beneToEdit.getDetails();
		}else if(beneToEdit.getType().equals("AUD")){
			this.audDetails = (AudDetails) beneToEdit.getDetails();
		}
		
	}
	
	/**
	 * Add the new Beneficiary if one with that name doesn't already exist
	 * the validation on the form will
	 * ensure the new Benficiary is complete with info
	 * @return
	 */
	public void addAudBeneficiary(ActionEvent event){

		boolean okToAdd = true;
		
		/*Check to see if this name already exists*/
		for(int i=0; i<this.user.getBeneficiaries().size(); i++){
			if(this.user.getBeneficiaries().get(i).getName().equals(this.name)){
				okToAdd = false;
				break;
			}
		}
		
		if(okToAdd){
			Beneficiary newBeneficiary = new Beneficiary(this.name,CurrencyCodeEnum.AUD,this.audDetails);
	
			this.user.getBeneficiaries().add(newBeneficiary);
			
			UserHibernation uh = new UserHibernation();
			uh.persist(this.user);
			
			//Create an empty Beneficiary to use next
			this.name = "";
			this.audDetails = new AudDetails();
		}else{
			//SHOULD SHOW SOME STATUS THAT THE NAME ALReADY EXIStS
		}

	}
	
	/**
	 * Update a Beneficiary
	 * @param event
	 */
	public void updateAudBeneficiary(ActionEvent event){
		
		BeneficiaryHibernation bh = new BeneficiaryHibernation();
		
		//Need to get details as fparams.
		Beneficiary editedBene = bh.findById(this.id);
		
		editedBene.setDetails(this.audDetails);
		editedBene.setName(this.name);
		bh.merge(editedBene);
		
	}

	public void setUser(User user) {
		this.user = user;
	}


	public User getUser() {
		return user;
	}


	public void setUpdateConfirmed(boolean updateConfirmed) {
		this.updateConfirmed = updateConfirmed;
	}


	public boolean isUpdateConfirmed() {
		return updateConfirmed;
	}
	
	public void setUsdDetails(UsdDetails usdDetails) {
		this.usdDetails = usdDetails;
	}


	public UsdDetails getUsdDetails() {
		return usdDetails;
	}


	public void setAudDetails(AudDetails audDetails) {
		this.audDetails = audDetails;
	}


	public AudDetails getAudDetails() {
		return audDetails;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setId(long idToEdit) {
		this.id = idToEdit;
	}


	public long getId() {
		return id;
	}




}
