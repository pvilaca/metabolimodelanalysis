package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.collection.CollectionUtils;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.InfoMemory;

public class CountContainerCoffactorRevReactions implements Evaluators<Container>{

	@Override
	public String getHeaders() {
		return "# Rev reactions with cofactors";
	}

	@Override
	public String getValues(Container obj)  {
		String ret = null;
		
		if(obj!=null){
			try {
				
			
			Set<String> reactions = CollectionUtils.getIntersectionValues((Set<String>) InfoMemory.getIntance().getData(obj.getModelName()
					,"getRevReactionsByDefaultBounds")
					, (Set<String>) InfoMemory.getIntance().getData(obj.getModelName()
							,"getReactionsWithCofactores"));
					
					ret = reactions.size() + "";
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		
		return ret;
	}

	
}
