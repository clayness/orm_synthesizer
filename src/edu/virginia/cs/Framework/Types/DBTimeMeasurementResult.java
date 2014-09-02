package edu.virginia.cs.Framework.Types;

public class DBTimeMeasurementResult {

	private double insertTime = -1.0;
	private double selectTime = -1.0;
	
	public DBTimeMeasurementResult(double iTime, double sTime){
		this.insertTime = iTime;
		this.selectTime = sTime;
	}
	
	public double getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(double insertTime) {
		this.insertTime = insertTime;
	}
	public double getSelectTime() {
		return selectTime;
	}
	public void setSelectTime(double selectTime) {
		this.selectTime = selectTime;
	}
}
