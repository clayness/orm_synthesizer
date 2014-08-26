package edu.virginia.cs

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object AppConfig {
  private final var params: Config = ConfigFactory.load()

  private final var debug: Boolean = params.getBoolean("dev.debug");
  private final var mysqlUser: String = params.getString("mysql.user");
  private final var mysqlPassword: String = params.getString("mysql.password");
  private final var specificationPath: String = params.getString("app.specificationPath");
  private final var implsPath: String = params.getString("app.implsPath");
  private final var testCasesPath: String = params.getString("app.testCasesPath");
  private final var intScopeForImpl: Integer = params.getInt("alloy.intScopeForImpl");
  private final var intScopeForTestCases: Integer = params.getInt("alloy.intScopeForTestCases");
  private final var maxSolForImpl: Integer = params.getInt("alloy.maxSolForImpl");
  private final var maxSolForTest: Integer = params.getInt("alloy.maxSolForTest");
  private final var A4ReportSymmetry: Integer = params.getInt("alloy.A4Report.symmetry");
  private final var A4ReportSkolemDepth: Integer = params.getInt("alloy.A4Report.skolemDepth");

  def getDebug(): Boolean = {
    this.debug 
  }
  
  def getMySQLUser(): String = {
    this.mysqlUser
  }

  def getMysqlPassword(): String = {
    this.mysqlPassword
  }

  def getSpecificationPath(): String = {
    this.specificationPath
  }

  def getImplsPath(): String = {
    this.implsPath
  }

  def getTestCasesPath(): String = {
    this.testCasesPath
  }

  def getIntScopeForImpl(): Integer = {
    this.intScopeForImpl
  }

  def getIntScopeForTestCases(): Integer = {
    this.intScopeForTestCases
  }

  def getMaxSolForImpl(): Integer = {
    this.maxSolForImpl
  }

  def getMaxSolForTest(): Integer = {
    this.maxSolForTest
  }

  def getA4ReportSymmetry(): Integer = {
    this.A4ReportSymmetry
  }
  
  def getA4ReportSkolemDepth(): Integer = {
    this.A4ReportSkolemDepth
  }
}