package com.silicolife.metabolimodelanalysis.avaliators.container;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.ContainerUtils;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.chemestry.BalanceValidator;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.MapUtils;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class CountBalancedReactions implements Evaluators<Container>{

//	public static Map<String, Map<String, String>> balanceInfo = new HashMap<String, Map<String, String>>(); 
	
	protected static Logger log = Logger.getLogger(CountBalancedReactions.class.getName());
	@Override
	public String getHeaders() {
		String headers = "";
		
		for(String bId : BalanceValidator.ALL_TAGS)
			headers += "#BALANCE [" + bId + "]\t";
		
		headers = headers.substring(0, headers.length()-1);
		
		return headers;
	}

	@Override
	public String getValues(Container obj) {
		String ret = null;
		
		if(obj != null){
			ret = "";
			
			Container cont = obj.clone();
			try {
				cont.stripDuplicateMetabolitesInfoById(Pattern.compile("M_(.+)_."));
			} catch (Exception e) {
				log.warning(e.getMessage());
			}
			BalanceValidator bv = null;
			if(obj.getModelName().endsWith("_2")) bv = ContainerUtils.balanceModelInH(cont, "h");
			else bv = ContainerUtils.balanceModelInH(cont, "cpd00067");
			
			Map<String, String> t = bv.getReactionTags();
			
			Map<String, Set<String>> info = MapUtils.revertMap(t);
			for(String bId : BalanceValidator.ALL_TAGS)
				if(info.get(bId) != null)ret+=info.get(bId).size() + "\t";
				else ret+="\t";
		}
		ret = ret.substring(0, ret.length()-1);
		return ret;
	}

}
