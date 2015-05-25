package com.silicolife.metabolimodelanalysis.avaliators.model;

import java.io.IOException;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.mewcore.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.mewcore.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;

import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class NormalizedSimulateFBAModel extends SimulateFBAModel{

	private static EnvironmentalConditions normEc = null;
	private static int metaboliteIdx = 1;
	private static int minBound = 3;
	private static int maxBound = 4;
	
	public NormalizedSimulateFBAModel() throws IOException{
		super(getNormalizedEC());
		
	}
	
	private static EnvironmentalConditions getNormalizedEC() throws IOException {
		
		if(normEc == null){
			Map<String, String[]> metaboliteBounds = FileUtils.readTableFileFormat("../paper_models/drainstreatment/metaboliteToBound", "\t", 0);
			Map<String, String[]> drainMetabolite = FileUtils.readTableFileFormat("../paper_models/drainstreatment/drainToMetabolite", "\t", 0);
			normEc = new EnvironmentalConditions("NormEC");
			
			for(String id : drainMetabolite.keySet()){
				String metaboliteId = drainMetabolite.get(id)[metaboliteIdx];
				
//				System.out.println(id + "\t" + metaboliteId);
				String lbS = metaboliteBounds.get(metaboliteId)[minBound];
				String ubS = metaboliteBounds.get(metaboliteId)[maxBound];			
				normEc.put(id, new ReactionConstraint(Double.parseDouble(lbS),Double.parseDouble(ubS)));
			}
		}
		
		return normEc;
	}

	@Override
	public String getHeaders() {
		return "Ec Normalized";
	}

	@Override
	public String getValues(SteadyStateModel obj) {
		Double ret = null;
		if(obj!= null){
			
			SteadyStateSimulationResult result = MethodsWithMemory.wt(obj, ec);
			if(result!= null)
			ret = result.getFluxValues().get(obj.getBiomassFlux());
			
		}
		return ret+"";
	}

	
	
}
