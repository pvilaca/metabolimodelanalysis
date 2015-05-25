package com.silicolife.metabolimodelanalysis.mains;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;

public class InfoMemory implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String WT = "WT";
	public static final String FVA_FIXED = "FVA_FIXED";
	public static final String POLCO = "POLCO";
	
	private static InfoMemory instance = null;
	
	public synchronized static InfoMemory getIntance(){
		if(instance == null) instance = new InfoMemory();
		return instance; 
	}
	
	private Map<String, Map<String, Object>> mem;

	private InfoMemory() {
		mem = new HashMap<String, Map<String,Object>>();
	}
	
	public void addData(String model, String info, Object data){
		
		Map<String, Object> modelInfo = mem.get(model);
		
		if(modelInfo==null){
			modelInfo = new HashMap<String, Object>();
			mem.put(model, modelInfo);
		}
		
		modelInfo.put(info, data);
	}
	
	
	public Object getData(String model, String info) throws IOException{
		
		Object data = null;
		if(mem.get(model) == null || !mem.get(model).containsKey(info)) throw new IOException();
		try {
			data = mem.get(model).get(info);
		} catch (NullPointerException e) {
			throw new IOException();
		}
		return data;
	}

	public void saveData(String path) throws IOException {
		FileUtils.saveSerializableObject(mem, path);
	}

	public void loadData(String string) throws ClassNotFoundException, IOException {
		mem = FileUtils.loadSerializableObject(string);
	}
	
}
