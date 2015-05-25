package com.silicolife.metabolimodelanalysis.mains;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigMains {

	private static final String ORIGINAL_SBML_MODEL_FOLDER = "ORIGINAL_SBML_MODEL_FOLDER";
	private static final String CONVERTED_SBML_MODEL_FOLDER = "CONVERTED_SBML_MODEL_FOLDER";

	static ConfigMains confs = null;
	
	Properties p = new Properties();
	
	
	public static ConfigMains getConfs() {
		if(confs == null)
			confs = new ConfigMains();
		
		return confs;
	}
	
	public ConfigMains() {
		Properties p = new Properties();
		loadDefault();
	}
	
	private void loadDefault() {
		p.put(ORIGINAL_SBML_MODEL_FOLDER, "/home/pvilaca/Work/Tese/PaperModes/OriginalModels/sbml/");
		p.put(CONVERTED_SBML_MODEL_FOLDER, "/home/pvilaca/Work/Tese/PaperModes/ConvertedModesl/sbml/");
		
	}
	
	public void saveConfs(String file) throws FileNotFoundException, IOException{
		p.store(new FileOutputStream(file), "");
	}
	
	public void loadConfs(String file) {
		
		if(file !=null){
			Properties prop = new Properties();
			
			try {
				prop.load(new FileInputStream(file));
				p.putAll(prop);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public String getConvertedSbmlModelFolder() {
		return (String)p.get(CONVERTED_SBML_MODEL_FOLDER);
	}
	
	public String getOriginalSbmlModelFolder() {
		return  (String)p.get(ORIGINAL_SBML_MODEL_FOLDER);
	}
	
}
