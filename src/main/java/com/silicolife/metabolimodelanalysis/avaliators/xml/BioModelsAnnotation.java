package com.silicolife.metabolimodelanalysis.avaliators.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class BioModelsAnnotation implements Evaluators<Document>{

	@Override
	public String getHeaders() {
		return "Annotation";
	}

	@Override
	public String getValues(Document obj) {
		NodeList nl = obj.getElementsByTagName("model");
//		System.out.println(getHeaders());
		Node elem = nl.item(0);
		String ret = null;
		nl = ((Element)((Element)elem).getElementsByTagName("annotation").item(0)).getElementsByTagName("bqbiol:isDescribedBy");
		if(nl.getLength()>0){
			elem = nl.item(0);
			ret=((Element)((Element)elem).getElementsByTagName("rdf:li").item(0)).getAttribute("rdf:resource");
		}
		return ret;
	}

}
