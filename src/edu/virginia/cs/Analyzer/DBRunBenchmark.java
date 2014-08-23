package edu.virginia.cs.Analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.virginia.cs.Framework.DBBenchmark;

public class DBRunBenchmark {

	private double selectTime = -1.0;
	private double insertTime = -1.0;
	private String implPath;
	private DBBenchmark bm;
	
	// Chong: task
	// make username and password configuration parameters
	String mysqlUser = "root";
	String mysqlPassword = "root";
	private String mysqlCMD = "mysql -u" + mysqlUser + " -p" + mysqlPassword;
	
	public DBRunBenchmark(String implPath, DBBenchmark bm) {
		this.implPath = implPath;
		this.bm = bm;
		run();
	}
	
	private void run() {
		dropDB();
		createDB();
		createTables();
		runInsert();
		runSelect();
	}
	
	private void dropDB(){
		/**
		 * get the name from implPath
		 * create drop database script : "drop database implName;"
		 * create cmd to drop the database
		 * execute the cmd
		 */
		
		String dbName = implPath.substring(implPath.lastIndexOf(File.separator)+1, implPath.lastIndexOf("."));
		String dropDatabase = "drop database " + dbName + ";";
		String scriptFileName = implPath.substring(0, implPath.lastIndexOf(File.separator)) + "dropDatabase.sql";
		try {
			PrintWriter pw = new PrintWriter(new File(scriptFileName));
			String outToFile = this.mysqlCMD + " -Bse " + "\"" + dropDatabase +"\"";
			pw.println(outToFile);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Runtime.getRuntime().exec("bash " + scriptFileName);
			// delete the script
			new File(scriptFileName).delete();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	private void createDB(){
		/**
		 * get the name from implPath
		 * create create database script : "create database implName;"
		 * create cmd to create the database
		 * execute the cmd
		 */
		
		String dbName = implPath.substring(implPath.lastIndexOf(File.separator)+1, implPath.lastIndexOf("."));
		String createDatabase = "create database " + dbName + ";";
		String scriptFileName = implPath.substring(0, implPath.lastIndexOf(File.separator)) + "createDatabase.sql";
		try {
			PrintWriter pw = new PrintWriter(new File(scriptFileName));
			String outToFile = this.mysqlCMD + " -Bse " + "\"" + createDatabase +"\"";
			pw.println(outToFile);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			Runtime.getRuntime().exec("bash " + scriptFileName);
			// delete the script
			new File(scriptFileName).delete();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	private void createTables(){
		/**
		 * get the name from implPath
		 * create create tables script
		 * call bash to execute that script
		 */
		
		String dbName = implPath.substring(implPath.lastIndexOf(File.separator)+1, implPath.lastIndexOf("."));
		String scriptFileName = implPath.substring(0, implPath.lastIndexOf(File.separator)) + "createTables.sql";
		
		try {
			PrintWriter pw = new PrintWriter(new File(scriptFileName));
			String outToFile = this.mysqlCMD + " < " + implPath;
			pw.println(outToFile);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			Runtime.getRuntime().exec("bash " + scriptFileName);
			// delete the script
			new File(scriptFileName).delete();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	private void runInsert(){
	}
	
	private void runSelect(){
	}
	
	public double insertTime() 
	{
		return this.insertTime;
	}
	
	public double selectTime() 
	{	
		return this.selectTime;
	}
	
	
}
