package com.silicolife.metabolimodelanalysis.avaliators;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.ErrorsException;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLReader;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidationException;

import com.silicolife.metabolimodelanalysis.mains.InputMethods;

public class LoadContainerJob implements Callable<Container>{


	private static Logger log = Logger.getLogger(RunEvaluatorsJob.class);
//	static{
//		log.setLevel(Level.ALL);
//	}
	
	public static String sep = "\t";
	protected File file;
	protected Container cont;
	
	protected String value;
	
	
	
	public LoadContainerJob(File file) {
		super();
		this.file = file;
	}

//	@Override
	public void run() throws XMLStreamException, ErrorsException, IOException, ParserConfigurationException, SAXException, JSBMLValidationException {
		
		String name = file.getName();
		name = name.replace(".xml", "");
		log.warn("Reading... " + name);
		FileInputStream fis = new FileInputStream(file);
		
		System.out.println(name);
//		JSBMLValidator val = new JSBMLValidator(file);
//		try {
//			val.validate();
//			log.info("Validate complete without erros");
//		} catch (JSBMLValidationException e) {
//			log.error("Problems with model " + name+ "\t" + e.getProblems().size()+"\t");
//			for(Class<ElementValidator> klass : e.getProblemsByClass().keySet()){
//				log.error(klass.toString()+"#"+e.getProblemsByClass().get(klass).size()+"|");
//			}
//		}
		
		
		JSBMLReader reader = new JSBMLReader(fis, name, false);
		Container cont = new Container(reader, false);
		fis.close();
		cont.setModelName(name);
		
		String b = InputMethods.getBiomassIdModels().get(name);
		if(b!=null)
			cont.setBiomassId(b);
		this.cont = InputMethods.putDrainsStrategy(cont);
		
		System.out.println("Ending " + name);
	}

//	@Override
//	public String getResult() {
//		return value;
//	}
//
//	@Override
//	public Exception getError() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Container call() throws Exception {
		try {
			run();
		} catch (Exception e) {
			cont = null;
		}
		
		return cont;
	}

}
