package com.silicolife.metabolimodelanalysis.avaliators.container;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class CountContainerGenes implements Evaluators<Container>{

	@Override
	public String getHeaders() {
		return "#Genes";
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		if(obj!=null)
			ret = obj.getGenes().size()+"";
		
		return ret;
	}

}
