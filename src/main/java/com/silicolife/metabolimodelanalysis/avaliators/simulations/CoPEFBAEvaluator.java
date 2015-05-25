package com.silicolife.metabolimodelanalysis.avaliators.simulations;

import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SimulationProperties;

import com.silicolife.metabolimodelanalysis.avaliators.AbstractMemEvaluator;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;
import com.silicolife.metabolimodelanalysis.mains.SimulationResultMethods;

public class CoPEFBAEvaluator extends AbstractMemEvaluator<SteadyStateModel, CoPEFBA>{

	@Override
	public String getHeaders() {
		return "#Vertices\t#Rays";
	}

	@Override
	protected String getInfoToShow(CoPEFBA result) {
		String ret = "null\tnull";
		if(result != null)
			ret = result.getVertices().size() + "\t" + result.getRays().size();
		
		return ret;
	}
	
	@Override
	protected String getModelId(SteadyStateModel obj) {
		return obj.getId();
	}

	@Override
	protected CoPEFBA getInfoToMem(SteadyStateModel obj) {
		
		CoPEFBA ret = null;
		try {
			if(MethodsWithMemory.isModelSimulated(obj)){
				ret = SimulationResultMethods.runCope(obj, SimulationProperties.FBA);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}


	

}
