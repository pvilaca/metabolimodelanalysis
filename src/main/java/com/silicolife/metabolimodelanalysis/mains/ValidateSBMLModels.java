package com.silicolife.metabolimodelanalysis.mains;

import java.io.IOException;

public class ValidateSBMLModels {

	public static void main(String[] args) throws IOException {
		InputMethods.getModelFromFolder(ConfigMains.getConfs().getOriginalSbmlModelFolder(),
				ConfigMains.getConfs().getConvertedSbmlModelFolder() + "/file.log" ,  false);
	}
}
