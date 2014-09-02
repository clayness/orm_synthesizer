package edu.virginia.cs.Framework.Types;

import java.util.ArrayList;
import edu.virginia.cs.Framework.Types.DBFormalAbstractMeasurementFunction.MeasurementType;

public class DBFormalConcreteMeasurementFunction {
	private MeasurementType mType = null;
	private ArrayList<ConcreteLoad> loads;
	
	public DBFormalConcreteMeasurementFunction(MeasurementType m){
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
