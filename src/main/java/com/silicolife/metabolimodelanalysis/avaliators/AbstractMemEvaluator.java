package com.silicolife.metabolimodelanalysis.avaliators;

import java.io.IOException;
import java.util.logging.Logger;

import com.silicolife.metabolimodelanalysis.mains.InfoMemory;

public abstract class AbstractMemEvaluator<T extends Object, J extends Object> implements Evaluators<T>{
	
	protected static Logger log = Logger.getLogger(AbstractMemEvaluator.class.getName());
	
	protected abstract String getModelId(T obj);
	protected abstract J getInfoToMem(T obj);
	protected abstract String getInfoToShow(J result);
	
	public String getValues(T obj){
		return getInfoToShow(getValue(obj));
	}
	
	String getFunctionId(){
		return getClass().getName();
	}
	
	J getValue(T obj){
		J ret = null;
		try {
			System.out.println("Getting "+getModelId(obj)+" " + this.getClass().toString());
			log.info("Getting "+getModelId(obj)+" " + this.getClass().toString() );
			ret = (J) InfoMemory.getIntance().getData(getModelId(obj), getFunctionId());
		} catch (IOException e) {
			System.out.println("Running "+getModelId(obj)+" " + this.getClass().toString());
			log.info("Running "+getModelId(obj)+" " + this.getClass().toString());
			ret = getInfoToMem(obj);
		}
		System.out.println("End "+getModelId(obj)+" " + this.getClass().toString());
		log.info("End "+getModelId(obj)+" " + this.getClass().toString());
		
		return ret;
	}

}
