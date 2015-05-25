package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.collection.CollectionUtils;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class NotDeclaredMetabolitesDebug implements Evaluators<Container>{

	static public Map<String, Container> cont = new HashMap<String, Container>(); 
	@Override
	public String getHeaders() {
		return "NotDeclaredMetabolites";
	}

	@Override
	public String getValues(Container obj) {
		
		Set<String> reactionMetabolites = new HashSet<String>();
		for(ReactionCI r : obj.getReactions().values())
			reactionMetabolites.addAll(r.getMetaboliteSetIds());
		
		Set<String> ret = CollectionUtils.getSetDiferenceValues(reactionMetabolites, obj.getMetabolites().keySet());
		
		return "" + ret.size() + " " +ret + " " + reactionMetabolites.size() + " " +  obj.getMetabolites().keySet().size();
	}

}
