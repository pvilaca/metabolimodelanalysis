package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class ReactionsWithWrongStoiquiometries implements Evaluators<Container>{

	static public Map<String, Container> cont = new HashMap<String, Container>(); 
	@Override
	public String getHeaders() {
		return "#ReactionsWithWrongStoic";
	}

	@Override
	public String getValues(Container obj) {
		return "" + obj.getReactionsWithWrongStoichiometry().size();
	}

}
