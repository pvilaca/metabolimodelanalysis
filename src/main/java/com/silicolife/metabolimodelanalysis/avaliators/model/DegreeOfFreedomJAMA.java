package com.silicolife.metabolimodelanalysis.avaliators.model;

import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.sun.istack.internal.logging.Logger;

public class DegreeOfFreedomJAMA implements Evaluators<SteadyStateModel>{
	
	static public int MAX_NUMBER=3000;
	Logger log = Logger.getLogger(DegreeOfFreedomJAMA.class);
	
	@Override
	public String getHeaders() {
		return "#rank\t#FreedomDegree";
	}

	@Override
	public String getValues(SteadyStateModel obj) {
		
		String ret = null + "\t" + null;
		if(obj!= null){
		
			DoubleMatrix2D matrix = obj.getStoichiometricMatrix().convertToColt();
			
			if(matrix.columns()<MAX_NUMBER && matrix.rows() < MAX_NUMBER){
				 Algebra alg = new Algebra();
				System.out.print(obj.getId() + "\t" + matrix.rows() + "\t" + matrix.columns() + "\t");
				if(matrix.columns() > matrix.rows()){
					matrix = alg.transpose(matrix);
					System.out.print("\t");
				}else
					System.out.print("problem\t");
				log.info("Calculating DegreeOfFreedomJAMA "+ matrix.columns() + "\t" + matrix.rows());
				int rank = alg.rank(matrix);
				int degree = obj.getNumberOfReactions() - rank;
				System.out.println(rank+"\t"+degree);
				ret = rank+"\t"+degree+"";
			}
		}
		return ret;
	}

	
	
}
