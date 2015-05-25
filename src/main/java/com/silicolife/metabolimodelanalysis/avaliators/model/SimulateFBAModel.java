package com.silicolife.metabolimodelanalysis.avaliators.model;

import pt.uminho.ceb.biosystems.mew.mewcore.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class SimulateFBAModel implements Evaluators<SteadyStateModel>{

	
	protected EnvironmentalConditions ec = null;
	
	public SimulateFBAModel(EnvironmentalConditions ec){
		this.ec = ec;
		
	}
	
	public SimulateFBAModel(){
		this.ec = null;
		
	}
	
	@Override
	public String getHeaders() {
		return "Max Biomass";
	}

	@Override
	public String getValues(SteadyStateModel obj) {
		Double ret = null;
		if(obj!= null){
			
			SteadyStateSimulationResult result = MethodsWithMemory.wt(obj, ec);
			if(result!= null)
			ret = result.getFluxValues().get(obj.getBiomassFlux());
			
		}
		return ret+"";
	}

	
	
}
