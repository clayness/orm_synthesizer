package edu.virginia.cs.Synthesizer.Types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.virginia.cs.Framework.DBImplementation;
import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction.MeasurementType;

public class DBConcreteMeasurementFunction {
	private MeasurementType mType = null;
	private ArrayList<ConcreteLoad> loads;
	private DBImplementation impl;

	// Chong: task
	// make username and password configuration parameters
	String mysqlUser = "root";
	String mysqlPassword = "root";
	private String mysqlCMD = "mysql -u" + mysqlUser + " -p" + mysqlPassword;

	public DBConcreteMeasurementFunction(MeasurementType m) {
		this.setmType(m);
	}

	public DBImplementation getImpl() {
		return impl;
	}

	public void setImpl(DBImplementation impl) {
		this.impl = impl;
	}

	public MeasurementType getmType() {
		return mType;
	}

	public void setmType(MeasurementType mType) {
		this.mType = mType;
	}

	public ArrayList<ConcreteLoad> getLoads() {
		return loads;
	}

	public void setLoads(ArrayList<ConcreteLoad> loads) {
		this.loads = loads;
	}

	protected double checkSpace() {
		String implPath = this.impl.getImPath();
		String dbName = implPath.substring(
				implPath.lastIndexOf(File.separator) + 1,
				implPath.lastIndexOf("."));
		String[] command = new String[] {
				"bash",
				"-c",
				this.mysqlCMD
						+ " -Bse\"select table_schema, sum((data_length+index_length)/1024) AS KB from information_schema.tables group by 1;\"" };
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] splited = line.split("\\s+");
				if (splited.length == 2) {
					if (splited[0].equalsIgnoreCase(dbName)) { // find right
																// data base
						return Double.valueOf(splited[1]);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1.0;
	}

	protected void dropDB() {
		/**
		 * get the name from implPath create drop database script :
		 * "drop database implName;" create cmd to drop the database execute the
		 * cmd
		 */
		String implPath = this.impl.getImPath();
		String dbName = implPath.substring(
				implPath.lastIndexOf(File.separator) + 1,
				implPath.lastIndexOf("."));
		String dropDatabase = "drop database " + dbName + ";";
		String scriptFileName = implPath.substring(0,
				implPath.lastIndexOf(File.separator))
				+ "dropDatabase.sql";
		try {
			PrintWriter pw = new PrintWriter(new File(scriptFileName));
			String outToFile = this.mysqlCMD + " -Bse " + "\"" + dropDatabase
					+ "\"";
			pw.println(outToFile);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Process p = Runtime.getRuntime().exec("bash " + scriptFileName);
			p.waitFor();
			if(p.exitValue() != 0){
				System.out.println("Drop DB Failure...");
				System.out.println("exit value = " + p.exitValue());
			}
			// delete the script
			new File(scriptFileName).delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void createDB() {
		/**
		 * get the name from implPath create create database script :
		 * "create database implName;" create cmd to create the database execute
		 * the cmd
		 */
		String implPath = this.impl.getImPath();
		String dbName = implPath.substring(
				implPath.lastIndexOf(File.separator) + 1,
				implPath.lastIndexOf("."));
		String createDatabase = "create database " + dbName + ";";
		String scriptFileName = implPath.substring(0,
				implPath.lastIndexOf(File.separator))
				+ "createDatabase.sql";
		try {
			PrintWriter pw = new PrintWriter(new File(scriptFileName));
			String outToFile = this.mysqlCMD + " -Bse " + "\"" + createDatabase
					+ "\"";
			pw.println(outToFile);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Process p = Runtime.getRuntime().exec("bash " + scriptFileName);
			p.waitFor();
			if(p.exitValue() != 0){
				System.out.println("Created DB Failure...");
				System.out.println("exit value = " + p.exitValue());
			}
			// delete the script
			new File(scriptFileName).delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void createTables() {
		/**
		 * get the name from implPath create create tables script call bash to
		 * execute that script
		 */
		String implPath = this.impl.getImPath();
		// String dbName =
		// implPath.substring(implPath.lastIndexOf(File.separator)+1,
		// implPath.lastIndexOf("."));
		String scriptFileName = implPath.substring(0,
				implPath.lastIndexOf(File.separator))
				+ "createTables.sql";

		try {
			PrintWriter pw = new PrintWriter(new File(scriptFileName));
			String outToFile = this.mysqlCMD + " < " + implPath;
			pw.println(outToFile);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Process p = Runtime.getRuntime().exec("bash " + scriptFileName);
			p.waitFor();
			if(p.exitValue() != 0){
				System.out.println("Create schema Failure...");
				System.out.println("exit value = " + p.exitValue());
			}
			// delete the script
			new File(scriptFileName).delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected double runInsert() {
		// iterate all concrete load
		// record start time
		// run scripts
		// record end time
		// return end-start
		long insertInterval = -1;
		for (ConcreteLoad cl : this.loads) {
			String insertPath = cl.getInsertPath();
			if (insertPath.length() == 0) {
				continue;
			}
			String imPath = this.impl.getImPath();
			String dbName = imPath.substring(
					imPath.lastIndexOf(File.separator) + 1,
					imPath.lastIndexOf("."));
			String[] cmd = new String[] { "mysql", dbName, "-uroot", "-proot",
					"-e", "source " + insertPath };
			long startTime = System.currentTimeMillis();
			Process p;
			try {
				System.out.println("Insert start----" +dbName);
				p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				if(p.exitValue() != 0){
					System.err.println("exit value = " + p.exitValue());
				}
				System.out.println("Insert finished----" + dbName);
			} catch (IOException ex) {
				Logger.getLogger(DBConcreteMeasurementFunction.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (InterruptedException ex) {
				Logger.getLogger(DBConcreteMeasurementFunction.class.getName())
						.log(Level.SEVERE, null, ex);
			}
			long endTime = System.currentTimeMillis();
			insertInterval = endTime - startTime;
		}
		return insertInterval;
	}

	protected double runSelect() {
		long selectInterval = -1;
		for (ConcreteLoad cl : this.loads) {
			String selectPath = cl.getSelectPath();
			if (selectPath.length() == 0) {
				continue;
			}
			String imPath = this.impl.getImPath();
			String dbName = imPath.substring(
					imPath.lastIndexOf(File.separator) + 1,
					imPath.lastIndexOf("."));

			String[] cmd = new String[] { "mysql", dbName, "-uroot", "-proot",
					"-e", "source " + selectPath};
			long startTime = System.currentTimeMillis();
			Process p;
			try {
				System.out.println("Select start----" + dbName);
				p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				if(p.exitValue() != 0){
					System.err.println("exit value = " + p.exitValue());
				}
				System.out.println("Select finished----" + dbName);
			} catch (IOException ex) {
				Logger.getLogger(DBConcreteMeasurementFunction.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (InterruptedException ex) {
				Logger.getLogger(DBConcreteMeasurementFunction.class.getName())
						.log(Level.SEVERE, null, ex);
			}
			long endTime = System.currentTimeMillis();
			selectInterval = endTime - startTime;
		}
		return selectInterval;
	}
}
