package com.clemble.aws.analysis

import java.io._
import java.util
import java.util.stream.Collectors

import scala.collection.JavaConverters._

trait CSVReader {

  def read(is: InputStream): CSV

}

object SimpleCSVReader extends CSVReader {

  override def read(is: InputStream): CSV = {
    val reader = new BufferedReader(new InputStreamReader(is))
    val csvLines = reader.lines().map[Array[String]]((line) => line.split(","))
    val csv: util.List[Array[String]] = csvLines.collect(Collectors.toList[Array[String]])
    reader.close()
    csv.asScala.map(_.toList).toList
  }

}