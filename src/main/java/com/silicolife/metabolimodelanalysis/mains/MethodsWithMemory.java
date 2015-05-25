package com.silicolife.metabolimodelanalysis.mains;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.mewcore.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.mewcore.model.exceptions.NonExistentIdException;
import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.MapUtils;

public class MethodsWithMemory {
	
	private static Logger log = Logger.getLogger(MethodsWithMemory.class.getName() );
	
	private static synchronized Logger getLogger(){
		return log;
	}
	
	public static void addContainer(Container cont){
		getLogger().info("addContainer " + cont.getModelName());
		InfoMemory.getIntance().addData(cont.getModelName(), "Container", cont);
	}
	
	public static Container getContainer(String id){
		try {
			
			getLogger().info("Get Container " + id);
			return (Container) InfoMemory.getIntance().getData(id, "Container");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Set<String> identifyCofactors(Container cont){
		
		Set<String> ret = null;
		try {
			
			ret = (Set<String>) InfoMemory.getIntance().getData(cont.getModelName(), "COFACT");
			getLogger().info("Mem identifyCofactors " + cont.getModelName());
		} catch (IOException e) {
			ret = ContainerMethods.identifyCofactors(cont);
			getLogger().info("Calc identifyCofactors " + cont.getModelName());
			InfoMemory.getIntance().addData(cont.getModelName(), "COFACT", ret);
		}
		return ret;
	}
	
	public static Set<String> getReactionsWithMetabolites(Container cont, Set<String> cofac){
		
		Set<String> ret = null;
		try {
			
			ret = (Set<String>) InfoMemory.getIntance().getData(cont.getModelName(), "getReactionsWithCofactores");
			getLogger().info("Mem getReactionsWithMetabolites " + cont.getModelName() );
		} catch (IOException e) {
			getLogger().info("Calc getReactionsWithMetabolites " + cont.getModelName() );
			ret = ContainerMethods.getReactionsWithMetabolites(cont, cofac);
			InfoMemory.getIntance().addData(cont.getModelName(), "getReactionsWithCofactores", ret);
		}
		return ret;
	}
	
	
	public static Set<String> getRevReactionsByDefaultBounds(Container cont, Set<String> reactionsToEval){
		
		Set<String> ret = null;
		try {
			
			ret = (Set<String>) InfoMemory.getIntance().getData(cont.getModelName(), "getRevReactionsByDefaultBounds");
			getLogger().info("Mem getRevReactionsByDefaultBounds " + cont.getModelName() );
		} catch (IOException e) {
			getLogger().info("Calc getRevReactionsByDefaultBounds " + cont.getModelName() );
			ret = ContainerMethods.getRevReactionsByDefaultBounds(cont, reactionsToEval);
			InfoMemory.getIntance().addData(cont.getModelName(), "getRevReactionsByDefaultBounds", ret);
		}
		return ret;
	}
	
	public static List<Set<String>> calculateSingleCyclesReactions(Container cont){
		
		Set<String> cofact = identifyCofactors(cont);
		List<Set<String>> ret = null;
		try {
			
			ret = (List<Set<String>>) InfoMemory.getIntance().getData(cont.getModelName(), "calculateSingleCyclesReactions");
			getLogger().info("Mem calculateSingleCyclesReactions " + cont.getModelName() );
		} catch (IOException e) {
			getLogger().info("Calc calculateSingleCyclesReactions " + cont.getModelName() );
			ret = ContainerMethods.calculateSingleCyclesReactions(cont, cofact);
			InfoMemory.getIntance().addData(cont.getModelName(), "calculateSingleCyclesReactions", ret);
		}
		return ret;
	}
	
	public static List<Set<String>> getSingleCyclesReactions(String modelId) throws IOException{
		getLogger().info("get calculateSingleCyclesReactions " + modelId );
		return (List<Set<String>>) InfoMemory.getIntance().getData(modelId, "calculateSingleCyclesReactions");
	}

	public static Set<String> getZeroReactions(Container cont){
		Set<String> ret = null;
		try {
			
			ret = (Set<String>) InfoMemory.getIntance().getData(cont.getModelName(), "getZeroReactions");
			getLogger().info("Mem getZeroReactions " + cont.getModelName() );
		} catch (IOException e) {
			getLogger().info("Calc getZeroReactions " + cont.getModelName() );
			ret = ContainerMethods.getZeroReactions(cont);
			InfoMemory.getIntance().addData(cont.getModelName(), "getZeroReactions", ret);
			
		}
		return ret;
	}
	
	public static Map<String, double[]> getMinMaxFixedBiomass(SteadyStateSimulationResult result, double relaxBio, Set<String> fluxes){
		
		Map<String, double[]> info = null;
		
		try {
			info = (Map<String, double[]>) InfoMemory.getIntance().getData(result.getModel().getId(), InfoMemory.FVA_FIXED + "[" + relaxBio+ "]");
			getLogger().info("Mem getMinMaxFixedBiomass " );
		} catch (IOException e) {
			getLogger().info("Calc getMinMaxFixedBiomass " );
			info = SimulationResultMethods.getMinMaxFixedBiomass(result, relaxBio, null);
			InfoMemory.getIntance().addData(result.getModel().getId(), InfoMemory.FVA_FIXED + "[" + relaxBio+ "]", info);
			info = MapUtils.subMap(info, fluxes,null);
			
		}
		
		return(info);
	}
	
	public static SteadyStateSimulationResult wt(SteadyStateModel obj, EnvironmentalConditions ec){
		SteadyStateSimulationResult result = null;
		
		String ecId = (ec==null)?"NONE":ec.getId();
		try {
			result = (SteadyStateSimulationResult) InfoMemory.getIntance().getData(obj.getId(), InfoMemory.WT+"_"+ecId);
			getLogger().info("Mem wt " +obj.getId() + "\t" + ((result!=null)?result.getSolutionType():null));
		} catch (IOException e) {
			result = SimulationResultMethods.wt(obj, ec);
			InfoMemory.getIntance().addData(obj.getId(), InfoMemory.WT+"_"+ecId, result);
			getLogger().info("Calc wt " +obj.getId()+ "\t" + ((result!=null)?result.getSolutionType():null));
		}
		return result;
	}
	
	static public List<Set<String>> getSingleCyclesWithFlux(Container cont,SteadyStateSimulationResult result, Collection<String> cofac, List<Set<String>> cicles) throws NonExistentIdException{
		
		List<Set<String>> ret = null;
		try {
			ret = (List<Set<String>>) InfoMemory.getIntance().getData(cont.getModelName(), "getSingleCyclesWithFlux");
			getLogger().info("Mem Calc Single cycles " +cont.getModelName());
		} catch (IOException e) {
			getLogger().info("Calc Single cycles " +cont.getModelName());
			ret = ContainerMethods.getSingleCyclesWithFlux(cont, result, cofac, cicles);
			InfoMemory.getIntance().addData(cont.getModelName(), "getSingleCyclesWithFlux", ret);
			
		}
		return ret;
	}
	
	public static Boolean isModelSimulated(SteadyStateModel model){
		SteadyStateSimulationResult result = null;
		result = wt(model, null);
		Double biomass = (result == null)? null:result.getFluxValues().get(model.getBiomassFlux());
		return result != null && !biomass.equals(Double.NaN) && biomass > 1E-6 && biomass <=3.0;
	}

}
