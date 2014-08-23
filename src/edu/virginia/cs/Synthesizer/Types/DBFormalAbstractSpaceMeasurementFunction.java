package edu.virginia.cs.Synthesizer.Types;

import java.util.ArrayList;

public class DBFormalAbstractSpaceMeasurementFunction extends DBFormalAbstractMeasurementFunction {
	public DBFormalAbstractSpaceMeasurementFunction(AbstractLoad load) { 
		super (MeasurementType.SPACE); 
		ArrayList<AbstractLoad> l = new ArrayList();
		l.add(load);
		super.setLoads(l);
	}
}