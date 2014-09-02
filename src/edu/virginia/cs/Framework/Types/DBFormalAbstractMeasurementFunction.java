package edu.virginia.cs.Framework.Types;

import java.util.ArrayList;


public class DBFormalAbstractMeasurementFunction {
	public 	enum MeasurementType {
		TIME, SPACE
	}
	
	private MeasurementType mType = null;
	private ArrayList<AbstractLoad> loads; 
	
	public DBFormalAbstractMeasurementFunction(MeasurementType m){
		this.setmType(m);
	}
	

	public MeasurementType getmType() {
		return mType;
	}


	public void setmType(MeasurementType mType) {
		this.mType = mType;
	}
	
	public ArrayList<AbstractLoad> getLoads() {
		return loads;
	}
	
	public void setLoads(ArrayList<AbstractLoad> loads) {
		this.loads = loads;
	}
}



