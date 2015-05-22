package com.silicolife.metabolimodelanalysis.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pt.uminho.ceb.biosystems.mew.availablemodelsapi.RestClient;
import pt.uminho.ceb.biosystems.mew.availablemodelsapi.ds.ModelInfo;
import pt.uminho.ceb.biosystems.mew.availablemodelsapi.ds.ModelsIndex;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.MapUtils;

public class ModelDatabaseManagement {
	
	public static final String ID = "ID";
	public static final String AUTHOR = "AUTHOR";
	public static final String NAME = "NAME";
	public static final String ORGANISM = "ORGANISM";
	public static final String TAXONOMY = "TAXONOMY";
	public static final String URL = "URL";
	public static final String YEAR = "YEAR";
	public static final String DOI = "DOI";
	public static final String HASFILE = "HASFILE";
	
	private static ModelDatabaseManagement _instance;
	
	public static ModelDatabaseManagement getInstance(){
		if(_instance == null){
			_instance = new ModelDatabaseManagement();
		}
		return _instance;
	}
	
	private Map<String, Map<String, String>> infoModels;
	
	private ModelDatabaseManagement(){
		try {
			infoModels = populateinfo();
		} catch (Exception e) {
			throw new RuntimeException("Server models problem!!", e);
		}
		
	}

	private Map<String, Map<String, String>> populateinfo() throws Exception {
		
		Map<String, Map<String, String>> ret = new HashMap<String, Map<String, String>>();
		RestClient client = new RestClient();
		
		ModelsIndex models = client.index(false);
		for(ModelInfo m : models)
			ret.put(m.getId()+"", convert(m));
		
		return ret;
	}
	
	private Map<String, String> convert(ModelInfo m){
		Map<String, String> info = new LinkedHashMap<String, String>();
		int id = m.getId();
		String author = m.getAuthor().trim();
		String name = m.getName().trim();
		String organism = m.getOrganism().trim();
		String taxonomy = m.getTaxonomy().trim();
		String publication = m.getPublicationURL().trim();
		String year = m.getYear().trim();
		boolean hasFile = m.getFormats().size()>0;
		
		info.put(ID, id+"");
		info.put(AUTHOR, author);
		info.put(NAME, name);
		info.put(ORGANISM, organism);
		info.put(TAXONOMY, taxonomy);
		info.put(URL, publication);
		info.put(YEAR, year);
		info.put(DOI, publication.replace("http://dx.doi.org/", ""));
		info.put(HASFILE, hasFile+"");
		
		return info;
	}
	
	public Map<String, Set<String>> getAgregatedInformation(String key, String value){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();
		
		for(Map<String, String> info : infoModels.values()){
			String keyValue = info.get(key);
			String valueValue = info.get(value);
			
			Set<String> set = ret.get(keyValue);
			if(set == null){
				set = new HashSet<String>();
				ret.put(keyValue, set);
			}
			set.add(valueValue);
		}
		return ret;
	}
	
	public Map<String, Set<String>> filteredMapBySize(Map<String, Set<String>> info, int size){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();
		for(String id : info.keySet()){
			if(info.get(id).size() > size)
				ret.put(id, info.get(id));
				
		}
		return ret;
	}
	
	public Map<String, Set<String>> getFilteredRepeatedModel(){
		Map<String, Set<String>> map = getAgregatedInformation(NAME, ID);
		return filteredMapBySize(map, 1);
	}
	
	public Set<String> getRepeatedModel(){
		return new TreeSet<String>(getFilteredRepeatedModel().keySet());
	}
	
	public Map<String, Set<String>> getFilteredRepeatedDoi(){
		Map<String, Set<String>> map = getAgregatedInformation(DOI, ID);
		return filteredMapBySize(map, 1);
	}
	
	public Set<String> getRepeatedDoid(){
		
		return new TreeSet<String>(getFilteredRepeatedDoi().keySet());
	}
	
	public Map<String, Set<String>> getFilteredDoiModel(){
		Map<String, Set<String>> map = getAgregatedInformation(DOI, NAME);
		return filteredMapBySize(map, 1);
	}
	
	public Set<String> getDoiMoreThanOneModel(){
		return new TreeSet<String>(getFilteredDoiModel().keySet());
	}
	
	public void printSomeInfo(Set<String> info) throws IOException{
		Map<String, Map<String, String>> infoFiltered = new TreeMap<String, Map<String,String>>(infoModels);
		infoFiltered.keySet().retainAll(info);
		writeInfo(new OutputStreamWriter(System.out), "\t", infoFiltered);
	}
	
	@Override
	public String toString() {
		StringWriter w = new StringWriter();
		try {
			writeInfo(w, "\n", infoModels);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return w.toString();
	}
	
	public void saveTSVFile(String file) throws IOException{
		FileWriter w = new FileWriter(file);
		writeInfo(w, "\t", infoModels);
		w.close();
	}
	
	private void writeInfo(Writer w, String sepFile, Map<String, Map<String, String>> infoToPrint) throws IOException{
		String header = "ID" + sepFile + "Model" + sepFile + "Organism" +sepFile+ "Taxonomy"+ sepFile + "Author" + sepFile + "Year" + sepFile + "Publication" +sepFile+"HasFile"+ "\n";
		w.write(header);
		String info = MapUtils.prettyMAP2LineKeySt(infoToPrint, MapUtils.getSecondMapKeys(infoToPrint),"null");
		w.write(info);
		w.flush();
		
	}
}
