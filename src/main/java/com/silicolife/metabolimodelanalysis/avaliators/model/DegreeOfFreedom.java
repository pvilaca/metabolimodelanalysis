package com.silicolife.metabolimodelanalysis.avaliators.model;

import java.util.logging.Logger;

import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import cern.colt.matrix.DoubleMatrix2D;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class DegreeOfFreedom implements Evaluators<SteadyStateModel>{
	
	private static Logger log = Logger.getLogger(DegreeOfFreedom.class.getName());
	static public int MAX_NUMBER=300;
	@Override
	public String getHeaders() {
		return "#rank\t#FreedomDegree";
	}

	@Override
	public String getValues(SteadyStateModel obj) {
		String ret = null;
		System.out.println("DegreeOfFreedom " + obj.getId());
		if(obj!= null && MethodsWithMemory.isModelSimulated(obj) ){
			DoubleMatrix2D matrix = obj.getStoichiometricMatrix().convertToColt();
			if(matrix.columns()<MAX_NUMBER && matrix.rows() < MAX_NUMBER){
				log.info("DegreeOfFreedom " + matrix.columns() + "\t" + matrix.rows());
				Matrix m = new Matrix( matrix.toArray());
				int rank = m.rank();
				int degree = obj.getNumberOfReactions() - rank;
				System.out.println(rank+"\t"+degree);
				ret = rank+"\t"+degree+"";
			}
		}
		return ret;
	}

	
	
}
