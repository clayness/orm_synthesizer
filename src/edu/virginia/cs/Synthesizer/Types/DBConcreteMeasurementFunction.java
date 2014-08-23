package edu.virginia.cs.Synthesizer.Types;

import java.util.ArrayList;

import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction.MeasurementType;

public class DBConcreteMeasurementFunction {
	private MeasurementType mType = null;
	private ArrayList<ConcreteLoad> loads;
	
	public DBConcreteMeasurementFunction(MeasurementType m){
		this.setmType(m);
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
}
