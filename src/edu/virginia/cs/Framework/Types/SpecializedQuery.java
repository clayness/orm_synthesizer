package edu.virginia.cs.Framework.Types;

import java.util.ArrayList;
import java.util.HashMap;

public class SpecializedQuery {
	private HashMap<String, HashMap<Integer, String>> insertStmtsInOneObject = new HashMap<String, HashMap<Integer, String>>();
	private HashMap<String, ArrayList<String>> selectStmtsInOneObject = new HashMap<String, ArrayList<String>>();
	
	public HashMap<String, HashMap<Integer, String>> getInsertStmtsInOneObject() {
		return insertStmtsInOneObject;
	}

	public void setInsertStmtsInOneObject(
			HashMap<String, HashMap<Integer, String>> insertStmtsInOneObject) {
		this.insertStmtsInOneObject = insertStmtsInOneObject;
	}

	public HashMap<String, ArrayList<String>> getSelectStmtsInOneObject() {
		return selectStmtsInOneObject;
	}

	public void setSelectStmtsInOneObject(
			HashMap<String, ArrayList<String>> selectStmtsInOneObject) {
		this.selectStmtsInOneObject = selectStmtsInOneObject;
	}
}
