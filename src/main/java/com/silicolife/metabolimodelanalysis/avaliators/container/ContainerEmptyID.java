package com.silicolife.metabolimodelanalysis.avaliators.container;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class ContainerEmptyID implements Evaluators<Container>{

	@Override
	public String getHeaders() {
		return "Problem In Container";
	}

	@Override
	public String getValues(Container obj) {
		return (obj.getReactions().containsKey("") || obj.getMetabolites().containsKey("")) + "";
	}

}
