package com.silicolife.metabolimodelanalysis.mains;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.ContainerUtils;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionConstraintCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.chemestry.MetaboliteFormula;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.reactions.ReactionUtils;
import pt.uminho.ceb.biosystems.mew.mewcore.model.exceptions.NonExistentIdException;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.collection.CollectionUtils;

import com.silicolife.metabolimodelanalysis.utils.SingleCyclesPars;

public class ContainerMethods {
	
	public static Set<String> getReactionsWithoutDrains(Container c){
		Set<String> ret = new HashSet<String>(c.getReactions().keySet());
		ret.removeAll(c.getDrains());
		return ret;
	}
	
	
	public static Set<String> getRevReactionsByRevFlag(Container c, Set<String> reactionsToEval){
		
		Set<String> ret = new HashSet<String>();
		for(String id : reactionsToEval){
			ReactionCI r = c.getReaction(id);
			
			if(r.isReversible()) ret .add(id); 
		}
		
		return ret;
	}
	
	public static Set<String> getRevReactionsByDefaultBounds(Container c, Set<String> reactionsToEval){
		
		Set<String> ret = new HashSet<String>();
		for(String id : reactionsToEval){
			ReactionCI r = c.getReaction(id);
			ReactionConstraintCI rc = c.getDefaultEC().get(id);
			
			Boolean test = null;
			
			if(rc!=null && rc.getLowerLimit() < 0 && rc.getUpperLimit() >0) test = true;
			
			if(test==null) test = r.isReversible();
			if(test) ret.add(id);
		}
		
		return ret;
	}
	
	
	public static Set<String> identifyCofactors(Container c){
		Set<String> ret = new HashSet<String>();
		
		ret.addAll(c.identifyMetabolitesIdByPattern(Pattern.compile("(?i).*nad.*")));
		ret.addAll(c.identifyMetabolitesIdByPattern(Pattern.compile("(?i).*nadp.*")));
		ret.addAll(c.identifyMetabolitesIdByPattern(Pattern.compile("(?i).*fad.*")));
		ret.addAll(c.identifyMetabolitesIdByPattern(Pattern.compile("(?i).*fadp.*")));
		
		ret.addAll(c.identifyMetabolitesByNamePatter(Pattern.compile("(?i).*nad.*")));
		ret.addAll(c.identifyMetabolitesByNamePatter(Pattern.compile("(?i).*nadp.*")));
		ret.addAll(c.identifyMetabolitesByNamePatter(Pattern.compile("(?i).*fad.*")));
		ret.addAll(c.identifyMetabolitesByNamePatter(Pattern.compile("(?i).*fadp.*")));
		
		return ret;
	}
	
	public static List<Set<String>> calculateSingleCyclesReactions(Container cont, Collection<String> cofactorIds){
		SingleCyclesPars pars = new SingleCyclesPars(cont, cofactorIds);
		return pars.getCycles();
	}
	
	
	static public List<Set<String>> getSingleCyclesWithFlux(Container container,SteadyStateSimulationResult result, Collection<String> cofactorIds, List<Set<String>> clusters) throws  NonExistentIdException{
		
		List<Set<String>> ret = new ArrayList<Set<String>>();
		
		for(int i=0; i < clusters.size(); i++){
			Iterator<String> reactions = clusters.get(i).iterator();
			
			ReactionCI r1 = container.getReaction(reactions.next());
			ReactionCI r2 = container.getReaction(reactions.next());
			
			double f1 = result.getFluxValues().get(r1.getId());
			double f2 = result.getFluxValues().get(r2.getId());
			
			boolean isCycle = isSingleFluxCycle(r1, r2, f1, f2, cofactorIds, false, true);
			if(isCycle)
				ret.add(clusters.get(i));
		}
		
		return ret;
	}
	
