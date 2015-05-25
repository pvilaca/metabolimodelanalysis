package com.silicolife.metabolimodelanalysis.avaliators.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidator;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class BioModelsTaxonom implements Evaluators<Document>{

	@Override
	public String getHeaders() {
		return "Taxonomy Id";
	}

	@Override
	public String getValues(Document obj) {
		NodeList nl = obj.getElementsByTagName("model");
//		System.out.println(getHeaders());
		Node elem = nl.item(0);
		return ((Element)((Element)((Element)elem).getElementsByTagName("bqbiol:occursIn").item(0)).getElementsByTagName("rdf:li").item(0)).getAttribute("rdf:resource");
	}

	
	public static String getInfo(Document doc, String attribute, String... path){
		
		String ret = null;
		NodeList nl = null;
		Element elem = (Element) doc;
		
		int i =0;
		do{
			nl = elem.getElementsByTagName(path[i]);
			elem = (Element) nl.item(0);
			
			i++;
		}while(i < path.length);
		
		ret = elem.getAttribute(attribute);
		
		return ret;
	}
	
	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		
		BioModelsTaxonom bio = new BioModelsTaxonom();
		Document doc = JSBMLValidator.readStream(new FileInputStream("../paper_models/models/biomodels/test/BMID000000142750.xml"));
		
		System.out.println(bio.getValues(doc));
	}
}


