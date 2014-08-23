package edu.virginia.cs.Synthesizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

	int solutionNo = 1;

	/**
	 * Call legacy code
	 * 
	 * @param model
	 * @param solutions
	 */
	public void genObjects(String model, String solutions) {
		String logFile = solutions + File.separator + "log.txt";
		if(!new File(logFile).exists()){
			try {
				new File(logFile).createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileOutputStream logFS = null;
		try {
			logFS = new FileOutputStream(new File(logFile), true);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		PrintWriter logPW = new PrintWriter(logFS);
//		String trimmedFilename = model.substring(0, model.length() - 4);
		String trimmedFilename = model.substring(model.lastIndexOf(File.separator)+1, model.length()-4);		
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
//		long start = System.currentTimeMillis();
//		long forOutput = System.currentTimeMillis();
//		long forStop = System.currentTimeMillis();
//		long hundredMinutes = TimeUnit.MINUTES.toMillis(1);

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
				int max = 0;
				boolean isFinished = false;
				while (!isFinished) {
					if (solutionNo > maxSol) {
						break;
					}
					if (solution.satisfiable()) {
						long now = System.currentTimeMillis();
						String outputLog = "Solution #" + solutionNo
								+ " has been generated.";
						System.out.println(outputLog);
						logPW.println(outputLog);
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//						long elapsed = now - start;
//						Date date_elapsed = new Date(elapsed);
//						outputLog = "Within time: " + sdf.format(date_elapsed);
//						System.out.println(outputLog);
						logPW.println(outputLog);
						logPW.flush();

						// System.out.println("Within time: " + format2);
						String xmlFileName = xmlFileNameBase + "_Sol_" + solutionNo
								+ ".xml";

						solution.writeXML(xmlFileName); // This writes out
						// "answer_0.xml",
						// "answer_1.xml"...
						solutionNo++;
					}
				}
			}
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		for(ObjectOfDM object : objSet.getObjSet()){
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
		for(ObjectOfDM object : objSet.getObjSet()){
			AbstractQuery aq = new AbstractQuery();
			aq.setAction(Action.SELECT);
			aq.setOodm(object);
			selectLoads.getQuerySet().add(aq);
		}
		return selectLoads;
	}

//	public ArrayList<FormalAbstractMeasurementFunction> getAbsMeasurementFunction(
//			ArrayList<AbstractLoad> loads) {
//		ArrayList<FormalAbstractMeasurementFunction> funcs = new ArrayList<FormalAbstractMeasurementFunction>();
//		funcs.add(genTimeMeasurement(loads));
//		funcs.add(genSpaceMeasurement(loads));
//		return funcs;
//	}
//	
//	private FormalAbstractMeasurementFunction genTimeMeasurement(ArrayList<AbstractLoad> loads){
//		MeasurementType timeMeasurement = MeasurementType.TIME;
//		ArrayList<AbstractLoad> timeLoads = new ArrayList<AbstractLoad>();
//		for (AbstractLoad load : loads) {
//			timeLoads.add(load);
//		}
//		FormalAbstractMeasurementFunction timeFunc = new FormalAbstractMeasurementFunction(timeMeasurement, timeLoads);
//		return timeFunc;
//	}
//	
//	private FormalAbstractMeasurementFunction genSpaceMeasurement(ArrayList<AbstractLoad> loads){
//		MeasurementType spaceMeasurement = MeasurementType.SPACE;
//		ArrayList<AbstractLoad> spaceLoads = new ArrayList<AbstractLoad>();
//		for (AbstractLoad load : loads) {
//			spaceLoads.add(load);
//		}
//		FormalAbstractMeasurementFunction spaceFunc = new FormalAbstractMeasurementFunction(spaceMeasurement, spaceLoads);
//		return spaceFunc;
//	}
	

	
}
