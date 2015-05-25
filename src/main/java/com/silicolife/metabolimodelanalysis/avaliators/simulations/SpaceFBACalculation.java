package com.silicolife.metabolimodelanalysis.avaliators.simulations;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;
import com.silicolife.metabolimodelanalysis.mains.SimulationResultMethods;

public class SpaceFBACalculation implements Evaluators<SteadyStateSimulationResult>{

	
	
	
	@Override
	public String getHeaders() {
		return "#Space reactions with Flux FBA";
	}

	@Override
	public String getValues(SteadyStateSimulationResult obj) {
		String ret = null;
		if(obj != null){
			Set<String> test = new HashSet<String>(obj.getModel().getReactions().keySet());
			test.removeAll(MethodsWithMemory.getZeroReactions(MethodsWithMemory.getContainer(obj.getModel().getId())));
			
			
			if(obj!=null && SimulationResultMethods.isModelSimulated(obj)){
				Map<String, double[]> flux = MethodsWithMemory.getMinMaxFixedBiomass(obj, 0.99999, test);
				
				ret = (SimulationResultMethods.manhattanDistance(flux)/test.size())+"";
			}
		}
		
		return ret;
	}

}
