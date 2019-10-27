package edu.virginia.cs.Framework

/**
 * Created by tang on 8/9/14.
 */

import scala.io.Source
import java.io.File

class DBSpecification {//extends FrameworkTypeWrapper {

//  override type WrapperType = String
  private var specFile:String = ""

  // store benchmark path to innerValue
//  makeWrapper(specFile)
  def setSpecFile(spec: String) = {
    specFile = spec
  }
  
  def getSpecFile:String = specFile
  
  def getFileContent = {
    val content = Source.fromFile(specFile).getLines().mkString
    content
  }

}
