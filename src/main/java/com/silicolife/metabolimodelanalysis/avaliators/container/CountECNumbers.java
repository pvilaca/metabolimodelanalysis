package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.Map;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class CountECNumbers implements Evaluators<Container>{

	
	@Override
	public String getHeaders() {
		String headers = "#EC Numbers";	
		return headers;
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		
		if(obj != null){
			Map<String, Set<String>> ecNumbers = obj.getECNumbers();
			ret = ecNumbers.keySet().size() + "";
		}
		return ret;
	}

}
