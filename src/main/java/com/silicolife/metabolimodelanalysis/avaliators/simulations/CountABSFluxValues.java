package com.silicolife.metabolimodelanalysis.avaliators.simulations;

import java.util.Set;

import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.SimulationResultMethods;

public class CountABSFluxValues implements Evaluators<SteadyStateSimulationResult>{

	
	protected Double value;
	public CountABSFluxValues(Double value){
		this.value = Math.abs(value);
	}
	
	@Override
	public String getHeaders() {
		return "#FluxValues >= " +value;
	}

	@Override
	public String getValues(SteadyStateSimulationResult obj) {
		
		String ret = null;
		
		if(obj!=null){
			
			Set<String> reactions = SimulationResultMethods.getFluxGreaterOrEqualThan(obj, value);
			
			ret = reactions.size()+"";
		}
		
		return ret;
	}

}
