package com.silicolife.metabolimodelanalysis.utils;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.SAXException;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.ErrorsException;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLReader;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidationException;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.MapUtils;

public class ModelDatabaseManagementTest {

//	@Test
	public void test() {
		System.out.println(ModelDatabaseManagement.getInstance());
	}
	
//	@Test
	public void testAgregator(){
		System.out.println(ModelDatabaseManagement.getInstance().getAgregatedInformation(ModelDatabaseManagement.DOI, ModelDatabaseManagement.ID));
	}
	
//	@Test
	public void testRepetedDoi(){
		System.out.println(ModelDatabaseManagement.getInstance().getRepeatedDoid());
	}
	
//	@Test
	public void testRepetedModel(){
		System.out.println(ModelDatabaseManagement.getInstance().getRepeatedModel());
	}
	
//	@Test
	public void printRepeateadModel(){
		System.out.println("RepeatedModel");
		MapUtils.prettyPrint(ModelDatabaseManagement.getInstance().getFilteredRepeatedModel());
	}
	
//	@Test
	public void printRepeatedDoi(){
		System.out.println("RepeatedDoi");
		MapUtils.prettyPrint(ModelDatabaseManagement.getInstance().getFilteredRepeatedDoi());
	}
	
//	@Test
	public void printRepeatedDoidAllInfo() throws IOException{
		System.out.println("RepeatedDoi All Info");
		Map<String, Set<String>> allinfo = ModelDatabaseManagement.getInstance().getFilteredRepeatedDoi();
		
		for (String id : allinfo.keySet()) {
			Set<String> i = allinfo.get(id);
			ModelDatabaseManagement.getInstance().printSomeInfo(i);
			System.out.println();
			
		}
	}
	
	@Test
	public void readSbml(){
	
		
		Map<String, String> info = ModelDatabaseManagement.getInstance().getOriginalSBML();
		
		MapUtils.prettyPrint(info);
		for(String id : info.keySet()){
			
			String file = info.get(id);
			System.out.println(id + "\t" + file);
			try {
				JSBMLReader reader = new JSBMLReader(file, "", false);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ErrorsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSBMLValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				System.out.println(e.getProblems().size() + "\t" + e.getProblemsByClass());
			}
			
		}
	}

}
