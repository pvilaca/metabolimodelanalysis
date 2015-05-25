package com.silicolife.metabolimodelanalysis.avaliators.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;

public class BioModelsName implements Evaluators<Document>{

	@Override
	public String getHeaders() {
		return "Annotation";
	}

	@Override
	public String getValues(Document obj) {
		NodeList nl = obj.getElementsByTagName("model");
		Node elem = nl.item(0);
		String ret = ((Element)elem).getAttribute("name");
		return ret;
	}

}
