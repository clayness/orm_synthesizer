package edu.virginia.cs

import edu.virginia.cs.Framework.Types.DBSpecification
import com.typesafe.config.ConfigFactory

/**
 * @author tang
 */

// This is the Main class, the entry point of the DB instance of Trademaker Framework
object Main {
  var isDebugOn = AppConfig.getDebug
  def main(args: Array[String]) {

    var mySpec: DBSpecification = new DBSpecification(AppConfig.getSpecificationPath)
    //    mySpec.setSpecFile("/Users/tang/Desktop/ORM/Parser/customerOrderObjectModel.als")

    var myDBTrademaker = new DBTrademaker()
    // get solutions and test results
//    var evaluatedResults = myDBTrademaker.tradespaceFunction(mySpec)
    myDBTrademaker.run()
    // write the result to files and print it out
    // iterate list of results
    // get head of the list 
    // get the tail of the list
    
//    var tmpPair = Pair[ImplementationType, MeasurementFunctionSetType]()
    
    if (isDebugOn) {
      println("Done")
    }
  }
}