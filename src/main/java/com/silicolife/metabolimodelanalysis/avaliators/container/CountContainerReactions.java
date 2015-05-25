package com.silicolife.metabolimodelanalysis.avaliators.container;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.ContainerMethods;

public class CountContainerReactions implements Evaluators<Container>{

	@Override
	public String getHeaders() {
		return "#Reactions";
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		if(obj!=null)
			ret = ContainerMethods.getReactionsWithoutDrains(obj).size()+"";
		
		return ret;
	}

}
