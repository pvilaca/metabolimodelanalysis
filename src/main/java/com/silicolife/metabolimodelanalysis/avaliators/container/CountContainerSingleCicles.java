package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.List;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class CountContainerSingleCicles implements Evaluators<Container> {

	
	@Override 
	public String getHeaders() {
		return "#single cicles";
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		if(obj!=null){
			
			List<Set<String>> cicles = MethodsWithMemory.calculateSingleCyclesReactions(obj);
			
			ret = cicles.size()+"";
		}
			
		
		return ret;
	}
}
