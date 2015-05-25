package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.ContainerMethods;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class CountContainerRevReactions implements Evaluators<Container>{

	
	
	@Override
	public String getHeaders() {
		return "#Rev. Reactions using Bounds";
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		if(obj!=null){
			
			Set<String> reactions = MethodsWithMemory.getRevReactionsByDefaultBounds(obj, ContainerMethods.getReactionsWithoutDrains(obj));
			ret = reactions.size()+"";
		}
			
		
		return ret;
	}

}
