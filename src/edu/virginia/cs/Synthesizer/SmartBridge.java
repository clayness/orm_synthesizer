package edu.virginia.cs.Synthesizer;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.virginia.cs.AppConfig;
import kodkod.instance.Instance;
import weka.attributeSelection.PrincipalComponents;
import weka.clusterers.SimpleKMeans;
import weka.core.AbstractInstance;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.xml.internal.txw2.output.StreamSerializer;

public class SmartBridge {
	private Boolean isDebugOn = AppConfig.getDebug();

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
	private Integer overallNIC = 0;

	private int numDimensions;

	private int numFeatures;
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
			
			// Alloy4 sends diagnostic messages and progress reports to the
			// A4Reporter.
			// By default, the A4Reporter ignores all these events (but you can
			// extend the A4Reporter to display the event for the user)
			A4Reporter rep = new A4Reporter() {
				// For example, here we choose to display each "warning" by printing
				// it to System.out
				@Override
				public void warning(ErrorWarning msg) {
					if (isDebugOn) {
						System.out.print("Relevance Warning:\n"
								+ (msg.toString().trim()) + "\n\n");
						System.out.flush();
					}
				}
			};
	
			Module root = null;
			// Parse+typecheck the model
			// System.out.println("=========== Parsing+Typechecking "+filename+" =============");
			if (isDebugOn) {
				System.out.println("Computing Satisfying Solutions ...");
				System.out.println("Current Time: " + now());
			}
			file.println("Computing Satisfying Solutions ...");
			file.println("Current Time: " + now());
			root = CompUtil.parseEverything_fromFile(rep, null, AlloyFile);
	
			// Choose some default options for how you want to execute the commands
			A4Options options = new A4Options();
			options.solver = A4Options.SatSolver.SAT4J; // .KK;//.MiniSatJNI;
														// //.MiniSatProverJNI;//.SAT4J;
	
			options.symmetry = 20;
			options.skolemDepth = 1;
	
			// String trimmedFilename = AlloyFile.replace(".als", "");
	
			trimmedFilename = AlloyFile.replace(".als", "");
			// System.out.println(sensorName);
			StringTokenizer st = new StringTokenizer(trimmedFilename, "\\/");
			String tmp = null;
			while (st.hasMoreTokens()) {
				tmp = st.nextToken();
			}
			String appFileName = tmp;
	
			trimmedFilename = solutionDirectory + "/"
					+ appFileName.substring(0, appFileName.length() - 12);
	
