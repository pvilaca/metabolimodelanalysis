package com.silicolife.metabolimodelanalysis.utils;

import java.util.Set;

import org.junit.Test;

public class FilesManagementTest{

	@Test
	public void getAllDoi() {
		Set<String> t = FilesManagement.getInstance().getAllDois();
		System.out.println("All Dois: " + t.size() + " "+ t );
		
	}
	
	@Test
	public void getAllSBMLFiles() {
		System.out.println(FilesManagement.getInstance().getAllSBMLFiles());
		
	}
}
