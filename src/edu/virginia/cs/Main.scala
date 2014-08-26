package edu.virginia.cs

import edu.virginia.cs.Framework._
import edu.virginia.cs.Framework.TrademakerFramework
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
    var evaluatedResults = myDBTrademaker.tradespaceFunction(mySpec)
    if (isDebugOn) {
      println("Done")
    }
  }
}