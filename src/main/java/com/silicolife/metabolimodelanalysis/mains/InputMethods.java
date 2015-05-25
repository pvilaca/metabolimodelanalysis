package com.silicolife.metabolimodelanalysis.mains;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import pt.uminho.ceb.biosystems.mew.availablemodelsapi.Format;
import pt.uminho.ceb.biosystems.mew.availablemodelsapi.RestClient;
import pt.uminho.ceb.biosystems.mew.availablemodelsapi.ds.ModelInfo;
import pt.uminho.ceb.biosystems.mew.availablemodelsapi.ds.ModelsIndex;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLReader;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidationException;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidator;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.jsbml.validators.ElementValidator;
import pt.uminho.ceb.biosystems.mew.mewcore.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.mewcore.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.mewcore.model.converters.ContainerConverter;
import pt.uminho.ceb.biosystems.mew.mewcore.model.exceptions.InvalidSteadyStateModelException;
import pt.uminho.ceb.biosystems.mew.mewcore.model.steadystatemodel.SteadyStateModel;
import pt.uminho.ceb.biosystems.mew.mewcore.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.collection.CollectionUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.MapUtils;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;

import com.silicolife.metabolimodelanalysis.avaliators.Evaluators;
import com.silicolife.metabolimodelanalysis.avaliators.LoadContainerJob;
import com.silicolife.metabolimodelanalysis.avaliators.RunEvaluatorsJob;
import com.silicolife.metabolimodelanalysis.avaliators.container.BiomassId;
import com.silicolife.metabolimodelanalysis.avaliators.container.BiomassName;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountBalancedReactions;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerCoffactorReactions;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerCoffactorRevReactions;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerCompartments;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerDeadEnds;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerDrains;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerGenes;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerReactions;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerRevReactions;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerRevReactionsByFlag;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerSingleCicles;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountContainerZeroFluxes;
import com.silicolife.metabolimodelanalysis.avaliators.container.CountECNumbers;
import com.silicolife.metabolimodelanalysis.avaliators.container.NotDeclaredMetabolites;
import com.silicolife.metabolimodelanalysis.avaliators.model.CountCiclesWithFlux;
import com.silicolife.metabolimodelanalysis.avaliators.model.SimulateFBAModel;
import com.silicolife.metabolimodelanalysis.avaliators.simulations.CountABSFluxGreaterValues;
import com.silicolife.metabolimodelanalysis.avaliators.simulations.CountABSFluxValues;
import com.silicolife.metabolimodelanalysis.avaliators.simulations.LPSolutionType;
import com.silicolife.metabolimodelanalysis.avaliators.simulations.SpaceFBACalculation;
import com.silicolife.metabolimodelanalysis.avaliators.simulations.UnboundedBiomassFixedFluxesFVACalculation;
import com.silicolife.metabolimodelanalysis.avaliators.simulations.UnboundedBiomassFixedFluxesRestrictedFVACalculation;
import com.silicolife.metabolimodelanalysis.avaliators.xml.BioModelsAnnotation;
import com.silicolife.metabolimodelanalysis.avaliators.xml.BioModelsKEGGOrganismId;
import com.silicolife.metabolimodelanalysis.avaliators.xml.BioModelsName;
import com.silicolife.metabolimodelanalysis.avaliators.xml.BioModelsTaxonom;

public class InputMethods {

	public static String sepFile = "\t";
	
