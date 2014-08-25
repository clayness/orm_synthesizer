package edu.virginia.cs.Synthesizer.Types;

import edu.virginia.cs.Framework.DBImplementation;

public class DBMeasurementResult {
	public DBTimeMeasurementResult tmr;
	public DBSpaceMeasurementResult smr;
	public DBImplementation impl;
	
	public DBMeasurementResult(DBTimeMeasurementResult tmr, DBSpaceMeasurementResult smr){
		this.tmr = tmr;
		this.smr = smr;
	}
	
	public DBTimeMeasurementResult getTmr() {
		return tmr;
	}
	
	public void setTmr(DBTimeMeasurementResult tmr) {
		this.tmr = tmr;
	}
	
	public DBSpaceMeasurementResult getSmr() {
		return smr;
	}
	
	public void setSmr(DBSpaceMeasurementResult smr) {
		this.smr = smr;
	}
	
	public DBImplementation getImpl() {
		return impl;
	}
	
	public void setImpl(DBImplementation impl) {
		this.impl = impl;
	}
}
