package com.silicolife.metabolimodelanalysis.avaliators.simulations;

import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class LPSolutionType implements Evaluators<SteadyStateSimulationResult>{

	
	
	@Override
	public String getHeaders() {
		return "Solution Type";
	}

	@Override
	public String getValues(SteadyStateSimulationResult obj) {
		
		String ret = null;
		
		if(obj!=null ){
			
			ret = obj.getSolutionType().toString();
		}
		
		return ret;
	}

}