	static public String getDataBaseModelInfo() throws Exception{
		StringBuffer buffer = new StringBuffer();
		String header = "ID" + sepFile + "Model" + sepFile + "Organism" +sepFile+ "Taxonomy"+ sepFile + "Author" + sepFile + "Year" + sepFile + "Publication" +sepFile+"SBML"+sepFile+"Excel"+ "\n";
		
		buffer.append(header);
		RestClient req = new RestClient();
		ModelsIndex indx = req.index(false);
		Iterator<ModelInfo> it = indx.iterator();
		
		while(it.hasNext()){
			ModelInfo f = it.next();
			int id = f.getId();
			String author = f.getAuthor();
			String name = f.getName();
			String organism = f.getOrganism();
			String taxonomy = f.getTaxonomy();
			String publication = f.getPublicationURL();
			String year = f.getYear();
			boolean hasSBML = f.getFormats().contains(Format.SBML);
			boolean hasExcel = f.getFormats().contains(Format.EXCEL);
			
			String lineFile = id + sepFile + name + sepFile + organism +sepFile+ taxonomy+ sepFile + author + sepFile + year + sepFile + publication +sepFile+hasSBML+sepFile+hasExcel+ "\n";
			buffer.append(lineFile);
		}
		
		return buffer.toString();
	}
	
	
	public static void saveModelFromDataBase(String folder) throws Exception{
		RestClient req = new RestClient();
		
		Set<Integer> valid = getSbmlValidated();
		ModelsIndex indx = req.index(false);
		
		Iterator<ModelInfo> it = indx.iterator();
		
		String fileInfo = folder + "/infoBD.csv";
		FileWriter info = new FileWriter(fileInfo);
		String header = "ID" + sepFile + "Model" + sepFile + "Organism" +sepFile+ "Taxonomy"+ sepFile + "Author" + sepFile + "Year" + sepFile + "Publication" +sepFile+"HasFile"+sepFile+"Valid?"+sepFile+"Erro"+ "\n";
		info.write(header);
		
		while(it.hasNext()){
			
			ModelInfo f = it.next();
			int id = f.getId();
			String author = f.getAuthor();
			String name = f.getName();
			String organism = f.getOrganism();
			String taxonomy = f.getTaxonomy();
			String publication = f.getPublicationURL();
			String year = f.getYear();
			boolean hasFile = f.getFormats().size()>0; 
			String erro = "";
			
			try {
				if(f.getFormats().contains(Format.SBML)){
					FileWriter file = new FileWriter(folder+"/"+ name.replace("/", "_")+".xml");
					InputStream stream = req.getSBMLStream(id);
					
					FileUtils.saveInputStreamInFile(stream, file);
					
					
					stream.close();
					file.close();
				}
			
			} catch (Exception e) {
				e.printStackTrace();
				erro = e.getMessage();
			}
			
			String lineFile = id + sepFile + name + sepFile + organism +sepFile+ taxonomy+ sepFile + author + sepFile + year + sepFile + publication +sepFile+hasFile+sepFile+valid.contains(id)+sepFile+erro+ "\n";
			System.out.println(lineFile);
			info.write(lineFile);
			
		}
		info.close();
	}
	
	
	public static void saveGenomeScaleModelFromBioModels(String folder, boolean verify) throws IOException{
		Map<String, String[]> info = FileUtils.readTableFileFormat("../paper_models/biomodels.tsv", "\t", 0);
		for(String id : info.keySet()){
			String erro = "";
			
//			http://www.ebi.ac.uk/biomodels-main/download?mid=BMID000000142305
			String urlString = "http://www.ebi.ac.uk/biomodels-main/download?mid="+id;
			System.out.println("Reading " + id + " ===>> " + urlString);
			URL url = new URL(urlString);
			
			try {
//				url.openConnection();
				File f = new File(folder+"/"+ id.replace("/", "_")+".xml");
				System.out.println(verify + "\t" + f.exists());
				if(!(verify && f.exists())){

					FileWriter file = new FileWriter(f);
					InputStream stream = url.openStream();
						
					FileUtils.saveInputStreamInFile(stream, file);
						
						
					stream.close();
					file.close();
				}
			
			} catch (Exception e) {
				e.printStackTrace();
				erro = e.getMessage();
			}
			
			System.out.println("done!! " + erro);
			
		}
	}
	
