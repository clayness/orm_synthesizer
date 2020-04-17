package edu.virginia.cs.Synthesizer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class SqliteDataProvider extends DataProvider {

	public SqliteDataProvider(ArrayList<Sig> sigs) {
		super(sigs);
	}

	@Override
	public boolean writeIntoFile(String filename) throws IOException {
		try (OutputStream os = Files.newOutputStream(Paths.get(filename)); PrintStream pPRINT = new PrintStream(os)) {
			pPRINT.println("-- CREATE DATABASE FOR " + filename + "\n");

			int PKNum = 0;
			int FKNum = 0;
			for (Map.Entry<String, ArrayList<CodeNamePair<String>>> entry : this.tableItems.entrySet()) {
				String tableName = entry.getKey();
				ArrayList<String> primaryKeys = getPrimaryKey(tableName);
				if (primaryKeys.size() == 0) {
					continue;
				}
				PKNum = hasMultipleItem(tableName, "primaryKey");
				FKNum = hasMultipleItem(tableName, "foreignKey");

				pPRINT.println("--");
				pPRINT.println("-- Table structure for table " + tableName);
				pPRINT.println("--" + "\n");

				pPRINT.println("CREATE TABLE `" + tableName + "` (");
				ArrayList<CodeNamePair<String>> tableItems = entry.getValue();
				int arraySize = tableItems.size();
				boolean firstPK = true;

				String primaryKeyStr = new String("PRIMARY KEY (`");
				String foreignKeyStr = "";
				int lastFKCounter = 0;
				for (int i = 0; i < arraySize; i++) {
					String itemName = tableItems.get(i).getSecond().toString();
					String itemType = getTypesByName(itemName);
					if (itemType == null)
						continue;
					if (itemType.equalsIgnoreCase("Integer")) {
						itemType = "INTEGER";
					} else if (itemType.equalsIgnoreCase("Real")) {
						itemType = "REAL";
					} else if (itemType.equalsIgnoreCase("string")) {
						itemType = "TEXT";
					} else if (itemType.equalsIgnoreCase("class")) {
						itemType = "BLOB";
					} else if (itemType.equalsIgnoreCase("DType")) {
						itemType = "TEXT";
					} else if (itemType.equalsIgnoreCase("Bool")) {
						itemType = "INTEGER";
					} else if (itemType.equalsIgnoreCase("Longblob")) {
						itemType = "BLOB";
					} else if (itemType.equalsIgnoreCase("Time")) {
						itemType = "INTEGER";
					}

					String caseName = tableItems.get(i).getFirst().toString();
					if (caseName.equalsIgnoreCase("fields")) {
						String postfix = isID(itemName, tableName) ? " NOT NULL, \n" : ",\n";
						pPRINT.print("`" + itemName + "` " + itemType + postfix);
					} else if (caseName.equalsIgnoreCase("primaryKey")) {
						if (PKNum > 1) {
							if (firstPK) {
								primaryKeyStr = primaryKeyStr + itemName + "`";
								firstPK = false;
							} else {
								primaryKeyStr = primaryKeyStr + ",`" + itemName + "`)";
							}
						} else {
							primaryKeyStr = "PRIMARY KEY (`" + itemName + "`)";
						}
					} else if (caseName.equalsIgnoreCase("foreignKey")) {
						String pkTable = tableNameByID(itemName);
						if (1 == FKNum) {
							foreignKeyStr = foreignKeyStr + "FOREIGN KEY (`" + itemName + "`) REFERENCES `" + pkTable
									+ "` (`" + itemName + "`) " + "ON DELETE CASCADE ON UPDATE CASCADE\n";
						} else if (FKNum > 1) {
							lastFKCounter++;
							if (lastFKCounter < FKNum) {
								foreignKeyStr = foreignKeyStr + "FOREIGN KEY (`" + itemName + "`) REFERENCES `"
										+ pkTable + "` (`" + itemName + "`) "
										+ "ON DELETE CASCADE ON UPDATE CASCADE,\n";
							} else if (lastFKCounter >= FKNum) {
								foreignKeyStr = foreignKeyStr + "FOREIGN KEY (`" + itemName + "`) REFERENCES `"
										+ pkTable + "` (`" + itemName + "`) " + "ON DELETE CASCADE ON UPDATE CASCADE\n";
							}
						}
					}
				}
				pPRINT.println(primaryKeyStr);
				pPRINT.println(foreignKeyStr);
				pPRINT.println(");" + "\n");
			}
		}
		return true;
	}

}
