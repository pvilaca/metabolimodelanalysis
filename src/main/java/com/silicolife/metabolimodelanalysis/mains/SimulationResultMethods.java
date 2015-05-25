package com.silicolife.metabolimodelanalysis.mains;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import pt.uminho.ceb.biosystems.mew.mewcore.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.mewcore.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;

public class SimulationResultMethods {

	private static Logger log = Logger.getLogger(SimulationResultMethods.class.getName());
	
	public static Set<String> getFluxGreaterOrEqualThan(SteadyStateSimulationResult result, double value){
		
		Set<String> ret= new HashSet<String>();
		for(String id : result.getFluxValues().keySet()){
			
			if(Math.abs(result.getFluxValues().get(id)) >= Math.abs(value))
				ret.add(id);
		}
		
		return ret;
	}
	
	public static Set<String> getFluxGreaterThan(SteadyStateSimulationResult result, double value){
		
		Set<String> ret= new HashSet<String>();
		for(String id : result.getFluxValues().keySet()){
			
			if(Math.abs(result.getFluxValues().get(id)) > Math.abs(value))
				ret.add(id);
		}
		
		return ret;
	}
	
	
	public static Set<String> getFluxLessThan(SteadyStateSimulationResult result, double value){
		
		Set<String> ret= new HashSet<String>();
		for(String id : result.getFluxValues().keySet()){
			
			if(Math.abs(result.getFluxValues().get(id)) < Math.abs(value))
				ret.add(id);
		}
		
		return ret;
	}
	
	
	public static double manhattanDistance(Map<String, double[]> minMax){
		
		double distance = 0;
		
		for(double[] mm : minMax.values()){
//			System.out.println(mm + "\t" + mm.length);
//			System.out.println("\t" + mm[0] + "\t"+ mm[1]);
			distance+= Math.abs(mm[0]-mm[1]);
		}
		return distance;
		
	}
	
	
	
	public static Map<String, double[]> getMinMaxFixedBiomass(SteadyStateSimulationResult result,double relaxBio, Set<String> fluxes){
		
		Map<String, double[]> ret = new HashMap<String, double[]>();
		
		String biomassId = result.getModel().getBiomassFlux();
		double fluxValue = result.getFluxValues().get(biomassId);
		
		EnvironmentalConditions conditions = null;	
		if(result.getEnvironmentalConditions()!=null)
			conditions = result.getEnvironmentalConditions().copy();
		else
			conditions = new EnvironmentalConditions();
		
		conditions.put(biomassId, new ReactionConstraint(relaxBio * fluxValue, 10000.0));
		SimulationSteadyStateControlCenter cc = new SimulationSteadyStateControlCenter(conditions, null, result.getModel(), SimulationProperties.FBA);
		cc.setSolver(SolverType.CPLEX);
		
		fluxes = (fluxes==null)?result.getModel().getReactions().keySet():fluxes;
		
		for(String id : fluxes){
			double[] minMax = new double[2];
			cc.setFBAObjSingleFlux(id, 1.0);
			cc.setMaximization(false);
			try {
				minMax[0] = cc.simulate().getFluxValues().get(id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.warning(e.getMessage());
				minMax[0] = Double.NaN;
			}
			
			cc.setMaximization(true);
			
			try {
				minMax[1] = cc.simulate().getFluxValues().get(id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.warning(e.getMessage());
				minMax[1] = Double.NaN;
			}
			
			ret.put(id, minMax);
		}
		
		return ret;
	}
	
	public static SteadyStateSimulationResult wt(SteadyStateModel obj, EnvironmentalConditions ec){
		SteadyStateSimulationResult result = null;
		
		
		SimulationSteadyStateControlCenter cc = new SimulationSteadyStateControlCenter(ec, null, obj, SimulationProperties.PFBA);
		cc.setSolver(SolverType.CPLEX);
		cc.setMaximization(true);
		cc.setFBAObjSingleFlux(obj.getBiomassFlux(), 1.0);
		
		
		try {
			result = cc.simulate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warning(e.getMessage());
		}
		
		return result;
	}
	
	
	
	static public Boolean isModelSimulated(SteadyStateSimulationResult result){
		Double biomass = result.getFluxValues().get(result.getModel().getBiomassFlux());
		return result != null && !biomass.equals(Double.NaN) && biomass > 1E-6 && biomass <=3.0;
	}
	
//	static public CoPEFBA runCope(SteadyStateModel model, String methodId) throws Exception{
//		CoPEFBA cope = new CoPEFBA(model, methodId);
//		cope.addMethodProperty(SimulationProperties.IS_MAXIMIZATION, true);
//		cope.setSolver(SolverType.CPLEX);
//		cope.run();
//		return cope;
//	}
}
