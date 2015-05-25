package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class CountContainerZeroFluxes implements Evaluators<Container>{

	
	@Override
	public String getHeaders() {
		return "#Zero Fluxes";
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		
		if(obj != null){
			Set<String> zero = MethodsWithMemory.getZeroReactions(obj);
			
			ret = zero.size() + "";
		}
		
		return ret;
	}

}
