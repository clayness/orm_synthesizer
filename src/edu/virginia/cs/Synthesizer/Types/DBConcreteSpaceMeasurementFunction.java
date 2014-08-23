package edu.virginia.cs.Synthesizer.Types;

import java.util.ArrayList;

import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction.MeasurementType;

public class DBConcreteSpaceMeasurementFunction extends DBConcreteMeasurementFunction {
	public DBConcreteSpaceMeasurementFunction(ConcreteLoad load) { 
		super (MeasurementType.SPACE); 
		ArrayList<ConcreteLoad> l = new ArrayList();
		l.add(load);
		super.setLoads(l);
	}
}
