package com.silicolife.metabolimodelanalysis.avaliators.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class BioModelsKEGGOrganismId implements Evaluators<Document>{

	@Override
	public String getHeaders() {
		return "ORGANISM KEGG ID";
	}

	@Override
	public String getValues(Document obj) {
		NodeList nl = obj.getElementsByTagName("model");
//		System.out.println(getHeaders());
		Node elem = nl.item(0);
		return ((Element)((Element)((Element)elem).getElementsByTagName("bqmodel:isDerivedFrom").item(0)).getElementsByTagName("rdf:li").item(0)).getAttribute("rdf:resource");
	}

}
