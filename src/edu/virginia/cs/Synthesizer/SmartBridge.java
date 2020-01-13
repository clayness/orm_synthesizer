package edu.virginia.cs.Synthesizer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import weka.attributeSelection.PrincipalComponents;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class SmartBridge {
	private static final int NUM_CLUSTERS = 5;
	
	List<SolutionInfo> solutionList = new ArrayList<SolutionInfo>();

	// static String applicationName;
	// static String appType = "";
	// static String archStyle = "";
	static String workspace = ".";// UI.workspace;
	String trimmedFilename = "";

	// The list of quality equivalence classes on the Pareto optimal frontier.
	ArrayList<MetricValue> solutionsMV = new ArrayList<MetricValue>();
	// The list of Solutions on the Pareto optimal frontier.
	ArrayList<MetricValue> paretoOptimalSolutions = new ArrayList<MetricValue>();
	List<List<Double>> observations = new ArrayList<List<Double>>();
	static boolean storeAllSolutions = true;

	/*
	 * args[0]: solution folder args[1]: alloy OM args[2]: max solution number
	 * parameter args[3] (optional): store all solution? default value: false
	 */
	public static void main(String args[]) {

		workspace = args[0];
		String alloyFile = args[1];
		int maxSolNoParam = 1000000; // Integer.parseInt(args[2]);
		if (args.length > 2) {
			maxSolNoParam = Integer.parseInt(args[2]);
		}
		if ((args.length > 3) && (args[3].equalsIgnoreCase("all"))) {
			storeAllSolutions = true;
		}
		// if(args.length > 2)
		// workspace = args[2];
		// int maxSolNoParam = 1;
		String solutionDirectory = workspace; // + "\\Solutions";
		// String mergedFile = workspace + "\\"+ appType + "\\" + appDesc + "_"
		// + archStyle + ".als";

		try {
			new SmartBridge(solutionDirectory, alloyFile, maxSolNoParam);
		} catch (Err err) {
			err.printStackTrace();
		}
	}

	// public SmartBridge(String solutionDirectory, String filename, String
	// appDescp, String appTypep, String archStylep, int maxSol) throws Err {

	public SmartBridge(String solutionDirectory, String AlloyFile, int maxSol)
			throws Err {

		try (FileOutputStream output = new FileOutputStream(solutionDirectory + "/metricsValue.txt");
				PrintStream file = new PrintStream(output)) {
			
			Module root = CompUtil.parseEverything_fromFile(null, null, AlloyFile);
	
			A4Options options = new A4Options();
			options.solver = A4Options.SatSolver.SAT4J;
			options.symmetry = 20;
			options.skolemDepth = 1;
	
			trimmedFilename = AlloyFile.replace(".als", "");
			StringTokenizer st = new StringTokenizer(trimmedFilename, "\\/");
			String tmp = null;
			while (st.hasMoreTokens()) {
				tmp = st.nextToken();
			}
			String appFileName = tmp;
	
			trimmedFilename = solutionDirectory + "/"
					+ appFileName.substring(0, appFileName.length() - 12);
	
			for (Command command : root.getAllCommands()) {
				long beg = System.currentTimeMillis();
				System.out.print("synthesizing: " + command + " ... ");
				A4Solution solution = TranslateAlloyToKodkod.execute_command(null,
						root.getAllReachableSigs(), command, options);
				int solutionNo = 0;
				while ((solutionNo < maxSol) && solution.satisfiable()) {
					solutionNo++;
					
					solutionList.add(new SolutionInfo(root, solution));
					observations.add(computeFeatureVector(root, solution));
					//boolean isNewSolution = measureMetric(root, solution, solutionNo, file);
					//if (isNewSolution || storeAllSolutions)
					//	solution.writeXML(trimmedFilename + "_Sol_" + solutionNo + ".xml");
					solution = solution.next();
				}
				System.out.printf("done (%d sols, %10.2f sec)%n", solutionNo, (System.currentTimeMillis() - beg)/1000.0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Double> computeFeatureVector(Module root, A4Solution solution) {
		Evaluator e = new Evaluator(root, solution);
		List<String> components = e.queryNames("Class + Association");
		components.size();
		components.sort(null);
		List<String> strategies = e.queryNames("Strategy");
		components.size();
		strategies.sort(null);
		List<Double> features = new ArrayList<Double>(components.size());
		for (String c : components) {
			List<String> strategy = e.queryNames("assignees." + c);
			if (strategy.size() > 0) {
				assert strategy.size() == 1;
				features.add(strategies.indexOf(strategy.get(0))+1.0d);				
			} else {
				features.add(0.0d);
			}
		}
		return features;
	}
	
	public static String now() {
		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss.SSS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	
	public int[] cluster() throws Exception {
		if (observations.size() == 0)
			return new int[0];
		
		System.out.print("clustering ... ");
		long beg = System.currentTimeMillis();
		// set up the dataset based on the extant observation vectors
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i = 0; i < observations.get(0).size(); i++)
			attributes.add(new Attribute("a"+i, i));
		Instances instances = new Instances("Dataset", attributes, observations.size());
		for (List<Double> f : observations) {
			instances.add(new DenseInstance(1, f.stream().mapToDouble(x->x).toArray()));
		}				
		
		PrincipalComponents pc = new PrincipalComponents();
		pc.buildEvaluator(instances);
		Instances converted = pc.transformedData(instances);
		
		// generate the clusters and get the centroids
		SimpleKMeans km = new SimpleKMeans();
		km.setPreserveInstancesOrder(true);
		km.setNumClusters(NUM_CLUSTERS);
		km.buildClusterer(converted);
		System.out.printf("done (%10.2f secs)%n", (System.currentTimeMillis() - beg)/1000.0);
		
		double[] sizes = km.getClusterSizes();
		System.out.println("["+String.join(",", Arrays.stream(sizes).boxed().map(x -> x.toString()).collect(Collectors.toList())) + "]");
	
		return km.getAssignments();
	}
	
	public List<Map<String, Bounds>> partition(int[] clusters) {	
		List<Map<String, Bounds>> bounds = new ArrayList<>(NUM_CLUSTERS);
		for (int i = 0; i<NUM_CLUSTERS; ++i) bounds.add(new HashMap<>());
		
		for (int i=0; i < clusters.length; i++) {
			SolutionInfo sol = solutionList.get(i);
			for (String strategy : sol.assignees.keySet()) {
				Map<String, Bounds> partition = bounds.get(clusters[i]);
				if (!partition.containsKey(strategy))
					partition.put(strategy, new Bounds(sol.assignees.get(strategy)));
				else {
					Bounds b = partition.get(strategy);
					b.lower.retainAll(sol.assignees.get(strategy));
					b.upper.addAll(sol.assignees.get(strategy));
				}
			}
		}
		return bounds;	
	}
	
	private class SolutionInfo {
		final Map<String, List<String>> assignees = new HashMap<String, List<String>>();
		
		SolutionInfo(Module root, A4Solution solution) {
			Evaluator e = new Evaluator(root, solution);
			for (String strategy : e.queryNames("Strategy")) {
				assignees.put(strategy, e.queryNames(strategy + ".assignees"));
			}	
		}
	}
	
	public class Bounds {
		final Set<String> lower;
		final Set<String> upper;
		
		Bounds(List<String> instances) {
			this.lower = new HashSet<String>(instances);
			this.upper = new HashSet<String>(instances);
		}
	}
}
