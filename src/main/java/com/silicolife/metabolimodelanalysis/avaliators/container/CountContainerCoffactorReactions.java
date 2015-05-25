package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class CountContainerCoffactorReactions implements Evaluators<Container>{

//	public static Map<String, Collection<String>> cofactors = new HashMap<String, Collection<String>>();
//	public static Map<String, Set<String>> reactionWithCofact = new HashMap<String, Set<String>>();
	
	@Override
	public String getHeaders() {
		return "#reactions with cofactors";
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		if(obj != null){
			Set<String> cofac = MethodsWithMemory.identifyCofactors(obj);
			
			Set<String> rs = MethodsWithMemory.getReactionsWithMetabolites(obj, cofac);
			ret = rs.size()+"";
		}
		
		return ret;
	}
	
	
	
	
}
