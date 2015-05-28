package com.silicolife.metabolimodelanalysis.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.jsbml.validators.ElementValidator;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.collection.CollectionUtils;
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


//	@Test
	public void getOriginalFiles(){
		System.out.println(ModelDatabaseManagement.getInstance().getOriginalSBML());
	}
	
//	@Test
	public void getModelsWithSBML(){
		System.out.println(ModelDatabaseManagement.getInstance().getOriginalSBML());
		System.out.println(ModelDatabaseManagement.getInstance().getOriginalSBML().size());
	}
	
	@Test
	public void verifyOriginalModels() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
		
		Map<String, Map<Class<ElementValidator>, Set<String>>> erros = ModelDatabaseManagement.getInstance().verifyOriginalSBML();
		for(String id : erros.keySet()){
			
			System.out.println("####################  " + id + "("+ModelDatabaseManagement.getInstance().getInfoModels().get(id).get("ORIGINAL_SBML_RESOLVABLE") +")"  + "   #####################" + ModelDatabaseManagement.getInstance().getInfoModels().get(id).get("ORIGINAL_SBML"));
			
			System.out.println(CollectionUtils.join(CollectionUtils.aggregate(erros.get(id).values()), "\n"));
			System.out.println("#####################################################");
		}
		
	}
	
	@Test
	public void verifyOriginalModelsTypes() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
		
		Map<String, Map<Class<ElementValidator>, Set<String>>> erros = ModelDatabaseManagement.getInstance().verifyOriginalSBML();
		for(String id : erros.keySet()){
			
			System.out.println("####################  " + id + "("+ModelDatabaseManagement.getInstance().getInfoModels().get(id).get("ORIGINAL_SBML_RESOLVABLE") +")"  + "   #####################" + ModelDatabaseManagement.getInstance().getInfoModels().get(id).get("ORIGINAL_SBML"));
			
			System.out.println(CollectionUtils.join(erros.get(id).keySet(), "\n"));
			System.out.println("#####################################################");
		}
		
	}
}