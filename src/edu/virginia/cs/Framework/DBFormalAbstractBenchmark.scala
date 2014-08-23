package edu.virginia.cs.Framework

/**
 * Created by tang on 8/8/14.
 */

import scala.io.Source
import edu.virginia.cs.Synthesizer.Types.AbstractQuery
import java.util.ArrayList
// XML Files
class DBFormalAbstractBenchmark {
	private var querySet:ArrayList[AbstractQuery] = null;

	def AbstractLoad() {
		this.querySet = new ArrayList[AbstractQuery]();
	}

	def AbstractLoad(querySet:ArrayList[AbstractQuery] ) {
		this.querySet = querySet;
	}

	def getQuerySet():ArrayList[AbstractQuery] = {
		this.querySet
	}

	def setQuerySet(querySet:ArrayList[AbstractQuery] ) {
		this.querySet = querySet
	}
}
