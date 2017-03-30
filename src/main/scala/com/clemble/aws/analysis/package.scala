package com.clemble.aws

import scala.util.Try

package object analysis {

  case class AWSResults(query: String, csv: CSV)

  type CSVLine = scala.collection.Map[String, String]
  type CSV = List[CSVLine]

  implicit class StringToBigDecimal(str: String) {
    def asBigDecimal = Try(BigDecimal(str)).toOption
  }

  implicit class BigDecimalMath(numbers: Seq[BigDecimal]) {
    def median(): BigDecimal = {
      val (lower, upper) = numbers.sorted.splitAt(numbers.size / 2)
      if (numbers.size % 2 == 0)
        (lower.last + upper.head) / 2
      else
        upper.head
    }
  }


}
