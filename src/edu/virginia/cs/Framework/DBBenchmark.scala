package edu.virginia.cs.Framework

/**
 * Created by tang on 8/9/14.
 */

// In SQL instance, benchmarks will be a list of SQL Scripts which contains insert&select statements
class DBBenchmark {
	private var benchmark: Tuple2[String, String] = null
	
	def setBenchmark(insert:String, select:String) = {
	  benchmark = (insert, select)
	}
	
	def getFirst() = {
	  benchmark._1 
	}
	
	def getSecond() = {
	  benchmark._2 
	}
	
	def getBenchmark() = {
	  benchmark
	}
}
