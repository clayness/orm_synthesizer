package edu.virginia.cs.Synthesizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import edu.virginia.cs.Synthesizer.SmartBridge.Bounds;

/**
 * Created by IntelliJ IDEA. User: ct4ew Date: 7/23/13 Time: 3:20 PM To change
 * this template use File | Settings | File Templates.
 */
public class Synthesizer {

	public static void main(String[] args) {
		assert (args.length > 1) : "Usage: java ... <solution-folder> <object-model-file> [max-solutions]";
		// suppose we already get the solutions of AlloyOM
		// then we need to parse the solutions to get the data structure and the
		// tAssociate
		// after get the information of tAssociation, we can parse the AlloyOM
		// to get the Alloy Instance Model
		// the execute the Alloy Instance Model
		// after each solution of AIM comes out, we parse it to different data
		// schema, and then go to the next one

		// Chong: we need to generate the solution first
		String workspace = args[0];
		String alloyFile = args[1];
		int maxSolNoParam = 1000000;
		if (args.length > 2) {
			maxSolNoParam = Integer.parseInt(args[2]);
		}

		// delete all files and folders in Solution Folder
		File f = new File(workspace);
		delete(f);
		if (!f.exists()) {
			f.mkdir();
		}

		File f1 = new File(alloyFile);
		String om = f1.getAbsolutePath();

		try {
			// get mapping_run file first
			String runFile = FileOperation.getMappingRun(om);
			// generate the solution for the sample
			SmartBridge sb = new SmartBridge(f.getAbsolutePath(), runFile, maxSolNoParam);
			// gather the observations
			Classifier classifier = new Classifier();
			for (SmartBridge.SolutionInfo si : sb.solutionList)
				classifier.observations.add(classifier.computeFeatureVector(si));
			// cluster the sample instances
			int[] clusters = classifier.cluster();
			// partition the clusters into bounds
			List<Map<String, SmartBridge.Bounds>> partitions = classifier.partition(clusters, sb.solutionList);
			printBounds(partitions);
		} catch (Exception err) {
			err.printStackTrace();
		}

		// (1) parse the AlloyOM solutions to get the data structure and the
		// tAssociation

		// (2) parse the AlloyOM to get Alloy Instance Model
	}

	public static void delete(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				delete(c);
			}
		}
		if (!f.delete()) {
			try {
				throw new FileNotFoundException("Failed to delete file: " + f);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Synthesizer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private static void printBounds(List<Map<String, Bounds>> bounds) {
		Gson gson = new Gson();
		for (int i = 0; i < bounds.size(); ++i) {
			List<String[]> lowers = new ArrayList<>();
			List<String[]> uppers = new ArrayList<>();
			Map<String, Bounds> entry = bounds.get(i);
			for (String strategy : entry.keySet()) {
				Bounds b = entry.get(strategy);
				for (String l : b.lower)
					lowers.add(new String[] { strategy, l });
				for (String u : b.upper)
					uppers.add(new String[] { strategy, u });
			}
			BoundInfo bi = new BoundInfo(lowers.size(), uppers.size());
			lowers.toArray(bi.lower);
			uppers.toArray(bi.upper);
			System.out.println(gson.toJson(bi));
		}
	}

	private static class BoundInfo {
		final String[][] lower;
		final String[][] upper;

		BoundInfo(int l, int u) {
			lower = new String[l][];
			upper = new String[u][];
		}
	}
}
