package edu.virginia.cs.Synthesizer.Types;

import java.util.ArrayList;

import edu.virginia.cs.Synthesizer.Types.DBFormalAbstractMeasurementFunction.MeasurementType;

public class DBConcreteTimeMeasurementFunction extends DBConcreteMeasurementFunction  {
	public DBConcreteTimeMeasurementFunction(ConcreteLoad load) { 
		super (MeasurementType.TIME); 
		ArrayList<ConcreteLoad> l = new ArrayList();
		l.add(load);
		super.setLoads(l);
	}
}
