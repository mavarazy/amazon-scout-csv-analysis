package com.clemble.aws.analysis

import scala.collection.mutable

object CSVUtils {

  def order(csv: CSV, desiredOrder: List[String]): CSV = {
    csv.map(line => order(line, desiredOrder))
  }

  def order(line: CSVLine, desiredOrder: List[String]): CSVLine = {
    if (desiredOrder.isEmpty) {
      line
    } else {
      val ordered = new mutable.LinkedHashMap[String, String]
      desiredOrder.foreach(key => ordered.put(key, line.get(key).getOrElse("0")))
      ordered
    }
  }

}
