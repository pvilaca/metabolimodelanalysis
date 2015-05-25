package com.silicolife.metabolimodelanalysis.avaliators.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.mewcore.model.exceptions.NonExistentIdException;
import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class CountCiclesWithFlux implements Evaluators<SteadyStateModel>{
	
	@Override
	public String getHeaders() {
		return "#Cicles with flux";
	}

	@Override
	public String getValues(SteadyStateModel obj) {
		String ret = null;
		if(obj!= null){
			
			Container container = MethodsWithMemory.getContainer(obj.getId());
			SteadyStateSimulationResult result = MethodsWithMemory.wt(obj, null);
			Collection<String> cofac = MethodsWithMemory.identifyCofactors(container);
			List<Set<String>> cicles = MethodsWithMemory.calculateSingleCyclesReactions(container);
			
			
			if(result!=null){
				try {
					ret = MethodsWithMemory.getSingleCyclesWithFlux(container, result, cofac, cicles).size()+"";
				
				} catch (NonExistentIdException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	
	
}
