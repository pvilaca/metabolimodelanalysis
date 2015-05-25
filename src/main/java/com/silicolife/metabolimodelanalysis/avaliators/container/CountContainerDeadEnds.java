package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.ContainerUtils;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class CountContainerDeadEnds implements Evaluators<Container>{

	@Override
	public String getHeaders() {
		return "#Dead Ends Metabolites";
	}

	@Override
	public String getValues(Container obj) {
		
		String ret = null;
		
		if(obj != null){
			Set<String> set = ContainerUtils.removeDeadEndsIteratively(obj.clone(), true);
			ret = set.size() + "";
		}
		
		return ret;
	}

}
