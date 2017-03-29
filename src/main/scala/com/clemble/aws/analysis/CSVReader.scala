package com.clemble.aws.analysis

import java.io._
import java.util.stream.Collectors

import scala.collection.JavaConverters._

trait CSVReader {

  def read(is: InputStream): CSV

}

object SimpleCSVReader extends CSVReader {

  private def toCSV(lines: List[List[String]]): CSV = {
    lines match {
      case head :: csv => csv.map(head.zip(_).toMap)
      case Nil => Nil
    }
  }

  override def read(is: InputStream): CSV = {
    val reader = new BufferedReader(new InputStreamReader(is))
    val csv = reader.lines().
      map[List[String]]((line) => line.split(",", 1).toList).
      collect(Collectors.toList[List[String]])
    val lines = csv.asScala.toList
    reader.close()
    toCSV(lines)
  }

}