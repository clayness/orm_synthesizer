package edu.virginia.cs.Synthesizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.parser.Module;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.virginia.cs.Framework.DBImplementation;
import edu.virginia.cs.Synthesizer.Types.AbstractLoad;
import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction;
import edu.virginia.cs.Synthesizer.Types.AbstractQuery;
import edu.virginia.cs.Synthesizer.Types.DBFormalConcreteMeasurementFunction;
import edu.virginia.cs.Synthesizer.Types.ObjectOfDM;
import edu.virginia.cs.Synthesizer.Types.ObjectSet;
import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction.MeasurementType;
import edu.virginia.cs.Synthesizer.Types.AbstractQuery.Action;

public class LoadSynthesizer {

	public int solutionNo = 1;
	public HashMap<String, String> globalNegation = new HashMap<String, String>();
	public ArrayList<String> ids = new ArrayList<String>();
	public HashMap<String, HashMap<String, ArrayList<CodeNamePair<String>>>> allInstances = new HashMap<String, HashMap<String, ArrayList<CodeNamePair<String>>>>();
	boolean isFinished = false;
	
	public void genObjsHelper(String model, String solutions, ArrayList<String> ids) {
		// call solve()
		// parse one solution
		// add negation to the end of DM
		// go to step (1)
		System.out.println("Generate objects starts....");
		this.ids = ids;
		String negation = "";
		String factName = "";
		int factNum = 1;
		while (true) {
			try {
				String object = genObjects(model, solutions);
				if(isFinished){
					break;
				}
				// parse the document
				ObjectOfDM oodm = new ObjectOfDM(object);
				allInstances = oodm.parseDocument();
				// add negation to data model
				getNegation(object);
				PrintStream ps = new PrintStream(new FileOutputStream(new File(
						model), true));
				factName = "fact_" + factNum;
				factNum++;
				negation = System.getProperty("line.separator") + "fact "
						+ factName + " {"
						+ System.getProperty("line.separator");
				for (Entry<String, String> s_negation : this.globalNegation
						.entrySet()) {
					negation += s_negation.getKey()
							+ System.getProperty("line.separator");
				}
				negation += "}";
				ps.print(negation);
				ps.flush();
				ps.close();
				this.globalNegation.clear();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Generate objects ends....");
	}

	/**
	 * Call legacy code
	 * 
	 * @param model
	 * @param solutions
	 */
	public String genObjects(String model, String solutions) {
		String logFile = solutions + File.separator + "log.txt";
		if (!new File(logFile).exists()) {
			try {
				new File(logFile).createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		FileOutputStream logFS = null;
//		try {
//			logFS = new FileOutputStream(new File(logFile), true);
//		} catch (FileNotFoundException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		PrintWriter logPW = new PrintWriter(logFS);
		// String trimmedFilename = model.substring(0, model.length() - 4);
		String trimmedFilename = model.substring(
				model.lastIndexOf(File.separator) + 1, model.length() - 4);
		String xmlFileNameBase = solutions + File.separator + trimmedFilename;
		Module root = null; // (14:45:08)
		int maxSol = 1000;
		A4Reporter rep = new A4Reporter() {
			// For example, here we choose to display each "warning" by printing
			// it to System.out
			@Override
			public void warning(ErrorWarning msg) {
				System.out.print("Relevance Warning:\n"
						+ (msg.toString().trim()) + "\n\n");
				System.out.flush();
			}
		};
		try {
			root = CompUtil.parseEverything_fromFile(rep, null, model);
		} catch (Err e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Choose some default options for how you want to execute the commands
		A4Options options = new A4Options();
		options.solver = A4Options.SatSolver.SAT4J; // .KK;//.MiniSatJNI;
													// //.MiniSatProverJNI;//.SAT4J;
		options.symmetry = 20;
		options.skolemDepth = 1;

		ConstList<Command> cmds = root.getAllCommands();
		try {
			for (Command command : cmds) {
				// Execute the command
				A4Solution solution = TranslateAlloyToKodkod.execute_command(
						rep, root.getAllReachableSigs(), command, options);
				for (ExprVar a : solution.getAllAtoms()) {
					root.addGlobal(a.label, a);
				}
				for (ExprVar a : solution.getAllSkolems()) {
					root.addGlobal(a.label, a);
				}

				// solutionNo = 1;
				while (!isFinished) {
					if (solutionNo > maxSol) {
						break;
					}
					if (solution.satisfiable()) {
//						String outputLog = "Solution #" + solutionNo
//								+ " has been generated.";
//						System.out.println(outputLog);
						String xmlFileName = xmlFileNameBase + "_Sol_"
								+ solutionNo + ".xml";
						solution.writeXML(xmlFileName); // This writes out
						solutionNo++;
						return xmlFileName;
					} else {
						isFinished = true;
					}
				}
			}
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<AbstractLoad> genAbsLoads(ObjectSet objSet) {
		ArrayList<AbstractLoad> loads = new ArrayList<AbstractLoad>();
		loads.add(genInsertLoad(objSet));
		loads.add(genSelectLoad(objSet));
		return loads;
	}

	private AbstractLoad genInsertLoad(ObjectSet objSet) {
		AbstractLoad insertLoads = new AbstractLoad();
		// iterate objects
		for (ObjectOfDM object : objSet.getObjSet()) {
			AbstractQuery aq = new AbstractQuery();
			aq.setAction(Action.INSERT);
			aq.setOodm(object);
			insertLoads.getQuerySet().add(aq);
		}
		return insertLoads;
	}

	private AbstractLoad genSelectLoad(ObjectSet objSet) {
		AbstractLoad selectLoads = new AbstractLoad();
		// iterate objects
		for (ObjectOfDM object : objSet.getObjSet()) {
			AbstractQuery aq = new AbstractQuery();
			aq.setAction(Action.SELECT);
			aq.setOodm(object);
			selectLoads.getQuerySet().add(aq);
		}
		return selectLoads;
	}
	
	public String getNegation(String xmlFile) {
//        String pattern = Pattern.quote(System.getProperty("file.separator"));
//        String[] paths = xmlFile.split(pattern);
//        String fileName = paths[paths.length - 1];
//        String factName = fileName.substring(0, fileName.length() - 4);
        String negation = "";
        String forGlobalNegation = "";
//        negation += System.getProperty("line.separator") + "fact " + factName + " {" + System.getProperty("line.separator");
        for (Map.Entry<String, HashMap<String, ArrayList<CodeNamePair<String>>>> entry : this.allInstances.entrySet()) {
            String element = entry.getKey();
            for (Map.Entry<String, ArrayList<CodeNamePair<String>>> instance : entry.getValue().entrySet()) {
                forGlobalNegation = "";
                forGlobalNegation = "no o:" + element + " | ";
                negation += "no o:" + element + " | ";
//                String instanceName = instance.getKey();
                ArrayList<CodeNamePair<String>> allFields = instance.getValue();
                for (CodeNamePair<String> fields : allFields) {
                    String field = fields.getFirst();
//                    field = field.split("_")[1];
                    // check if field is ID or not
                    if (isID(field.split("_")[1])) {
                        String value = fields.getSecond();
//                        int intValue = Integer.valueOf(value).intValue();
//                        intValue = intValue + (int) (Math.pow(2, intScope - 1) + 1);
                        negation += "o." + field + "=" + value + " && ";
                        forGlobalNegation += "o." + field + "=" + value + " && ";
                    }
                }
                forGlobalNegation = forGlobalNegation.substring(0, forGlobalNegation.length() - 4);// + System.getProperty("line.separator");
                globalNegation.put(forGlobalNegation, "");
                negation = negation.substring(0, negation.length() - 4) + System.getProperty("line.separator");
            }
            //String goToTable = getTableNameByElement()
        }
//        negation += "}";

        return negation;
	}
	
    public boolean isID(String field) {
        for (String s : this.ids) {
            if (s.equals(field))
                return true;
        }
        return false;
    }

}
