package com.silicolife.metabolimodelanalysis.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
		System.out.println(folder.getName()+" "+ isAllFolders(list));
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
	
	
}
