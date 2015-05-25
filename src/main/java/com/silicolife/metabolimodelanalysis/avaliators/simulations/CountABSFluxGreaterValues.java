package com.silicolife.metabolimodelanalysis.avaliators.simulations;

import java.util.Set;

import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.SimulationResultMethods;

public class CountABSFluxGreaterValues implements Evaluators<SteadyStateSimulationResult>{

	
	protected Double value;
	public CountABSFluxGreaterValues(Double value){
		this.value = Math.abs(value);
	}
	
	@Override
	public String getHeaders() {
		return "#FluxValues > " +value;
	}

	@Override
	public String getValues(SteadyStateSimulationResult obj) {
		
		String ret = null;
		
		if(obj!=null && SimulationResultMethods.isModelSimulated(obj)){
			
			Set<String> reactions = SimulationResultMethods.getFluxGreaterThan(obj, value);
			
			ret = reactions.size()+"";
		}
		
		return ret;
	}

}
