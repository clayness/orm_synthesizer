package edu.virginia.cs.Synthesizer.Types;

public class DBSpaceMeasurementResult {
	private double dbSpace = -1.0;

	public DBSpaceMeasurementResult(double sc){
		this.dbSpace = sc;
	}
	
	public double getDbSpace() {
		return dbSpace;
	}

	public void setDbSpace(double dbSpace) {
		this.dbSpace = dbSpace;
	}
}