	public static boolean isSingleFluxCycle(ReactionCI r1, ReactionCI r2, double f1, double f2, Collection<String> cofactores, boolean ignoreComp, boolean ignoreSValue){
		
		boolean samedir = ReactionUtils.isSameSoiq(r1.getReactants(),r2.getReactants(), ignoreComp, ignoreSValue, cofactores);
		samedir = samedir && ReactionUtils.isSameSoiq(r1.getProducts(),r2.getProducts(), ignoreComp, ignoreSValue, cofactores);
		
		boolean reversedir =false;
		if(!samedir){
			reversedir = ReactionUtils.isSameSoiq(r2.getReactants(),r2.getProducts(), ignoreComp, ignoreSValue, cofactores);
			reversedir = reversedir && ReactionUtils.isSameSoiq(r2.getProducts(),r2.getReactants(), ignoreComp, ignoreSValue, cofactores);
		}
		
		samedir = samedir && (f1>0 && f2<0 || f1<0 && f2>0);
		reversedir = reversedir && (f1<0 && f2<0 || f1>0 && f2>0);
		
		return samedir || reversedir;
	}
	
	
	public static Set<String> getReactionsWithMetabolites(Container cont, Collection<String> metabolites){
		
		Set<String> ret = new HashSet<String>();
		
		for(ReactionCI r : cont.getReactions().values()){
			
			Set<String> metabolitesInRaction = r.getMetaboliteSetIds();
			Collection<String> communMet = CollectionUtils.getIntersectionValues(metabolitesInRaction, metabolites);
			
			if(communMet.size()>1){
				ret.add(r.getId());
			}
		}
		
		return ret;
	}
	
	public static Set<String> getZeroReactions(Container obj){
		Set<String> set = null; 
		
		try {
			set = ContainerUtils.removeDeadEndsIteratively(obj.clone(), true);
		} catch (Exception e) {
			
		}
		
		if(set == null) return null;
		Set<String> zero = new HashSet<String>();
		for(String id : set){
			zero.addAll(obj.getMetabolite(id).getReactionsId());
		}
		
		return set;
	}
	
//	public static Map<String, Map<String, String>> getCuerentDrains(Map<String, Container> containers) throws Exception{
//		
//		Map<String, Container> contAux = new HashMap<String, Container>(containers);
//		Map<String, Map<String, String>> ret = new HashMap<String, Map<String,String>>();
//		for(String id : containers.keySet()){
//			Container c = containers.get(id);
//			Map<String, String> drainToMet = c.getDrainToMetabolite();
//			Map<String, String> metToDrain = c.getMetaboliteToDrain();
//			contAux.remove(id);
//			
//			
//			
//		}
//		
//		
//	}
	
