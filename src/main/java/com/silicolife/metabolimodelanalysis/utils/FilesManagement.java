package com.silicolife.metabolimodelanalysis.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class FilesManagement {
	
	private static FilesManagement _instance = null;
	public static FilesManagement getInstance(){
		if(_instance == null)
			_instance = new FilesManagement();
		return _instance;
	}
	
	String supplementaryFolder;
	String sbmlFolder;
	String excelFolder;
	String pdfFolder;
	
	Set<String> allDois;
	Set<String> sbmlDois;
	Set<String> excelDois;
	Set<String> pdfDois;
	
	private FilesManagement(){
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("FileManagement.prop"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		supplementaryFolder = prop.getProperty("SUPPLEMENTARY_MATERIALS");
		sbmlFolder = prop.getProperty("SBML");
		excelFolder = prop.getProperty("EXCEL");
		pdfFolder = prop.getProperty("PDF");
		
	}
	
	
	public Set<String> getAllDois(){
		if(allDois == null){
			allDois = getAllDoisFromFolder(supplementaryFolder);
		}
		return allDois;
	}
	
	public Set<String> getExcelDois() {
		if(excelDois == null)
			excelDois = getAllDoisFromFolder(excelFolder);
		return excelDois;
	}
	
	public Set<String> getPdfDois() {
		if(pdfDois == null)
			pdfDois = getAllDoisFromFolder(pdfFolder);
		return pdfDois;
	}
	
	public Set<String> getSbmlDois() {
		if(sbmlDois == null)
			sbmlDois = getAllDoisFromFolder(sbmlFolder);
		return sbmlDois;
	}
	
	
	private Set<String> getAllDoisFromFolder(String folder){
		
		return getAllDoisFromFolderAux("", new File(folder), new HashSet<String>());
	}
	
	private Set<String> getAllDoisFromFolderAux(String back, File folder, Set<String> dois){
		
		File[] list = folder.listFiles();
		if (folder.isDirectory() && isAllFolders(list)){
			for(File f:list)
				getAllDoisFromFolderAux((back.equals("")?"":back+"/")+f.getName(), f, dois);	
		}else{
			dois.add(back);
		}
		
		return dois;
	}
	
	private boolean isAllFolders(File[] files){
		boolean ret = true;
		
		for(int i =0; i < files.length && ret; i++){
			ret = ret && files[i].isDirectory();
		}
		
		return ret;
	}
	
	
	public Map<String, Set<String>> getAllSBMLFiles(){
		return getAllFiles(supplementaryFolder, new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
	}
	
	public String getAbsolutePathSuplementaryMat(){
		
		File f = new File(supplementaryFolder);
		return f.getAbsolutePath();
	}
	
	public Map<String, Set<String>> getAllFiles(String folder, FilenameFilter filter){
		
		
		Set<String> allDois = getAllDoisFromFolder(folder);
		Map<String, Set<String>> info = new HashMap<String, Set<String>>();
		for(String d : allDois){
			
			Set<String> fileSet = getFiles(folder+"/"+d, filter);
			if(fileSet.size() > 0)
				info.put(d, fileSet);
		}
		
		return info;
	}
	
	private Set<String> getFiles(String folder, FilenameFilter filter){
		File f = new File(folder);
		
		String[] files = null;
		
		if(filter != null)
			files = f.list(filter);
		else
			files = f.list();
		return new TreeSet<String>(Arrays.asList(files));
	}
	
	
//	public void normalizeSBMLFileNames(String folder){
//		Map<K, V>
//	}
	
	
	
	
}
