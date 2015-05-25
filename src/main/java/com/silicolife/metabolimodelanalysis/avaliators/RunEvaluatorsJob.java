package com.silicolife.metabolimodelanalysis.avaliators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.mewcore.model.converters.ContainerConverter;
import pt.uminho.ceb.biosystems.mew.mewcore.model.exceptions.InvalidSteadyStateModelException;
import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;

import com.silicolife.metabolimodelanalysis.mains.InputMethods;
import com.silicolife.metabolimodelanalysis.mains.MethodsWithMemory;

public class RunEvaluatorsJob implements Callable<String>{

public static String sep = "\t";
	
	
	protected String containerName;
	protected Container cont;
	
	protected String value;
	
	public RunEvaluatorsJob(String containerName, Container cont) {
		super();
		this.containerName = containerName;
		this.cont = cont;
	}

	public void run() {
		if(cont == null){
			value = containerName + "\tnull container";
			return ;
		}
		
		List<Evaluators<Container>> evalContainer = InputMethods.getContainerAvaliators();
		List<Evaluators<SteadyStateModel>> evalModel = InputMethods.getModelAvaliators();
		List<Evaluators<SteadyStateSimulationResult>> simulationEval = InputMethods.getSimulationAvaliators();
		
		
		List<Evaluators<?>> evaluators = new ArrayList<Evaluators<?>>();
		evaluators.addAll(evalContainer);
		evaluators.addAll(evalModel);
		evaluators.addAll(simulationEval);
	
		SteadyStateModel model = null;
		MethodsWithMemory.addContainer(cont);
		
		try {
			model = (SteadyStateModel) ContainerConverter.convert(cont);
			model.setId(containerName);
		} catch (InvalidSteadyStateModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		value = containerName+sep+InputMethods.applyAvaliators(evalContainer, cont, sep) + InputMethods.applyAvaliators(evalModel, model, sep) ;
		SteadyStateSimulationResult result = MethodsWithMemory.wt(model, null);
		value+= InputMethods.applyAvaliators(simulationEval, result, sep);
		
		try {
			result = MethodsWithMemory.wt(model, InputMethods.getNormalizedEC());
			value+= InputMethods.applyAvaliators(simulationEval, result, sep);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	@Override
	

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
	public String call() throws Exception {
		try {
			run();
		} catch (Exception e) {
			value = containerName + "\tstats problem Problem!!!\t" + e.getMessage();
		}
		
		return value;
	}

}
