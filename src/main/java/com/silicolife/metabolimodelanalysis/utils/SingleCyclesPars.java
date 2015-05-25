package com.silicolife.metabolimodelanalysis.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.reactions.CompareSameStoiqR;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.reactions.IsCycleParReactions;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.reactions.ReactionClusters;

public class SingleCyclesPars extends ReactionClusters{

	
	protected List<Set<String>> cycles;
	IsCycleParReactions cycle;
	
	public SingleCyclesPars(Container cont, Collection<String> cofact) {
		super( new CompareSameStoiqR(cofact), cont);
		cycle = new IsCycleParReactions(cofact);
	}
	
	public List<Set<String>> getCycles(){
	
		if(cycles==null)
			cycles = calculateCycles();
		
		return cycles;
		
	}
	
	public List<Set<String>> calculateCycles(){
		
		List<Set<String>> ret = new ArrayList<Set<String>>();
		
		for(int i =0; i < getReactionIdClusters().size(); i++){
			Set<String> cluster = getReactionIdClusters().get(i);
			Set<String> _2it = new HashSet<String>(cluster);
			
			Iterator<String> it = cluster.iterator();
			ReactionCI r = cont.getReaction(it.next());
			_2it.remove(r.getId());
			
			while(it.hasNext()){
				
				for(String id : _2it){
					ReactionCI r2 = cont.getReaction(id);
					if(cycle.isEqualReaction(r, r2)){
						Set<String> toSave = new HashSet<String>();
						toSave.add(r.getId());
						toSave.add(r2.getId());
						ret.add(toSave);
					}
				}
				r = cont.getReaction(it.next());
				_2it.remove(r.getId());
			}
			
		}
		
		return ret;
	}

}
