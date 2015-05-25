package com.silicolife.metabolimodelanalysis.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatsManagement {
	
	private Map<String, String> changeNames;
	
	public StatsManagement() {
		
		populateSBMLChangeNames();
		
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

	public Map<String, String> getModeIdAndFiles(){
		
		Map<String, Set<String>> files = FilesManagement.getInstance().getAllSBMLFiles();
		Map<String, Set<String>> models = ModelDatabaseManagement.getInstance().getDoiToModelId();
		Map<String, String> modelToFiles = new HashMap<String, String>();
		
		boolean allOk = true;
		for(String id : files.keySet()){
			
			Set<String> filesSet = files.get(id);
			Set<String> modelIdSet = models.get(id);
			boolean verified = modelIdSet != null && filesSet!=null && filesSet.size() ==1 && modelIdSet.size() == 1;
			
			if(verified){
				String modelId = modelIdSet.iterator().next();
				String fileName = filesSet.iterator().next();
				modelToFiles.put(modelId, fileName);
			}else
				for(String modelId : modelIdSet){
					
					String fileName = changeNames.get(modelId);
					verified = modelId !=null;
					if(!verified)		
						System.out.println(id + "\t" + files.get(id) + "\t" + models.get(id) + "\t" + verified + "\t" + modelId);
					else
						modelToFiles.put(modelId, fileName);
					allOk = allOk && verified;
				}
		}
		
		if(!allOk)
			throw new RuntimeException("Problem in model SBML Files!!");
		
		return modelToFiles;
	}

	public void normalizeAllSbmlModels(String folder){
		
		
	}
	
	private String converModelIdToFileName(String modelId) {
		return modelId.replaceAll(" ", "_") + ".xml";
	}
	
	

}
