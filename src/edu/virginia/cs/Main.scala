package edu.virginia.cs

import edu.virginia.cs.Framework._
import edu.virginia.cs.Framework.TrademakerFramework

/**
 * @author tang
 */

// This is the Main class, the entry point of the DB instance of Trademaker Framework
object Main {
//  type SpecificationType >: edu.virginia.cs.Framework.SpecificationType
//  type ImplementationType >: edu.virginia.cs.Framework.ImplementationType
//  type BenchmarkType >: DBBenchmark
  def main(args: Array[String]) {
    var mySpec: DBSpecification = new DBSpecification()
//    mySpec.setSpecFile("/Users/tang/Desktop/ORM/Parser/customerOrderObjectModel.als")
    mySpec.setSpecFile("/home/tang/Desktop/ORM/Parser/customerOrderObjectModel.als")
    //  
    //  var myDBTradespace = new DBTradespace()
    //  myDBTradespace.mySpec = ""
    //  var evaluatedResults = myDBTradespace.tradespaceFunction(myDBTradespace.mySpec)
    //  
    //  var myDBParetoFront = new DBParetoFront()
    //  var paretoSols = myDBParetoFront.getParetoResults(myDBTradespace.myTradespace, mySpec.getSpecFile)
    //  
    ////  var paretoFront = myParetoFront.paretoFront(myParetoFront)

    var myDBTrademaker = new DBTrademaker()
    // get solutions and test results
    var evaluatedResults = myDBTrademaker.tradespaceFunction(mySpec)
//    var evaluatedResults = myDBTrademaker.test(mySpec)
    // get pareto solutions with test results 
//    var paretoSols = myDBTrademaker.getParetoResults(mySpec)
    println("Done")
  }

}