			for (Command command : root.getAllCommands()) {
				// Execute the command
				// System.out.println("============ Command "+command+": ============");
				A4Solution solution = TranslateAlloyToKodkod.execute_command(rep,
						root.getAllReachableSigs(), command, options);
				for (ExprVar a : solution.getAllAtoms()) {
					root.addGlobal(a.label, a);
				}
				for (ExprVar a : solution.getAllSkolems()) {
					root.addGlobal(a.label, a);
				}
	
				int solutionNo = 0;
				while ((solutionNo < maxSol) && solution.satisfiable()) {
					solutionNo++;
					
					if (isDebugOn) {
						System.out.println("-----------------------------------------");
						System.out.println("Solution #" + solutionNo + " has been generated.");
					}
					file.println("-----------------------------------------");
					file.println("Solution #" + solutionNo + " has been generated.");
					if (solutionNo % 1000 == 0)
						writeClassesAndTime(file, solutionNo);

					observations.add(computeFeatureVector(root, solution));
					boolean isNewSolution = measureMetric(root, solution, solutionNo, file);
					if (isNewSolution || storeAllSolutions)
						solution.writeXML(trimmedFilename + "_Sol_" + solutionNo + ".xml");
		
					solution = solution.next();
				}
				if (isDebugOn)
					writeClassesAndTime(System.out, solutionNo);
				writeClassesAndTime(file, solutionNo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeClassesAndTime(PrintStream writer, int solutionNo) {
		writer.println("\n-----------------------------------------");
		writer.println("# Eq.Classes: " + solutionsMV.size() + " / " + solutionNo);
		writer.println("Current Time: " + now());
	}

	private List<Double> computeFeatureVector(Module root, A4Solution solution) {
		Evaluator e = new Evaluator(root, solution);
		List<String> components = e.queryNames("Class + Association");
		numDimensions = components.size();
		components.sort(null);
		List<String> strategies = e.queryNames("Strategy");
		numFeatures   = components.size();
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
	
	private <E> String getVectorString(List<E> vector) {
		StringBuilder vec = new StringBuilder();
		Iterator<E> it = vector.iterator(); 
		while (true) {
			vec.append(it.next().toString());
			if (it.hasNext())
		    	vec.append(",");
		    else 
		    	return vec.toString();
		}
	}

	private boolean measureMetric(Module root, A4Solution solution,
			int solutionNo, PrintStream file) throws Err {
		boolean isNewSolution = false;
		MetricValue solutionMV = new MetricValue(solutionNo);

		Integer overallTATI = 0;
		Integer overallNCT = 0;
		Integer overallNCRF = 0;
		Integer overallANV = 0;
		// Integer overallNIC = 0;

		Evaluator e = new Evaluator(root, solution);

		ArrayList classes = e.query("Class");
		String className = "";

		ArrayList<String> classNames = new ArrayList<String>();
		for (Iterator<String> resultIterator = classes.iterator(); resultIterator.hasNext();) {
			className = getBaseName(resultIterator.next());
			// System.out.println("className: " + className);
			if (className.length() > 0)
				classNames.add(className);
		}

		// Measuring TATI - Table Access for Type Identification
		for (Iterator<String> resultIterator = classNames.iterator(); resultIterator
				.hasNext();) {
			className = resultIterator.next();
			String queryTATI = "#" + className + ".*(~parent).~tAssociate";
			ArrayList queryResults = e.query(queryTATI);
			Integer valueTATI = Integer
					.parseInt(queryResults.get(0).toString());
			// if (value <0) value = 16+value
			valueTATI = valueTATI < 0 ? 16 + valueTATI : valueTATI;
			overallTATI += valueTATI;
			// System.out.println("TATI(" + className+")= " +valueTATI);
			solutionMV.setTATI_detail(solutionMV.getTATI_detail() + "\nTATI("
					+ className + ")= " + valueTATI);
		}
		// System.out.println("Overall_TATI(solution:" + solutionNo+")= "
		// +overallTATI);

		// Measuring NCT - Number of Corresponding Tables
		for (Iterator<String> resultIterator = classNames.iterator(); resultIterator
				.hasNext();) {
			className = resultIterator.next();
			String queryNCT = "#" + className + ".~tAssociate.foreignKey +1";
			ArrayList queryResults = e.query(queryNCT);
			if (queryResults.size() > 0) {
				Integer valueNCT = Integer.parseInt(queryResults.get(0).toString());
				// if (value <0) value = 16+value
				valueNCT = valueNCT < 0 ? 16 + valueNCT : valueNCT;
				overallNCT += valueNCT;
				// System.out.println("NCT(" + className+")= " +valueNCT);
				solutionMV.setNCT_detail(solutionMV.getNCT_detail() + "\nNCT("
						+ className + ")= " + valueNCT);
			}
		}
		// System.out.println("Overall_NCT(solution:" + solutionNo+")= "
		// +overallNCT);

		// Measuring NCRF - Number of Corresponding Relational Fields
		for (Iterator<String> resultIterator = classNames.iterator(); resultIterator
				.hasNext();) {
			className = resultIterator.next();
			String queryNCRF = "#(" + className + ".attrSet-" + className
					+ ".id).~fAssociate.~fields";
			ArrayList queryResults = e.query(queryNCRF);
			Integer valueNCRF = Integer
					.parseInt(queryResults.get(0).toString());
			// if (value <0) value = 16+value
			valueNCRF = valueNCRF < 0 ? 16 + valueNCRF : valueNCRF;
			overallNCRF += valueNCRF;
			// System.out.println("NCRF(" + className+")= " +valueNCRF);
			solutionMV.setNCRF_detail(solutionMV.getNCRF_detail() + "\nNCRF("
					+ className + ")= " + valueNCRF);
		}
		// System.out.println("Overall_NCRF(solution:" + solutionNo+")= "
		// +overallNCRF);

		// Measuring ANV - Additional Null Values
		// ANV(Student) = #Student.attrSet X
		// #(Student.~tAssociate.tAssociate-Student.*(parent))
		for (Iterator<String> resultIterator = classNames.iterator(); resultIterator
				.hasNext();) {
			className = resultIterator.next();
			String queryANV1 = "#" + className + ".attrSet";
			String queryANV2 = "#(" + className + ".~tAssociate.tAssociate-"
					+ className + ".*(parent))";
			ArrayList queryResults1 = e.query(queryANV1);
			ArrayList queryResults2 = e.query(queryANV2);
			Integer value1 = Integer.parseInt(queryResults1.get(0).toString());
			Integer value2 = Integer.parseInt(queryResults2.get(0).toString());
			// if (value <0) value = 16+value
			value1 = value1 < 0 ? 16 + value1 : value1;
			value2 = value2 < 0 ? 16 + value2 : value2;
			// Integer valueANV =
			// Integer.parseInt(queryResults1.get(0).toString()) *
			// Integer.parseInt(queryResults2.get(0).toString());
			Integer valueANV = value1 * value2;
			overallANV += valueANV;
			// System.out.println("ANV(" + className+")= " +valueANV);
			solutionMV.setANV_detail(solutionMV.getANV_detail() + "\nANV("
					+ className + ")= " + valueANV);
		}

		// ArrayList<String> tables = new ArrayList<String>();
		// tables.clear();
		// tables.addAll(e.query("Table"));
		// String tableName = "";
		// overallNIC = 0;

		// // Measuring NIC - Number of Involved Classes
		// for (Iterator<String> resultIterator = tables.iterator();
		// resultIterator.hasNext();) {
		// tableName = resultIterator.next();
		// String queryNIC = "#("+tableName+").fields.fAssociate.~attrSet";
		// ArrayList queryResults = e.query(queryNIC);
		//
		// Integer valueNIC = 0;
		// if(queryResults.size() == 1)
		// valueNIC = Integer.parseInt(queryResults.get(0).toString());
		//
		// overallNIC += valueNIC;
		// // solutionMV.setNIC_detail(solutionMV.getNIC_detail() + "\nNIC(" +
		// tableName + ")= " + valueNIC);
		// }
		// // file.println("overall_NIC(solution:" + solutionNo+")= "
		// +overallNIC);
		// System.out.println("overall_NIC(solution:" + solutionNo+")= "
		// +overallNIC);

		// Measuring NFK - Number of Foreign Keys
		String queryNFK = "#foreignKey";
		ArrayList queryResults = e.query(queryNFK);
		Integer valueNFK = Integer.parseInt(queryResults.get(0).toString());
		// if (value <0) value = 16+value
		valueNFK = valueNFK < 0 ? 16 + valueNFK : valueNFK;
		solutionMV.setNFK(valueNFK);

		solutionMV.setTATI(overallTATI);
		solutionMV.setNCT(overallNCT);
		solutionMV.setNCRF(overallNCRF);
		solutionMV.setANV(overallANV);
		// solutionMV.setNIC(overallNIC);

		file.println("Eq.Class #" + eqClass(solutionMV));
		if (isDebugOn) {
			System.out.println("Eq.Class #" + eqClass(solutionMV));
		}

		if (!contains(solutionMV)) {
			// int eqClassNo = eqClass(solutionMV);
			// if (eqClassNo==0) {
			measureNIC(e, classNames);
			solutionMV.setNIC(overallNIC);

			boolean isParetoOptimal = true;
			// Iterator<MetricValue> resultIterator = solutionsMV.iterator();
			// while (resultIterator.hasNext()) {
			for (Iterator<MetricValue> resultIterator = solutionsMV.iterator(); resultIterator.hasNext();) {
				MetricValue instance = resultIterator.next();
				if (solutionMV.getTATI() >= instance.getTATI()
						&& solutionMV.getNCT() >= instance.getNCT()
						&& solutionMV.getNCRF() >= instance.getNCRF()
						&& solutionMV.getANV() >= instance.getANV()
						&& solutionMV.getNFK() >= instance.getNFK()
						&& solutionMV.getNIC() >= instance.getNIC()) {
					isParetoOptimal = false;
					break;
				}
			}
			if (isParetoOptimal) {
				paretoOptimalSolutions.add(solutionMV);
				// file.println("Solution #" + solutionNo +
				// " is a pareto Optimal Solution.");
				// System.out.println("Solution #" + solutionNo +
				// " is a pareto Optimal Solution.");
			}

			solutionsMV.add(solutionMV);
			isNewSolution = true;
			// solution.writeXML(trimmedFilename + "_Sol_" + solutionNo +
			// ".xml"); // changed to write all solutions before calling this
			// method

			// System.out.println("-----------------------------------------");
			// file.println("Solution #" + solutionNo + " has been generated.");
			// file.println("Current Time: "+now());
			file.println(solutionMV.getTATI_detail());
			file.println("Overall_TATI(solution:" + solutionNo + ")= "
					+ overallTATI);
			file.println(solutionMV.getNCT_detail());
			file.println("Overall_NCT(solution:" + solutionNo + ")= "
					+ overallNCT);
			file.println(solutionMV.getNCRF_detail());
			file.println("Overall_NCRF(solution:" + solutionNo + ")= "
					+ overallNCRF);
			file.println(solutionMV.getANV_detail());
			file.println("Overall_ANV(solution:" + solutionNo + ")= "
					+ overallANV);
			// file.println(solutionMV.getNIC_detail());
			file.println("Overall_NIC(solution:" + solutionNo + ")= "
					+ overallNIC);
			file.println("Overall_NFK(solution:" + solutionNo + ")= "
					+ valueNFK);
			file.println("Eq.Classes: " + solutionsMV.size() + " / "
					+ solutionNo);
			// file.println("# ParetoOptimalSolutions: "
			// +paretoOptimalSolutions.size());
			// file.println("-----------------------------------------\n");
			if (isDebugOn) {
				System.out.println("Current Time: " + now());
			}
			file.println("-----------------------------------------");

			// System.out.println(solutionMV.getTATI_detail());
			// System.out.println("Overall_TATI(solution:" + solutionNo+")= "
			// +overallTATI);
			// System.out.println(solutionMV.getNCT_detail());
			// System.out.println("Overall_NCT(solution:" + solutionNo+")= "
			// +overallNCT);
			// System.out.println(solutionMV.getNCRF_detail());
			// System.out.println("Overall_NCRF(solution:" + solutionNo+")= "
			// +overallNCRF);
			// System.out.println(solutionMV.getANV_detail());
			// System.out.println("Overall_ANV(solution:" + solutionNo+")= "
			// +overallANV);
			// // System.out.println(solutionMV.getNIC_detail());
			// // System.out.println("Overall_NIC(solution:" + solutionNo+")= "
			// +overallNIC);
			// System.out.println("Overall_NFK(solution:" + solutionNo+")= "
			// +valueNFK);
			// System.out.println("overall_NIC(solution:" + solutionNo+")= "
			// +overallNIC);
			// System.out.println("Eq.Classes: " +solutionsMV.size() +" / " +
			// solutionNo);
			// // System.out.println("# ParetoOptimalSolutions: "
			// +paretoOptimalSolutions.size());
			// System.out.println("Current Time: "+now());
			// System.out.println("-----------------------------------------");
		}
		return isNewSolution;
	}

	private String getBaseName(String currentResult) {
		String className;
		StringTokenizer innerST = new StringTokenizer(currentResult, "/");
		String innerTmp = "";
		while (innerST.hasMoreTokens()) {
			innerTmp = innerST.nextToken();
		}
		className = innerTmp.substring(0, innerTmp.indexOf("$"));
		return className;
	}

	private void measureNIC(Evaluator e, ArrayList<String> classNames) {
		String className;// Measuring NIC - Number of Involved Classes
		ArrayList<String> visitedTables = new ArrayList<String>();
		visitedTables.clear();
		overallNIC = 0;

		for (Iterator<String> resultIterator = classNames.iterator(); resultIterator
				.hasNext();) {
			className = resultIterator.next();
			String queryNIC1 = className + ".~tAssociate";
			ArrayList queryResults1 = e.query(queryNIC1);
			if (queryResults1.size() > 0
					&& !visitedTables.contains(queryResults1.get(0).toString())) {
				visitedTables.add(queryResults1.get(0).toString());
				String queryNIC2 = "#(" + className
						+ ".~tAssociate.fields.fAssociate.~attrSet)";
				ArrayList queryResults2 = e.query(queryNIC2);
				Integer value1 = Integer.parseInt(queryResults2.get(0)
						.toString());
				value1 = value1 < 0 ? 16 + value1 : value1;
				overallNIC += value1;
			}
		}
	}

	private static boolean pressKey(Scanner kb, boolean pressed) {
		String entered;
		entered = "";
		entered = kb.next();
		if ((entered.equals("E")) || entered.equals("e")) {
			pressed = true;
		} else {
			if (AppConfig.getDebug()) {
				System.out.printf("Finding next Solution ...\n");
			}
		}
		return pressed;
	}

	private boolean contains(MetricValue solutionMV) {
		boolean contains = false;
		for (MetricValue c : solutionsMV) {
			if (c.equals(solutionMV)) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	private int eqClass(MetricValue solutionMV) {
		int eqClassNo = 0;
		boolean found = false;
		for (MetricValue c : solutionsMV) {
			eqClassNo++;
			if (c.equals(solutionMV)) {
				found = true;
				break;
			}
		}
		if (!found)
			eqClassNo++;
		return eqClassNo;
	}

	public static String now() {
		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss.SSS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	
	public void cluster() {
		if (observations.size() == 0)
			return;
		try {
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
			km.setNumClusters(5);
			km.buildClusterer(converted);
			int[] assignments = km.getAssignments();
			Instances centroids = km.getClusterCentroids();
			double[] sizes = km.getClusterSizes();
			for (int i = 0; i < centroids.size(); ++i) {
				System.out.printf("%s: %f%n", centroids.get(i), sizes[i]);
			}
			// compute all the possible combinations of the features
			//for (AbstractInstance inst : permute(numDimensions, numFeatures)) {
			//	System.out.printf("%s: %f%n", inst, km.clusterInstance(inst));
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<AbstractInstance> permute(int depth, int breadth) {
		List<AbstractInstance> list = new ArrayList<AbstractInstance>();
		permute(depth, breadth, new ArrayList<Double>(), list);
		return list;
	}
	
	private void permute(int depth, int breadth, List<Double> prefix, List<AbstractInstance> list) {
		if (depth == 0) {
			list.add(new DenseInstance(1, prefix.stream().mapToDouble(x->x).toArray()));
		} else {
			for (int i=0; i<breadth; ++i) {
				permute(depth-1, breadth, Stream.concat(prefix.stream(), Stream.of(new Double(i)))
						.collect(Collectors.toList()), list);
			}
		}
	}
}
