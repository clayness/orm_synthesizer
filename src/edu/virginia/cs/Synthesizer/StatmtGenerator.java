package edu.virginia.cs.Synthesizer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ct4ew
 * Date: 7/18/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatmtGenerator {
    static HashMap<String, String> insert_statement = new HashMap<String, String>();
    static HashMap<String, String> select_statement = new HashMap<String, String>();

    static void init(){
        insert_statement.clear();
        select_statement.clear();

    }

    static void insertToFile(String filePath) {
        int last_dash = filePath.lastIndexOf("_Sol_");
        String sqlPath = filePath.substring(0, last_dash) + "_insert.sql";
        List<String> sortedKeys = new ArrayList(insert_statement.keySet());
        Collections.sort(sortedKeys);
        insert_statement.clear();

        try {
            FileOutputStream fos = new FileOutputStream(new File(sqlPath), false);
            PrintStream ps = new PrintStream(fos);

            for (String s : sortedKeys) {
                ps.println(s);
            }
            ps.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        insert_statement.clear();
    }

    static void selectToFile(String filePath){
        int last_dash = filePath.lastIndexOf("_Sol_");
        String selectPath = filePath.substring(0, last_dash) + "_select.sql";
        List<String> sortedKeys = new ArrayList<String>(select_statement.keySet());
        Collections.sort(sortedKeys);
        select_statement.clear();

        try {
            FileOutputStream fos = new FileOutputStream(new File(selectPath), false);
            PrintStream ps = new PrintStream(fos);

            for (String s : sortedKeys) {
                ps.println(s);
            }
            ps.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
