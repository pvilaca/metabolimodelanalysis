package com.silicolife.metabolimodelanalysis.utils;

import java.io.FileNotFoundException;
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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import pt.uminho.ceb.biosystems.mew.availablemodelsapi.RestClient;
import pt.uminho.ceb.biosystems.mew.availablemodelsapi.ds.ModelInfo;
import pt.uminho.ceb.biosystems.mew.availablemodelsapi.ds.ModelsIndex;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidationException;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidator;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.jsbml.validators.ElementValidator;
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
	
	
	public static final String ORIGINAL_SBML = "ORIGINAL_SBML";
	public static final String ORIGINAL_SBML_VALID = "ORIGINAL_SBML_VALID";
	public static final String ORIGINAL_SBML_RESOLVABLE = "ORIGINAL_SBML_RESOLVABLE";
	
	private static ModelDatabaseManagement _instance;
	
	public static ModelDatabaseManagement getInstance(){
		if(_instance == null){
			_instance = new ModelDatabaseManagement();
		}
		return _instance;
	}
	
	private Map<String, Map<String, String>> infoModels;
	private Map<String, String> changeNames;
	
	private ModelDatabaseManagement(){
		try {
			infoModels = populateinfo();
			populateSBMLChangeNames();
		} catch (Exception e) {
			throw new RuntimeException("Server models problem!!", e);
		}
		
		verifyOriginalFiles();
	}
	
	public Map<String, Map<String, String>> getInfoModels() {
		return infoModels;
	}
	
	private void populateSBMLChangeNames() {
		changeNames = new HashMap<String, String>();
		changeNames.put("yeast 7.00", "yeast_7.00_cobra.xml");
		changeNames.put("iFap484", "iFap484.V01.00.xml");
		changeNames.put("iBif452", "iBif452.V01.00.xml");
		
		changeNames.put("iSS352", "mmc11.xml");
		
		changeNames.put("iAK692", "1752-0509-6-71-s5.xml");
		changeNames.put("iAL1006", "iAl1006 v1.00.xml");
		
		changeNames.put("iCyh755", "8802 iCyh.xml");
		changeNames.put("iCyc792", "7424 iCyc.xml");
		changeNames.put("iCyn731", "7425 iCyn.xml");
		changeNames.put("iCyj826", "7822 iCyj.xml");
		changeNames.put("iCyp752", "8801 iCyp.xml");
		
		changeNames.put("iCG238", "1471-2180-12-s1-s5-s5.xml");
		changeNames.put("iCG230", "1471-2180-12-s1-s5-s6.xml");
		
		changeNames.put("iNJ661m", "1752-0509-4-160-s2.xml");
		changeNames.put("iNJ661v", "1752-0509-4-160-s4.xml");
		
	}

	
	private Map<String, Map<String, String>> populateinfo() throws Exception {
		
		Map<String, Map<String, String>> ret = new HashMap<String, Map<String, String>>();
		RestClient client = new RestClient();
		
		ModelsIndex models = client.index(false);
		for(ModelInfo m : models){
			
			String id = m.getName().trim();
			if(ret.containsKey(id))
				throw new RuntimeException("Model alredy exists " + id);
			
			ret.put(id+"", convert(m));
		}
		
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
		info.put(DOI, publication.replace("http://dx.doi.org/", "").trim());
		info.put(HASFILE, hasFile+"");
		
		return info;
	}
	
	public Map<String, Set<String>> getAgregatedInformation(String key, String value){
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();
		
		for(Map<String, String> info : infoModels.values()){
			String keyValue = info.get(key);
			String valueValue = info.get(value);
			if(valueValue == null) continue;
			
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
	
	public Map<String, Set<String>> getDoiToModelId(){
		return getAgregatedInformation(DOI, NAME);
	}
	
	public Map<String, String> getModeIdToId(){
		Map<String, Set<String>> info = getAgregatedInformation(NAME, ID);
		return convertToDic(info);
	}
	
	public Map<String, String> getModeIdToDoi() {
		Map<String, Set<String>> info = getAgregatedInformation(NAME, DOI);
		return convertToDic(info);

	}
	
	private Map<String, String> convertToDic(Map<String, Set<String>> info){
		Map<String, String> dic = new HashMap<String, String>();
		for(String id : info.keySet()){
			Set<String> ids = info.get(id);
			if(ids.size() > 1)
				throw new RuntimeException("Problem in model ids " + id + ids);
			
			dic.put(id, ids.iterator().next());
		}
		
		return dic;
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
	
	public Map<String, String> getOriginalSBML(){
		Map<String, Set<String>> info = getAgregatedInformation(NAME, ORIGINAL_SBML);
		
//		MapUtils.prettyPrint(info);
		return convertToDic(info);
	}
	
	public Set<String> getModelsWithOriginalSBML(){
		return new TreeSet<String>(getOriginalSBML().keySet());
	}
	
	
	private Map<String, String> verifyOriginalFiles(){
		
		Map<String, Set<String>> files = FilesManagement.getInstance().getAllSBMLFiles();
		Map<String, String> modelToDoi = getModeIdToDoi();
		String absolutePathMat = FilesManagement.getInstance().getAbsolutePathSuplementaryMat();
		Map<String, Set<String>> models = getDoiToModelId();
		Map<String, String> modelToFiles = new HashMap<String, String>();
		
		boolean allOk = true;
		for(String id : files.keySet()){
			
			Set<String> filesSet = files.get(id);
			Set<String> modelIdSet = models.get(id);
			boolean verified = modelIdSet != null && filesSet!=null && filesSet.size() ==1 && modelIdSet.size() == 1;
			
			if(verified){
				String modelId = modelIdSet.iterator().next();
				String fileName = filesSet.iterator().next();
				
				infoModels.get(modelId).put(ORIGINAL_SBML, absolutePathMat+ "/"+ modelToDoi.get(modelId)+"/"+fileName);
				modelToFiles.put(modelId, fileName);
			}else{
				System.out.println(id + "\t"+ modelIdSet);
				for(String modelId : modelIdSet){
					
					String fileName = changeNames.get(modelId);
					verified = modelId !=null;
					if(!verified)		
						System.out.println(id + "\t" + files.get(id) + "\t" + models.get(id) + "\t" + verified + "\t" + modelId);
					else
						modelToFiles.put(modelId, fileName);
					
					infoModels.get(modelId).put(ORIGINAL_SBML, absolutePathMat+ "/"+ modelToDoi.get(modelId)+"/"+fileName);
					allOk = allOk && verified;
				}
			}
		}
		
		if(!allOk)
			throw new RuntimeException("Problem in model SBML Files!!");
		
		return modelToFiles;
	}
	
	public Map<String, Map<Class<ElementValidator>, Set<String>>> verifyOriginalSBML() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
		
		Map<String, Map<Class<ElementValidator>, Set<String>>> erros = new HashMap<String, Map<Class<ElementValidator>, Set<String>>>(); 
		Map<String, String> sbmlFiles = getOriginalSBML();
		for(String id : sbmlFiles.keySet()){
			
			String file = sbmlFiles.get(id);
			
			JSBMLValidator validator = new JSBMLValidator(file);
			boolean valid = true;
			boolean resolvable = true;
			try {
				validator.validate();
			} catch (JSBMLValidationException e) {
				valid=false;
				erros.put(id, e.getProblemsByClass());
				resolvable = e.isSBMLResolvable();
				infoModels.get(id).put(ORIGINAL_SBML_VALID, valid+"");
				infoModels.get(id).put(ORIGINAL_SBML_RESOLVABLE, resolvable+"");
			}
			
			System.out.println(id + "\t" +valid + "\t" + resolvable + "\t"+ file);
//			JSBMLFileValidator validator = new JSBMLFileValidator();
//			validator
		}
		return erros;
	}
	
	
	
	@Override
	public String toString() {
		StringWriter w = new StringWriter();
		try {
			writeInfo(w, "\t", infoModels);
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
//		String header = "ID" + sepFile + "Model" + sepFile + "Organism" +sepFile+ "Taxonomy"+ sepFile + "Author" + sepFile + "Year" + sepFile + "Publication" +sepFile+"HasFile"+ "\n";
//		w.write(header);
		String info = MapUtils.prettyMAP2LineKeySt(infoToPrint, MapUtils.getSecondMapKeys(infoToPrint),"null");
		w.write(info);
		w.flush();
		
	}
}