	public static Map<String, Set<String>> stydyDrain(Map<String, Container> containers) throws IOException{
		
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();
		Set<String> allDrains = new HashSet<String>();
		Set<String> allMetabolitesIdInDrain = new HashSet<String>();
		Set<String> allMetabolitesNameInDrain = new HashSet<String>();
		
		Map<String, Map<String,String>> metId = new HashMap<String, Map<String,String>>();
		Map<String, Map<String, String>> metName = new HashMap<String, Map<String,String>>();
		
		Map<String, Set<String>> metabolitesSynonms = new HashMap<String, Set<String>>();
		
//		Map<String, Container> contAux = new HashMap<String, Container>(containers);
//		Map<String, Map<String, String>> ret = new HashMap<String, Map<String,String>>();
		for(String id : containers.keySet()){
			Container c = containers.get(id);
			if(c!=null){
				Set<String> drains = c.getDrains();
				Map<String, String> drainMet;
				try {
					drainMet = c.getDrainToMetabolite();
					allDrains.addAll(drains);
					for(String drain : drains){
						
						String metaboliteId = drainMet.get(drain);
						allMetabolitesIdInDrain.add(metaboliteId);
						allMetabolitesNameInDrain.add(c.getMetabolite(metaboliteId).getName());
					}
				} catch (Exception e) {
					System.err.println("\n\n"+id);
					e.printStackTrace();
				}
				
				
			}
		}
		
		
		
		for(String drains : allDrains){
			for(String id : containers.keySet()){
				Container c = containers.get(id);
				if(c!=null)
					if(c.getDrains().contains(drains)){
						Set<String> info = ret.get(drains);
						if(info == null){
							info = new HashSet<String>();
							ret.put(drains, info);
						}
						info.add(id);
					}
					
			}
		}
		
		for(String mId : allMetabolitesIdInDrain){
			for(String id : containers.keySet()){
				Container c = containers.get(id);
				if(c!=null){
					Map<String, String> metDrain;
					try {
						
						metDrain = c.getMetaboliteToDrain();
						if(metDrain.containsKey(mId)){
							
							Map<String,String> info = metId.get(mId);
							if(info == null){
								info = new HashMap<String,String>();
								metId.put(mId, info);
							}
							info.put(id, metDrain.get(mId));
						}
						
					} catch (Exception e) {
						System.err.println("\n\n"+id);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
					
			}
		}
		
		for(String name : allMetabolitesNameInDrain){
			for(String id : containers.keySet()){
				Container c = containers.get(id);
				if(c!=null){
					
					try {
						Map<String, String> metNameToId = getMetaboliteNameToId(c);
//						System.out.println(metNameToId);
						String idMet = metNameToId.get(name);
						Map<String, String> idToDrain = c.getMetaboliteToDrain();
						
						if(metNameToId.containsKey(name)){
							Map<String,String> info = metName.get(name);
							if(info == null){
								info = new HashMap<String,String>();
								metName.put(name, info);
							}
							info.put(id, idToDrain.get(idMet));
						}
					} catch (Exception e) {
						System.err.println("\n\n"+id);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			}
		}
		
		
		
		for(String drain : ret.keySet()){
			Set<String> models = ret.get(drain); 
			
			Set<String> synonym = new TreeSet<String>();
			for(String id: models){
				Container cont = containers.get(id);
				try {
					Map<String, String> drainToMet = cont.getDrainToMetabolite();
					String metaboliteName = cont.getMetabolite(drainToMet.get(drain)).getName();
					synonym.add(metaboliteName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			Set<String> aux = new HashSet<String>(synonym);
			
			for(String id : synonym){
				Set<String> toAdd = metabolitesSynonms.get(id);
				if(toAdd == null){
					metabolitesSynonms.put(id, aux);
				}else{
					toAdd.addAll(aux);
				}
				
			}
			
			System.out.println(drain+"\t"+ CollectionUtils.join(ret.get(drain), "\t"));
		}
		
		System.out.println("\n\n");
		for(String idMet : metId.keySet()){
			Set<String> models = metId.get(idMet).keySet(); 
			
			Set<String> synonym = new TreeSet<String>();
			for(String id: models){
				Container cont = containers.get(id);
				try {
					String metaboliteName = cont.getMetabolite(idMet).getName();
					synonym.add(metaboliteName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			Set<String> aux = new HashSet<String>(synonym);

			for(String id : synonym){
				Set<String> toAdd = metabolitesSynonms.get(id);
				if(toAdd == null){
					metabolitesSynonms.put(id, aux);
				}else{
					toAdd.addAll(aux);
				}
				
			}
			
			System.out.println(idMet+"\t"+ CollectionUtils.join(metId.get(idMet).keySet(), "\t")+"\t\t"+ CollectionUtils.join(metId.get(idMet).values(), "\t"));
		}
		
		
		Map<String, Map<String, String>> metabolites = new HashMap<String, Map<String,String>>();
		Set<String> visited = new HashSet<String>();
		
		System.out.println("\n\n\nMetabolites " + metName);
		for(String name2 : metName.keySet()){
			
			String name = treatName(name2);
			if(!visited.contains(name)){
				Map<String, String> t = new HashMap<String, String>();
				
				t.putAll(metName.get(name2));
				
				System.out.println(name2+"\t"+ CollectionUtils.join(metName.get(name2).keySet(), "\t")+"\t\t"+ CollectionUtils.join(metName.get(name2).values(), "\t"));
			
			
				
				metabolites.put(name, t);
				Set<String> nome = metabolitesSynonms.get(name2);
				
				for(String n2 : nome){
					String n = treatName(n2);
					Map<String, String> ole = metName.get(n2);
					if(ole!=null)
					t.putAll(ole);
					visited.add(n);
				}
				
			}
			
		}
		
		
		FileWriter f = new FileWriter("linkDrains.tsv");
		Map<String, String> nameIdToName = new HashMap<String, String>();
		System.out.println("\n\n\n\n\nNew Metabolites");
		for(String name : metabolites.keySet()){
			Map<String,String> info = metabolites.get(name);
			
//			System.out.println(name+"\t"+ CollectionUtils.join(metabolites.get(name).keySet(), "\t")+"\t\t"+ CollectionUtils.join(metabolites.get(name).values(), "\t"));
			
			for(String d: info.keySet()){
				f.write(name + "\t" + info.get(d)+"\t"+d+"\n");
			}
		}
		f.close();
		
		
		System.out.println("\n\n\n");
		
//		MapUtils.prettyPrint(metabolitesSynonms);
		
		return ret;
		
	}
	
	static public Map<String, String> getMetaboliteNameToId(Container cont) throws Exception{
		
		Map<String, String> metToDrains = cont.getMetaboliteToDrain();
		
		Map<String, String> ret = new HashMap<String, String>();
		for(MetaboliteCI m : cont.getMetabolites().values()){
			
			String metId = m.getId();
			
			if(metToDrains.containsKey(metId))
				ret.put(m.getName(), metId);
		}
		
		return ret;
	}
	
	static public String treatName(String name){
		String ret  =name;
		String[] info = ret.split("_");
		String formula = info[info.length-1];
		
		System.out.println(formula);
		Map<String, Integer> t = MetaboliteFormula.parserFormula(formula);
		if(t.size() !=0){
			ret = ret.replace("_"+formula, "");
		}
		
		
		ret = ret.toLowerCase();
		if(ret.startsWith("_")) ret = ret.replaceFirst("_", "");
		if(ret.startsWith("m_")) ret = ret.replaceFirst("m_", "");
		if(ret.endsWith("(e)")) ret = ret.replace("(e)", "");
		if(ret.endsWith("[e]")) ret = ret.replace("[e]", "");
		if(ret.endsWith("[extracellular]")) ret = ret.replace("[extracellular]", "");
		if(ret.endsWith("_e")) ret = ret.replace("_e", "");
		ret=ret.replaceAll("[_ \\(\\)\\+-;,<>]", "");
	
			
		
		return ret.trim();
	}
	
	public static void printAllDrainsAndBoundsAndMetabolite(Map<String, Container> containers) throws IOException{
		
		
		FileWriter fw = new FileWriter("bounds.tsv");
		
		Set<String> visited = new HashSet<String>();
		for(Container cont : containers.values()){
			if(cont!=null){
				Set<String> drains = cont.getDrains();
				Map<String, String> drainsToMetabolite=null;
				try {
					drainsToMetabolite = cont.getDrainToMetabolite();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				for(String d: drains){
					
					if(!visited.contains(d)){
						visited.add(d);
						String modelid = cont.getModelName();
						String drainName = cont.getReaction(d).getName();
						String metId = null;
						String metName = null;
						ReactionConstraintCI ci = cont.getDefaultEC().get(d);
						if(ci == null){
							ci = new ReactionConstraintCI();
							if(!cont.getReaction(d).isReversible()){
								ci.setLowerLimit(0.0);
							}
						}
						
						
						if(drainsToMetabolite!=null){
							metId = drainsToMetabolite.get(d);
							try {
								metName = cont.getMetabolite(metId).getName();
							} catch (Exception e) {
								System.out.println( modelid + " Id: " + metId + "not declared in reaction: " +d);
							}
							
						}
						fw.write(modelid + "\t" + d + "\t" + drainName + "\t" + ci.getLowerLimit() + "\t" + ci.getUpperLimit() 
								+"\t"+metId + "\t" + metName + "\n");
					}
				}
			}
		}
		fw.flush();
		fw.close();
		
		
	}
	
	
	public static String treatDrainMame(String name){
		if(name == null) return null;
		String ret = name.toLowerCase();
		
		if(ret.startsWith("uptake of ")) ret = ret.substring(10);
		if(ret.startsWith("excretion of ")) ret = ret.substring(13);
		
		if(ret.endsWith(" exchange")) ret = ret.substring(0, ret.length()-9);
		
		return ret;
	}

	public static String treatDrainId(String id){
		if(id == null) return null;
		String ret = id.trim();
		
	
		
		ret = ret.replaceAll("_DASH_", "_");
		ret = ret.replaceAll("_LPAREN_", "_");
		ret = ret.replaceAll("_RPAREN_", "_");
		ret = ret.replaceAll("_EXF_", "_");
		ret = ret.replaceAll("_+", "_");
		
		
		if(ret.startsWith("UP_")) ret = ret.substring(3);
		if(ret.startsWith("EF_")) ret = ret.substring(3);
		
		if(ret.endsWith("extI")) ret = ret.substring(0, ret.length()-4);
		if(ret.endsWith("extO")) ret = ret.substring(0, ret.length()-4);
		if(ret.endsWith("xtO")) ret = ret.substring(0, ret.length()-3);
		if(ret.endsWith("xtI")) ret = ret.substring(0, ret.length()-3);
		if(ret.endsWith("OUT")) ret = ret.substring(0, ret.length()-3);
		if(ret.endsWith("IN")) ret = ret.substring(0, ret.length()-2);
		if(ret.endsWith("xt_")) ret = ret.substring(0, ret.length()-3);
		
		
		
		
		ret = ret.toLowerCase();
		if(ret.startsWith("r_")) ret = ret.substring(2);
		if(ret.startsWith("ex_")) ret = ret.substring(3);
		if(ret.startsWith("esc_")) ret = ret.substring(4);
		if(ret.startsWith("sink_")) ret = ret.substring(5);
		if(ret.startsWith("src_")) ret = ret.substring(4);
		
		
		if(ret.endsWith("_e0")) ret = ret.substring(0, ret.length()-3);
		if(ret.endsWith("_c0")) ret = ret.substring(0, ret.length()-3);
		
		if(ret.endsWith("_e")) ret = ret.substring(0, ret.length()-2);
		if(ret.endsWith("_e_")) ret = ret.substring(0, ret.length()-3);
		if(ret.endsWith("_lparen_e_rparen_")) ret = ret.substring(0, ret.length()-"_lparen_e_rparen_".length());
		if(ret.endsWith("_c_")) ret = ret.substring(0, ret.length()-3);
		
		if(ret.endsWith("_extraorganism_")) ret = ret.substring(0, ret.length()-"_extraorganism_".length());
		
		
		if(ret.matches(".+_.")) ret = ret.substring(0, ret.length()-2);
		if(ret.matches(".+_._")) ret = ret.substring(0, ret.length()-3);
		
		if(ret.matches("r*[0-9]+"))ret = "";
		if(ret.matches("cpd[0-9]+"))ret = "";
		if(ret.matches("ec[0-9]+"))ret = "";
		if(ret.matches("er[0-9]+"))ret = "";
		if(ret.matches("hc[0-9]+"))ret = "";
		if(ret.matches("ir[0-9]+"))ret = "";
		if(ret.matches("ce[0-9]+"))ret = "";

		if(ret.matches("rxn[0-9]+"))ret = "";
		if(ret.matches("rxnbme[0-9]+"))ret = "";
		if(ret.matches("v[0-9]+"))ret = "";

//		if(ret.endsWith("_LPAREN_e_RPAREN_")) ret = ret.substring(0, ret.length()-11);
		
		return ret.trim();
	}
	
	public static String treatMetaboliteName(String name){
		if(name == null) return null;
		
		String ret = name.trim();
		if(ret.startsWith("_")) ret = ret.substring(1);
		String[] info = ret.split("_");
		String formula = info[info.length-1];
		
		Map<String, Integer> t = MetaboliteFormula.parserFormula(formula);
		if(t.size() !=0){
			ret = ret.replace("_"+formula, "");
		}
		
		ret = ret.replaceAll("_DASH_", "_");
		
		if(ret.endsWith("xt_")) ret = ret.substring(0, ret.length()-3);
		ret = ret.toLowerCase();
		
		if(ret.startsWith("m_")) ret = ret.substring(2);
		
		if(ret.endsWith("_e0")) ret = ret.substring(0, ret.length()-"_e0".length());
		if(ret.endsWith("[e]")) ret = ret.substring(0, ret.length()-3);
		
		if(ret.endsWith("_")) ret = ret.substring(0, ret.length()-1);
		if(ret.endsWith(" [extracellular]")) ret = ret.substring(0, ret.length()-" [extracellular]".length());
		
		
		return ret;
	}

	public static String treatMetaboliteId(String id){
		if(id == null) return null;
		
		String ret = id;
		if(ret.startsWith("M_")) ret = ret.substring(2);
		if(ret.matches(".+_.")) ret = ret.substring(0, ret.length()-2);
		if(ret.matches("[A-Z1-9]+e")) ret = ret.substring(0, ret.length()-1);
	
		ret = ret.toLowerCase();
		
		if(ret.endsWith("_e0")) ret = ret.substring(0, ret.length()-3);
		if(ret.endsWith("_c0")) ret = ret.substring(0, ret.length()-3);
		
		if(ret.matches("r*[0-9]+"))ret = "";
		if(ret.matches("cpd[0-9]+"))ret = "";
		if(ret.matches("ec[0-9]+"))ret = "";
		if(ret.matches("er[0-9]+"))ret = "";
		if(ret.matches("hc[0-9]+"))ret = "";
		if(ret.matches("ir[0-9]+"))ret = "";
		if(ret.matches("ce[0-9]+"))ret = "";

		if(ret.matches("rxn[0-9]+"))ret = "";
		if(ret.matches("m[0-9]+"))ret = "";
		if(ret.matches("s_[0-9]+"))ret = "";
		return ret;
	}
	
	public static void printAllDrainsAndAndMetabolite(Map<String, Container> containers) throws IOException{
		
		
		FileWriter fw = new FileWriter("drainsAndMetabolites.tsv");
		
		Set<ArrayList<String>> visited = new HashSet<ArrayList<String>>();
		for(Container cont : containers.values()){
			if(cont!=null){
				Set<String> drains = cont.getDrains();
				Map<String, String> drainsToMetabolite=null;
				try {
					drainsToMetabolite = cont.getDrainToMetabolite();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				for(String d: drains){
					
					
					ArrayList<String> info = new ArrayList<String>();
					String drainName = cont.getReaction(d).getName();
					String metId = null;
					String metName = null;
					
					if(drainsToMetabolite!=null){
						metId = drainsToMetabolite.get(d);
						try {
							metName = cont.getMetabolite(metId).getName();
						} catch (Exception e) {
							System.out.println( cont.getModelName() + " Id: " + metId + "not declared in reaction: " +d);
						}
						
					}
					
					info.add(d);
					info.add(drainName);
					info.add(metId);
					info.add(metName);
					
					String treatDID = treatDrainId(d);
					String treatDName = treatDrainMame(drainName);
					String treatMId = treatMetaboliteId(metId);
					String treatMName = treatMetaboliteName(metName);
					
					if(!visited.contains(info)){
						visited.add(info);
						fw.write(d + "\t" + drainName + "\t" + metId + "\t" + metName + "\t" + treatDID + "\t" + treatDName + "\t" + treatMId + "\t" + treatMName+"\n");
//								+"\t"+metId + "\t" + metName + "\n");
					}
//						visited.add(d);
//						String modelid = cont.getModelName();
//						
//						
//						ReactionConstraintCI ci = cont.getDefaultEC().get(d);
//						if(ci == null){
//							ci = new ReactionConstraintCI();
//							if(!cont.getReaction(d).isReversible()){
//								ci.setLowerLimit(0.0);
//							}
//						}
//						
//						
//						
//						fw.write(modelid + "\t" + d + "\t" + drainName + "\t" + ci.getLowerLimit() + "\t" + ci.getUpperLimit() 
//								+"\t"+metId + "\t" + metName + "\n");
//					}
				}
			}
		}
		fw.flush();
		fw.close();
		
		
	}
	
//	static public void associateDrainToMetabolite(Container c) throws Exception{
//		
//		
//		Set<String> _drains = c. getDrains();
//		if (_drains.size() > 0) {
//			drainToMetabolite = new HashMap<String, String>();
//			metaboliteToDrain = new HashMap<String, String>();
//		}
//		
//		for(String id : _drains){
//			
//			
//			ReactionCI drain = getReaction(id);
//			Set<String> met = new HashSet<String>(drain.getProducts().keySet());
//			met.addAll(drain.getReactants().keySet());
//			
//			if(met.size() > 1)
//				throw new Exception("Drain " + id + " has more than one metabolite associated " +met);
//			
//			for(String metId : met){
//				
//				if(metaboliteToDrain.containsKey(met))
//					throw new Exception("Metabolite " + metId + " has more than one drain associated " + metaboliteToDrain.containsKey(met) + " " + id);
//				
//				metaboliteToDrain.put(metId, id);
//				drainToMetabolite.put(id, metId);
//			}
//			
//		}
//		
//		
//	}
	
}
