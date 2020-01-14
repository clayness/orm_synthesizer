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

	List<SolutionInfo> solutionList = new ArrayList<SolutionInfo>();

	/*
	 * args[0]: solution folder args[1]: alloy OM args[2]: max solution number
	 * parameter args[3] (optional): store all solution? default value: false
	 */
	public static void main(String args[]) {
		assert (args.length > 1) : "Usage: java ... <solution-folder> <object-model-file> [max-solutions]";
		String workspace = args[0];
		String alloyFile = args[1];

		int maxSolNoParam = 1000000;
		if (args.length > 2) {
			maxSolNoParam = Integer.parseInt(args[2]);
		}
		try {
			new SmartBridge(workspace, alloyFile, maxSolNoParam);
		} catch (Err err) {
			err.printStackTrace();
		}
	}

	// public SmartBridge(String solutionDirectory, String filename, String
	// appDescp, String appTypep, String archStylep, int maxSol) throws Err {

	public SmartBridge(String solutionDirectory, String alloyFile, int maxSol) throws Err {
		try (FileOutputStream output = new FileOutputStream(solutionDirectory + "/metricsValue.txt");
				PrintStream file = new PrintStream(output)) {
			long beg = 0;
			String msg = "";

			msg = "\rparsing: %s ... ";
			beg = System.currentTimeMillis();
			System.out.printf(msg, alloyFile);
			Module root = CompUtil.parseEverything_fromFile(null, null, alloyFile);
			System.out.printf(msg + "done (%10.2f sec)%n", alloyFile, (System.currentTimeMillis() - beg) / 1000.0);

			A4Options options = new A4Options();
			options.solver = A4Options.SatSolver.SAT4J;
			options.symmetry = 20;
			options.skolemDepth = 1;

			for (Command command : root.getAllCommands()) {
				beg = System.currentTimeMillis();
				msg = "\rsynthesizing: %s ... %6d";
				int solutionNo = 0;
				System.out.printf(msg, command, solutionNo);
				A4Solution solution = TranslateAlloyToKodkod.execute_command(null, root.getAllReachableSigs(), command,
						options);
				while ((solutionNo < maxSol) && solution.satisfiable()) {
					solutionNo++;
					System.out.printf(msg, command, solutionNo);
					solutionList.add(new SolutionInfo(root, solution));
					solution = solution.next();
				}
				System.out.printf(msg + "done (%10.2f sec)%n", command, solutionNo,
						(System.currentTimeMillis() - beg) / 1000.0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String now() {
		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss.SSS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	class SolutionInfo {
		final Map<String, List<String>> assignees = new HashMap<String, List<String>>();
		final Module root;
		final A4Solution solution;

		SolutionInfo(Module root, A4Solution solution) {
			this.root = root;
			this.solution = solution;
			Evaluator e = new Evaluator(root, solution);
			for (String strategy : e.queryNames("Strategy")) {
				assignees.put(strategy, e.queryNames(strategy + ".assignees"));
			}
		}
	}

	public static class Bounds {
		final Set<String> lower;
		final Set<String> upper;

		Bounds(List<String> instances) {
			this.lower = new HashSet<String>(instances);
			this.upper = new HashSet<String>(instances);
		}
	}
}
