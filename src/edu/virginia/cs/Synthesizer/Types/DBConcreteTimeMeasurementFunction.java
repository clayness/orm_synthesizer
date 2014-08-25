package edu.virginia.cs.Synthesizer.Types;

import java.util.ArrayList;

import edu.virginia.cs.Framework.DBImplementation;
import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction.MeasurementType;

public class DBConcreteTimeMeasurementFunction extends DBConcreteMeasurementFunction  {
	public DBConcreteTimeMeasurementFunction(ArrayList<ConcreteLoad> loads) { 
		super (MeasurementType.TIME); 
		super.setLoads(loads);
	}
	
	public DBTimeMeasurementResult run(){
		dropDB();
		createDB();
		createTables();
		double insertTime = runInsert();
		double selTime = runSelect();
		
		DBTimeMeasurementResult dbTMR = new DBTimeMeasurementResult(insertTime, selTime);
		return dbTMR;
	}
}
