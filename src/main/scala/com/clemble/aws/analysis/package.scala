package com.clemble.aws

package object analysis {

  case class AWSResults(query: String, csv: CSV)

  type CSVLine = scala.collection.Map[String, String]
  type CSV = List[CSVLine]

}
