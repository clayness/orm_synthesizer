package edu.virginia.cs.Synthesizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.mit.csail.sdg.alloy4.Err;

public class Synthesizer {

	private static final Logger logger = Logger.getLogger(Synthesizer.class.getName());

	private static void delete(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				delete(c);
			}
		}
		if (!f.delete()) {
			logger.log(Level.SEVERE, "Failed to delete file: " + f.getAbsolutePath());
		}
	}

	private static void execute(File workspaceFolder, File alloyFile, int limit, int intScope, int range) {
		// get mapping_run file first
		String runFile = FileOperation.getMappingRun(alloyFile.getAbsolutePath());

		try {
			new SmartBridge(workspaceFolder.getAbsolutePath(), runFile, limit);
		} catch (Err err) {
			logger.log(Level.SEVERE, "error solving for mapping file: "+runFile, err);
			System.exit(2);
		}

		// (1) parse the AlloyOM solutions to get the data structure and the
		// tAssociation
		HashMap<String, HashMap<String, ArrayList<CodeNamePair<String>>>> schemas = new HashMap<>();
		HashMap<String, ArrayList<CodeNamePair<String>>> parents = new HashMap<>();
		HashMap<String, ArrayList<CodeNamePair<String>>> reverseTAss = new HashMap<>();
		HashMap<String, ArrayList<CodeNamePair<String>>> foreignKeys = new HashMap<>();
		HashMap<String, HashMap<String, CodeNamePair<String>>> association = new HashMap<>();
		HashMap<String, ArrayList<CodeNamePair<String>>> primaryKeys = new HashMap<>();
		HashMap<String, ArrayList<CodeNamePair<String>>> fields = new HashMap<>();
		HashMap<String, ArrayList<CodeNamePair<String>>> fieldsTable = new HashMap<>();
		HashMap<String, ArrayList<String>> allFields = new HashMap<>();
		HashMap<String, ArrayList<CodeNamePair<String>>> fieldType = new HashMap<>();

		String alloyInstanceModel = alloyFile.getAbsolutePath().replace(".als", "_dm.als");

		AlloyOMToAlloyDM aotad = new AlloyOMToAlloyDM();
		aotad.run(alloyFile.getAbsolutePath(), alloyInstanceModel, intScope);
		ArrayList<String> ids = aotad.getIDs();
		ArrayList<String> associations = aotad.getAss();
		HashMap<String, String> typeList = aotad.getTypeList();
		ArrayList<Sig> sigs = aotad.getSigs();

		for (File singleFile : workspaceFolder.listFiles()) {
			// Do something with child
			if (singleFile.getName().endsWith(".xml")) {
				String fileName = singleFile.getPath();
				String dbSchemaFile = singleFile.getAbsolutePath().replace(".xml", ".sql");
				ORMParser parser = new ORMParser(fileName, dbSchemaFile, sigs);
				parser.createSchemas();

				schemas.put(dbSchemaFile, parser.getDataSchemas());
				parents.put(dbSchemaFile, parser.getParents());
				reverseTAss.put(dbSchemaFile, parser.getReverseTAssociate());
				foreignKeys.put(dbSchemaFile, parser.getForeignKey());
				association.put(dbSchemaFile, parser.getAssociation());
				primaryKeys.put(dbSchemaFile, parser.getPrimaryKeys());
				fields.put(dbSchemaFile, parser.getFields());
				allFields.put(dbSchemaFile, parser.getallFields());
				fieldsTable.put(dbSchemaFile, parser.getFieldsTable());
				fieldType.put(dbSchemaFile, parser.getFieldType());
			}
		}

		// (2) parse the AlloyOM to get Alloy Instance Model
		SolveAlloyDM solver = new SolveAlloyDM(schemas, parents, reverseTAss, foreignKeys, association, primaryKeys,
				fields, allFields, fieldsTable, fieldType, ids, associations, intScope, typeList, sigs);
		for (Map.Entry<String, HashMap<String, ArrayList<CodeNamePair<String>>>> entry : schemas.entrySet()) {
			String dbScheme = entry.getKey();
			System.out.print("NCT of " + dbScheme + ": ");
			System.out.println(solver.getNCTSum(dbScheme));
			System.out.println("-------------------------------");
		}
		for (Map.Entry<String, HashMap<String, ArrayList<CodeNamePair<String>>>> entry : schemas.entrySet()) {
			String dbScheme = entry.getKey();
			System.out.print("TATI of " + dbScheme + ": ");
			System.out.println(solver.getTATISum(dbScheme));
			System.out.println("-------------------------------");
		}

		generateSql(alloyFile, range, solver);
	}

	private static void generateSql(File alloyFile, int range, SolveAlloyDM solver) {
		int newRange = 5000;
		long start = System.currentTimeMillis();
		solver.getOutPutOrders(alloyFile.getAbsolutePath());
		SimpleDateFormat sdf;
		Date date_elapsed;
		for (int i = 0; i < range; i += newRange) {
			int low = i;
			int up = i + newRange;
			if (up > range) {
				up = range;
			}
			solver.randomInstanceGenerator(low + 1, up);
			solver.generateInsert();
			// solver.generateUpdate();
			solver.generateSelect1();
			solver.printAllStatements(1);
			long now = System.currentTimeMillis();
			sdf = new SimpleDateFormat("HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			long elapsed = now - start;
			date_elapsed = new Date(elapsed);
			System.out.println(up + " queries are generated within time: " + sdf.format(date_elapsed));
		}
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			logger.severe("invalid arguments: Synthesizer <workspace> <alloy model> [sol limit] [int scope] [range]");
			System.exit(1);
		}

		String workspace = args[0];
		String alloyPath = args[1];

		int maxSolNoParam = 1000000;
		if (args.length > 2) {
			maxSolNoParam = Integer.parseInt(args[2]);
		}

		int intScope = 6;
		if (args.length > 3) {
			intScope = Integer.parseInt(args[3]);
		}

		int range = 5000;
		if (args.length > 4) {
			range = Integer.parseInt(args[4]);
		}

		String leaf = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss'Z'"));
		File workspaceFolder = Paths.get(workspace, leaf).toFile();
		logger.info("writing output to path: " + workspaceFolder.getAbsolutePath());
		if (workspaceFolder.exists()) {
			logger.warning("output folder exists, clearing all contents: " + workspaceFolder.getAbsolutePath());
			delete(workspaceFolder);
		}
		workspaceFolder.mkdirs();

		File alloyFile = new File(alloyPath);
		logger.info("reading Alloy model from file: " + alloyFile.getAbsolutePath());
		execute(workspaceFolder, alloyFile, maxSolNoParam, intScope, range);
	}
}
