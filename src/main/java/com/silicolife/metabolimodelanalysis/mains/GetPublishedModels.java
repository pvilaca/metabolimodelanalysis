package com.silicolife.metabolimodelanalysis.mains;

public class GetPublishedModels {
	
	public static void main(String[] args) throws Exception {
		InputMethods.saveModelFromDataBase(ConfigMains.getConfs().getOriginalSbmlModelFolder());
	}

}
