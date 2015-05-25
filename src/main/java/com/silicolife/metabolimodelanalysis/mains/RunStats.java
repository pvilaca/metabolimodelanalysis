package com.silicolife.metabolimodelanalysis.mains;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.collection.CollectionUtils;

public class RunStats {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
		
		System.out.println(CollectionUtils.join(args, "\t"));
		
		String folder = args[0];
		String file = args[1];
		String threadsS = args[2];
		if(args.length >=5) InfoMemory.getIntance().loadData(args[4]);
		
		int t = Integer.parseInt(threadsS);
		
//		InputMethods.getModelFromFolder(folder, file, false);
		InputMethods.writeModelStatsThreaded(folder, file, t);
//		InputMethods.writeModelStats(folder, file);
		System.out.println("end!!!");
//		return;
	}

}