	public static void saveModelFromSeedDataBase(String folder) throws Exception{
		Map<String, String[]> info = FileUtils.readTableFileFormat("../paper_models/modelSeedModels.csv", "\t", 0);
		
		
		for(String id : info.keySet()){
			String erro = "";
			String urlString = "http://bioseed.mcs.anl.gov/~chenry/FIG/CGI/ModelSEEDdownload.cgi?model="+id+"&file=SBML";
			System.out.println("Reading " + id + " ===>> " + urlString);
			URL url = new URL(urlString);
			
			try {
//				url.openConnection();
				FileWriter file = new FileWriter(folder+"/"+ id.replace("/", "_")+".xml");
				InputStream stream = url.openStream();
					
				FileUtils.saveInputStreamInFile(stream, file);
					
					
				stream.close();
				file.close();
			
			} catch (Exception e) {
				e.printStackTrace();
				erro = e.getMessage();
			}
			
			System.out.println("done!! " + erro);
			
		}
	}
	
	
	public static Set<Integer> getSbmlValidated() throws Exception{
		
		Set<Integer> ret = new HashSet<Integer>();
		RestClient req = new RestClient();
		
		
		ModelsIndex indx = req.index(true);
		
		Iterator<ModelInfo> it = indx.iterator();
		
		while (it.hasNext()) {
			ModelInfo info = it.next();
			ret.add(info.getId());
		}
		
		return ret;
	}
	
	
	public static Map<String, Document> getDocumentsFromFolder(String folder){
		Map<String, Document> ret = new HashMap<String, Document>();
		File fFolder = new File(folder);
		File[] allFiles = fFolder.listFiles();
	
		
		for(File f : allFiles){
			
			String name = f.getName();
			
			if(name.endsWith(".xml")){
			System.out.println("Reading: " + name);
				try {
					
					name = name.replace(".xml", "");
					
					Document doc = JSBMLValidator.readStream(new FileInputStream(f));
					ret.put(name, doc);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			
		}
		return ret;
	}
	
	private static Map<String, Container> getModelFromFolderThreaded(
			String folderSBMLs, int threads) throws InterruptedException, ExecutionException {
		
		
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor manager = new ThreadPoolExecutor(threads, 10000, 0L, TimeUnit.MILLISECONDS, workQueue);
		
		File fFolder = new File(folderSBMLs);
		File[] allFiles = fFolder.listFiles();
		
		List<Future<Container>> futures = new ArrayList<Future<Container>>();
		List<String> names = new ArrayList<String>();
		for(File f : allFiles){
			if( f.getName().endsWith(".xml")){
				
				String name = f.getName();
				name = name.replace(".xml", "");
				names.add(name);
				
				Future<Container> future = manager.submit(new LoadContainerJob(f));
				futures.add(future);
			}
		}
		
		Map<String, Container> ret = new HashMap<String, Container>();
		int i = 0;
		for(Future<Container> cont : futures){
			Container values = cont.get();
			String name = names.get(i);
			ret.put(name, values);
			i++;
			
		}
		
		manager.shutdown();
		return ret;
	}
	
	
	
	public static Map<String, Container> getModelFromFolder(String folder, String logFile, boolean teste) throws IOException{
		FileWriter log = new FileWriter(logFile);
		
		Map<String, Container> ret = new HashMap<String, Container>();
		Map<String, String> biomass = getBiomassIdModels();
		
		File fFolder = new File(folder);
		File[] allFiles = fFolder.listFiles();
	
		ArrayList<ElementValidator> validator = null;
		
		for(File f : allFiles){
			
			String name = f.getName();
			
			
			String erro = "Done!";
			if(name.endsWith(".xml")){
				
//				System.out.println("Reading: " + name);
				try {
					
					name = name.replace(".xml", "");
					log.write(name);
					FileInputStream fis = new FileInputStream(f);
					
					JSBMLValidator val = new JSBMLValidator(f);
					
					try {
						val.validate();
						log.write("\t0");
					} catch (JSBMLValidationException e) {
						
						if(validator  == null)
							validator = new ArrayList<ElementValidator>(e.getSbmlvalidator().getValidators());
						
						log.write("\t" + e.getProblems().size()+"\t");
						for(ElementValidator v : validator){
							if(e.getProblemsByClass().get(v.getClass())!=null)
								log.write(e.getProblemsByClass().get(v.getClass()).size()+"\t");
							else
								log.write("0" + "\t");
						}
						
						log.write(e.isSBMLResolvable()+"");
						System.out.println("Problem #" + e.getProblems().size());
						System.out.println(CollectionUtils.join(e.getProblems(), "\n"));
					}
					
					
					JSBMLReader reader = new JSBMLReader(fis, name, teste);
					Container cont = new Container(reader, false);
					fis.close();
					cont.setModelName(name);
					
					String b = biomass.get(name);
					if(b!=null)
						cont.setBiomassId(b);
					ret.put(name, putDrainsStrategy(cont));
					
				
				} catch (Exception e) {
					e.printStackTrace();
					erro = e.getMessage();
				}
			
//				try {
////					log.write("\tReading file: "+ f + "\t" + erro+"\n");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				log.append("\n");
				log.flush();
			}
			
			
		}
		log.write("Model ID\tTotal Erros\t");
		for(ElementValidator v : validator)
			log.write(v.getClass().getSimpleName() + "\t");
		
		log.close();
		return ret;
	}
	
	public static Map<String, Container> getModelFromFolder(String folder) throws IOException{
		
		return getModelFromFolder(folder, folder+"/SbmlReading.log", true);
		
	}
	
	
	
	
	public static Map<Integer, Container> getContainers() throws Exception{
		RestClient req = new RestClient();
		
		
		Map<Integer, Container> ret = new HashMap<Integer, Container>();
		ModelsIndex indx = req.index(false);
		Iterator<ModelInfo> it = indx.iterator();
		
		while(it.hasNext()){
			
			ModelInfo f = it.next();
			int id = f.getId();
			String author = f.getAuthor();
			String name = f.getName();
			String organism = f.getOrganism();
			String publication = f.getPublicationURL();
			String year = f.getYear();
			boolean hasFile = f.getFormats().size()>0; 
			List<Format> formats = f.getFormats();
			String erro = "";
			
			if(formats.contains(Format.SBML)){
				try {
					InputStream stream = req.getSBMLStream(id);
					JSBMLReader reader = new JSBMLReader(stream, name, false);
					Container cont = new Container(reader);
					stream.close();
					ret.put(id, putDrainsStrategy(cont));
				
				} catch (Exception e) {
					e.printStackTrace();
					erro = e.getMessage();
				}
			}
			
			String lineFile = id + sepFile + name + sepFile + organism + sepFile + author + sepFile + year + sepFile + publication +sepFile+hasFile+sepFile+erro+ "\n";
			System.out.println(lineFile);
		}
		return ret;
	}
	
	public static Container putDrainsStrategy(Container cont){
		
		
		Container cToReturn =null;
		if(cont!=null){
			cToReturn = cont.clone();
			
			Set<String> bMetabolites = cont.identifyMetabolitesIdByPattern(Pattern.compile(".*_b"));
			if(bMetabolites.size() > 0){
				cToReturn.removeMetabolites(bMetabolites);
				System.out.println("Removing met: " + bMetabolites);
			}else
				if(cont.getDrains().size()==0){
					
					System.out.println("WARNING: ver este modelo" + cont.getModelName());
					System.out.println(cToReturn.getExternalCompartment().getMetabolitesInCompartmentID());
					cToReturn.removeMetabolites(new HashSet<String>(cToReturn.getExternalCompartment().getMetabolitesInCompartmentID()));
					
				}
		}
		return cToReturn;
	}
	
	public static Map<String, String> getBiomassIdModels(){
		Map<String, String> ret  = new HashMap<String, String>();
		ret.put("STM_v1.0", "R_biomass_iRR1083");
//		ret.put("iJP815", "EX_EC9324");
		ret.put("iBT721", "R_biomass_LPL60");
		ret.put("GSMN-TB", "BIOMASSe");
		ret.put("iCM925", "R_biomass");
		ret.put("PpuMBEL1071", "R_Biomass");
		ret.put("iAP358", "R_BIOMass");
		ret.put("iSO783", "R_SO_BiomassMacro_DM_noATP2");
		ret.put("iMB745", "R_overall");	
		ret.put("iKF1028", "EX_C9323");	
		ret.put("iJP962", "EX_EC9324");
		ret.put("iNF518", "R_biomass_LLA");
		ret.put("iAbaylyiV4", "R_GROWTH_DASH_RXN");
		ret.put("iMA789", "R_biomass");
		ret.put("iVW583", "R_1309");
		ret.put("iBsu1103", "bio00006");
		ret.put("SpoMBEL1693", "RXNBiomass");
		ret.put("iJW145", "IR09955");
		ret.put("iMF721", "RXNbiomass");
		ret.put("PpaMBEL1254", "R01288");
		ret.put("iHZ565", "R_OF1");
		ret.put("iCce806", "r_CYANOBM");
		ret.put("iAL1006", "R_r1463");
		ret.put("iWV1314", "R_r1898");
		
		
		return ret;
	}
	
	
	public static List<Evaluators<Container>> getContainerAvaliators(){
		ArrayList<Evaluators<Container>> ret = new ArrayList<Evaluators<Container>>();
		ret.add( new BiomassId());
		ret.add( new BiomassName());
		ret.add( new CountECNumbers());
		ret.add( new CountContainerDrains());
		ret.add( new CountContainerReactions());
		ret.add( new CountContainerRevReactions());
		ret.add( new CountContainerRevReactionsByFlag());
		ret.add( new CountContainerCoffactorReactions());
		ret.add( new CountContainerCoffactorRevReactions());
		ret.add( new CountContainerSingleCicles());
		ret.add( new CountContainerZeroFluxes());
		ret.add( new CountBalancedReactions());
		ret.add( new CountContainerDeadEnds());
		ret.add( new CountContainerCompartments());
		ret.add( new CountContainerGenes());
		ret.add( new NotDeclaredMetabolites());
//		ret.add( new ReactionsWithWrongStoiquiometries());
		ret.add( new CountContainerSingleCicles());
		
		return ret;
	}
	
	public static  List<Evaluators<SteadyStateModel>> getModelAvaliators(){
		ArrayList<Evaluators<SteadyStateModel>> ret = new ArrayList<Evaluators<SteadyStateModel>>();
		ret.add( new SimulateFBAModel());
		ret.add( new CountCiclesWithFlux());
//		ret.add( new DegreeOfFreedomJAMA());
//		ret.add( new DegreeOfFreedom());
		
		try {
			ret.add( new SimulateFBAModel(getNormalizedEC()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		ret.add( new CoPEFBAEvaluator());
		return ret;
	}
	
	public static  List<Evaluators<SteadyStateSimulationResult>> getSimulationAvaliators(){
		ArrayList<Evaluators<SteadyStateSimulationResult>> ret = new ArrayList<Evaluators<SteadyStateSimulationResult>>();
		
		ret.add( new CountABSFluxGreaterValues(0.0));
		ret.add( new CountABSFluxValues(20.0));
		ret.add( new CountABSFluxValues(50.0));
		ret.add( new CountABSFluxValues(100.0));
		ret.add( new LPSolutionType());
		ret.add( new SpaceFBACalculation());
		ret.add( new UnboundedBiomassFixedFluxesFVACalculation());
		ret.add( new UnboundedBiomassFixedFluxesRestrictedFVACalculation());
		
//		try {
//			EnvironmentalConditions ec = getNormalizedEC();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ret.add( new CountABSFluxGreaterValues(0.0));
//		ret.add( new CountABSFluxValues(20.0));
//		ret.add( new CountABSFluxValues(50.0));
//		ret.add( new CountABSFluxValues(100.0));
//		ret.add( new SpaceFBACalculation());
//		ret.add( new UnboundedBiomassFixedFluxesFVACalculation());
//		ret.add( new UnboundedBiomassFixedFluxesRestrictedFVACalculation());
		
		return ret;
	}
	
	private static List<Evaluators<Document>> getDocumentsAvaliators() {
		ArrayList<Evaluators<Document>> ret = new ArrayList<Evaluators<Document>>();
		
		ret.add(new BioModelsName());
		ret.add(new BioModelsTaxonom());
		ret.add(new BioModelsAnnotation());
		ret.add(new BioModelsKEGGOrganismId());
		return ret;
	}
	
	
	
	public static EnvironmentalConditions getNormalizedEC() throws IOException{
		
		int metaboliteIdx = 1;
		int minBound = 3;
		int maxBound = 4;
		
		EnvironmentalConditions normEc = new EnvironmentalConditions("normalized"); 
		Map<String, String[]> metaboliteBounds = FileUtils.readTableFileFormat("../paper_models/drainstreatment/metaboliteToBound", "\t", 0);
		Map<String, String[]> drainMetabolite = FileUtils.readTableFileFormat("../paper_models/drainstreatment/drainToMetabolite", "\t", 0);
		Map<String, String> dTM = new HashMap<String, String>();
		
		for(String id : drainMetabolite.keySet()){
			String metaboliteId = drainMetabolite.get(id)[metaboliteIdx];
			dTM.put(id, metaboliteId);
//			System.out.println(id + "\t" + metaboliteId);
			String lbS = metaboliteBounds.get(metaboliteId)[minBound];
			String ubS = metaboliteBounds.get(metaboliteId)[maxBound];			
			normEc.put(id, new ReactionConstraint(Double.parseDouble(lbS),Double.parseDouble(ubS)));
		}
		
		Map<String, Set<String>> metaboliteToDrains = MapUtils.revertMap(dTM);
		
		EnvironmentalConditions ret = normEc.copy();
		changeECSimple(ret, metaboliteToDrains);
		
		return ret;
	}
	
	
	static public void changeECSimple(EnvironmentalConditions ec, Map<String, Set<String>> metaboliteToDrains){
		
		changeEC("aso3", -10000, 10000, ec, metaboliteToDrains);
		
		changeEC("cl", -10000, 10000, ec, metaboliteToDrains);
		changeEC("cbl1", -10000, 10000, ec, metaboliteToDrains);
		changeEC("cu2", -10000, 10000, ec, metaboliteToDrains);
		changeEC("cobalt2", -10000, 10000, ec, metaboliteToDrains);	
		changeEC("ca2", -10000, 10000, ec, metaboliteToDrains);
//		changeEC("", -10000, 10000, ec);
		
		
		changeEC("fe", -10000, 10000, ec, metaboliteToDrains);
		changeEC("fe2", -10000, 10000, ec, metaboliteToDrains);
		changeEC("fe3", -10000, 10000, ec, metaboliteToDrains);
		
		changeEC("glc", -5, 10000, ec, metaboliteToDrains);
		
//		changeEC("h", -10000, 10000, ec);
		changeEC("h", 0.0, 10000, ec, metaboliteToDrains);
//		changeEC("h2", -10000, 10000, ec);
		
		changeEC("mg2", -10000, 10000, ec, metaboliteToDrains);
		changeEC("mobd", -10000, 10000, ec, metaboliteToDrains);
		changeEC("mn2", -10000, 10000, ec, metaboliteToDrains);
		
		changeEC("nh3", -10000, 10000, ec, metaboliteToDrains);
		changeEC("nh4", -10000, 10000, ec, metaboliteToDrains);
//		changeEC("nh4+", -10000, 10000, ec, metaboliteToDrains);
		changeEC("ni2", -10000, 10000, ec, metaboliteToDrains);
//		changeEC("no3", -10000, 10000, ec);
		changeEC("na", -10000, 10000, ec, metaboliteToDrains);
		
		changeEC("o2", -10000, 10000, ec, metaboliteToDrains);
//		changeEC("oa", -10000, 10000, ec, metaboliteToDrains);
		
//		changeEC("proton", -10000, 10000, ec);
		changeEC("pi", -10000, 10000, ec, metaboliteToDrains);
		
		changeEC("so4", -10000, 10000, ec, metaboliteToDrains);
		
		changeEC("k", -10000, 10000, ec, metaboliteToDrains);
		
		
		
//		changeEC("rdmbzi", -10000, 10000, ec, metaboliteToDrains);
		changeEC("zn2", -10000, 10000, ec, metaboliteToDrains);
		
	}
	
	static public void changeEC(String metaboliteId, double lb, double ub, EnvironmentalConditions ec, Map<String, Set<String>> metaboliteToDrains){
		
//		System.out.println(metaboliteId);
		Set<String> drains = metaboliteToDrains.get(metaboliteId);
		System.out.println(metaboliteId + "\t" +drains);
		for(String d : drains){
			ReactionConstraint rc = ec.get(d);
			rc.setLowerLimit(lb);
			rc.setUpperLimit(ub);
		}
	}
	
	public static String getHeader(String sep, List<Evaluators<?>> evaluators ){
		
		String ret = "";
		
			for(Evaluators<?> eval : evaluators){
				ret += eval.getHeaders()+sep; 
		}
		
		return ret;
	}

	public static <T> String applyAvaliators(
			List<Evaluators<T>> contAvaliator, T cont, String sep) {
		
		String info = "";
		for(Evaluators<T> t : contAvaliator){
			
			info+=t.getValues(cont) + sep;
		}
		return info;
	}
	
	public static void writeBioModelsInfo(String folderSBMLs, OutputStream ot) throws IOException{
		Map<String, Document> containers = InputMethods.getDocumentsFromFolder(folderSBMLs);
		
		String set = "\t";
		List<Evaluators<Document>> evalDoc = InputMethods.getDocumentsAvaliators();
		
		List<Evaluators<?>> evaluators = new ArrayList<Evaluators<?>>();
		evaluators.addAll(evalDoc);
		String header = "ModelId\t"+InputMethods.getHeader("\t", evaluators);
		
		System.out.println(header);
		ot.write((header + "\n").getBytes());
		for(String i : containers.keySet()){
//			System.out.println("Start " + i);
			Document cont = containers.get(i);
			
			String values = i+set+InputMethods.applyAvaliators(evalDoc, cont, set);
			
//			System.out.println(i);
			ot.write((values + "\n").getBytes());
//			System.out.println("END " + i);
			ot.flush();
			
		}
		
//		ot.close();
		
	}
	

	public static void writeModelStats(String folderSBMLs, String filePath) throws IOException{
		writeModelStats(folderSBMLs, filePath, true, true);
	}

	public static void writeModelStats(String folderSBMLs, String filePath, boolean evalM, boolean evalS) throws IOException{
		FileWriter file = new FileWriter(filePath);
		
		
		String set = "\t";
		Map<String, Container> containers = InputMethods.getModelFromFolder(folderSBMLs);
		
		
		List<Evaluators<Container>> evalContainer = InputMethods.getContainerAvaliators();
		List<Evaluators<SteadyStateModel>> evalModel = InputMethods.getModelAvaliators();
		List<Evaluators<SteadyStateSimulationResult>> simulationEval = InputMethods.getSimulationAvaliators();
		
		List<Evaluators<?>> evaluators = new ArrayList<Evaluators<?>>();
		evaluators.addAll(evalContainer);
		evaluators.addAll(evalModel);
		evaluators.addAll(simulationEval);
		
		String header = "ModelId\t"+InputMethods.getHeader("\t", evaluators);
		
		System.out.println(header);
		file.write(header + "\n");
		for(String i : containers.keySet()){
			Container cont = containers.get(i);
			
			SteadyStateModel model = null;
			
			
			
			
			String values = i+set+InputMethods.applyAvaliators(evalContainer, cont, set);
			
			Boolean testModel = evalM && (cont.getNotDeclaredMetabolites().size() == 0 /*&& cont.getReactionsWithWrongStoichiometry().size() == 0*/);
			Boolean testSimulation = evalS && (cont.getNotDeclaredMetabolites().size() == 0 /*&& cont.getReactionsWithWrongStoichiometry().size() == 0*/);
			
			if(testModel){
				try {
					model = (SteadyStateModel) ContainerConverter.convert(cont);
					if(model!=null)model.setId(i);
				} catch (InvalidSteadyStateModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				values+= InputMethods.applyAvaliators(evalModel, model, set);
			}
			if(testSimulation){
				SteadyStateSimulationResult result = MethodsWithMemory.wt(model, null);
				values+= InputMethods.applyAvaliators(simulationEval, result, set);
			}
			
			file.write(values + "\n");
			System.out.println(values);
//			System.out.println(i + "\t" + model.getId() + result+ "\t" + SimulateFBAModel.simulations);
		}
		
		file.close();
	}
	
	public static void writeModelStats(Map<String, Container> containers, String filePath, 
			List<Evaluators<Container>> ce, 
			List<Evaluators<SteadyStateModel>> me, 
			List<Evaluators<SteadyStateSimulationResult>> se ) throws IOException{
		
		
		FileWriter file = new FileWriter(filePath);
		
		
		List<Evaluators<Container>> evalContainer = (ce==null)?new ArrayList<Evaluators<Container>>():ce;
		List<Evaluators<SteadyStateModel>> evalModel = (me == null)? new ArrayList<Evaluators<SteadyStateModel>>():me;
		List<Evaluators<SteadyStateSimulationResult>> simulationEval = (se == null)? new ArrayList<Evaluators<SteadyStateSimulationResult>>():se;
		
		String set = "\t";
		List<Evaluators<?>> evaluators = new ArrayList<Evaluators<?>>();
		evaluators.addAll(evalContainer);
		evaluators.addAll(evalModel);
		evaluators.addAll(simulationEval);
		
		String header = "ModelId\t"+InputMethods.getHeader("\t", evaluators);
		
		System.out.println(header);
		file.write(header + "\n");
		for(String i : containers.keySet()){
			Container cont = containers.get(i);
			
			SteadyStateModel model = null;
			
			try {
				model = (SteadyStateModel) ContainerConverter.convert(cont);
				model.setId(i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			
			String values = i+set+InputMethods.applyAvaliators(evalContainer, cont, set) + InputMethods.applyAvaliators(evalModel, model, set) ;
			SteadyStateSimulationResult result = MethodsWithMemory.wt(model, null);
			values+= InputMethods.applyAvaliators(simulationEval, result, set);
			
			file.write(values + "\n");
			System.out.println(values);
		}
		file.flush();
		file.close();
	}
	
	
//	public static void writeModelStatsThreaded(String folderSBMLs, String filePath,
//			int threads) throws IOException{
//		FileWriter file = new FileWriter(filePath);
//		
//		
////		String set = "\t";
//		Map<String, Container> containers = InputMethods.getModelFromFolder(folderSBMLs);
//		List<Evaluators<Container>> evalContainer = InputMethods.getContainerAvaliators();
//		List<Evaluators<SteadyStateModel>> evalModel = InputMethods.getModelAvaliators();
//		List<Evaluators<SteadyStateSimulationResult>> simulationEval = InputMethods.getSimulationAvaliators();
//		
//		
//		List<Evaluators<?>> evaluators = new ArrayList<Evaluators<?>>();
//		evaluators.addAll(evalContainer);
//		evaluators.addAll(evalModel);
//		evaluators.addAll(simulationEval);
//		String header = "ModelId\t"+InputMethods.getHeader("\t", evaluators);
//		
//		MultithreadedQueueManager manager = new MultithreadedQueueManager(threads);
//		
//		
//		
//		for(String i : containers.keySet()){
//			Container cont = containers.get(i);
//			manager.addJob(new RunEvaluatorsJob(i, cont));
//		}
//		
//		manager.run();
//		List<IJob<String>> results = manager.finish();
//		
//		System.out.println(header);
//		file.write(header + "\n");
//		for(IJob<String> r : results){
//			String values = r.getResult();
//			file.write(values + "\n");
//			System.out.println(values);
//		}
//		file.flush();
//		file.close();
//		
//		System.out.println(manager.getWaiting().size());
////		System.out.println(manager.toString());
//		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//		Date date = new Date();
//		
//		InfoMemory.getIntance().saveData("info_"+dateFormat.format(date)+".data");
//		System.out.println(dateFormat.format(date));
//		
//	}
	
	
	public static void writeModelStatsThreaded(String folderSBMLs, String filePath,
			int threads) throws IOException, InterruptedException, ExecutionException{
		FileWriter file = new FileWriter(filePath);
		
		
//		String set = "\t";
		Map<String, Container> containers = InputMethods.getModelFromFolderThreaded(folderSBMLs, threads);
		
		System.out.println("\n\n\nAll Models Loaded!!!!");
		List<Evaluators<Container>> evalContainer = InputMethods.getContainerAvaliators();
		List<Evaluators<SteadyStateModel>> evalModel = InputMethods.getModelAvaliators();
		List<Evaluators<SteadyStateSimulationResult>> simulationEval = InputMethods.getSimulationAvaliators();
		
		
		List<Evaluators<?>> evaluators = new ArrayList<Evaluators<?>>();
		evaluators.addAll(evalContainer);
		evaluators.addAll(evalModel);
		evaluators.addAll(simulationEval);
		String header = "ModelId\t"+InputMethods.getHeader("\t", evaluators);
		
		
		
	
		
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor manager = new ThreadPoolExecutor(threads, 10000, 0L, TimeUnit.MILLISECONDS, workQueue);
		
		List<Future<String>> futures = new ArrayList<Future<String>>();
		for(String i : containers.keySet()){
			Container cont = containers.get(i);
			Future<String> future = manager.submit(new RunEvaluatorsJob(i, cont));
			futures.add(future);
		}
		
		
		System.out.println(header);
		file.write(header + "\n");
		for(Future<String> r : futures){
			String values = r.get();
			file.write(values + "\n");
			System.out.println(values);
		}
		file.flush();
		file.close();
		
//		System.out.println(manager.getWaiting().size());
//		System.out.println(manager.toString());
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		
		InfoMemory.getIntance().saveData("info_"+dateFormat.format(date)+".data");
		System.out.println(dateFormat.format(date));
		manager.shutdown();
		
	}


	


	public static void writeModelStatsThreaded(String folderSBMLs, String filePath) throws IOException, InterruptedException, ExecutionException {
		writeModelStatsThreaded(folderSBMLs, filePath, 10);
		
	}
	
}
