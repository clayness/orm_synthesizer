package edu.virginia.cs.Synthesizer.Types;

import java.util.ArrayList;

import edu.virginia.cs.Framework.DBImplementation;
import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction.MeasurementType;

public class DBConcreteSpaceMeasurementFunction extends DBConcreteMeasurementFunction {
	public DBConcreteSpaceMeasurementFunction(ArrayList<ConcreteLoad> loads) { 
		super (MeasurementType.SPACE); 
		super.setLoads(loads);
	}
	
	public DBSpaceMeasurementResult run(){
		// run insert only, and check the space consumption
//		dropDB();
//		createDB();
//		createTables();
//		runInsert();
		double spaceConsumption = checkSpace();
		DBSpaceMeasurementResult dbSMR = new DBSpaceMeasurementResult(spaceConsumption);
		return dbSMR;
	}
}
