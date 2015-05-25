package com.silicolife.metabolimodelanalysis.avaliators;

public interface Evaluators<T extends Object> {

	String getHeaders();
	String getValues(T obj);
}
