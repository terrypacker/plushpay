/**
 * 
 */
package com.payyourself.trading.tradeGenerator;

import java.util.ArrayList;
import java.util.List;

import com.payyourself.trading.tradeGenerator.settings.TaskSettings;
import com.payyourself.trading.tradeGenerator.settings.TaskSettingsHibernation;

/**
 * @author tpacker
 *
 */
public class TradeGeneratorControl {
	
	private TaskSettings settings;
	private boolean updateConfirmed;
	private List<Boolean> runStates;
	
	
	public List<Boolean> getRunStates() {
		return runStates;
	}

	public void setRunStates(List<Boolean> runStates) {
		this.runStates = runStates;
	}

	public TradeGeneratorControl() {
		
		this.runStates = new ArrayList<Boolean>();
		this.runStates.add(true);
		this.runStates.add(false);
		
		this.setUpdateConfirmed(false);
		TaskSettingsHibernation sh = new TaskSettingsHibernation();
		this.setSettings(sh.loadAllSettings().get(0));
		
		
	}

	public void updateSettings(){
		
		TaskSettingsHibernation sh = new TaskSettingsHibernation();
		sh.merge(this.settings);
		this.setUpdateConfirmed(true);
		
	}

	public void setSettings(TaskSettings settings) {
		this.settings = settings;
	}


	public TaskSettings getSettings() {
		return settings;
	}

	public void setUpdateConfirmed(boolean updateConfirmed) {
		this.updateConfirmed = updateConfirmed;
	}

	public boolean isUpdateConfirmed() {
		return updateConfirmed;
	}
}
