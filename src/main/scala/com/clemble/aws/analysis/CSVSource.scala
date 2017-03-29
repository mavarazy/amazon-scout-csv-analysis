package com.clemble.aws.analysis

import java.io.{File, FileInputStream}

trait CSVSource {

  def readCSV(): Stream[CSV]

}

case class FileDirCSVSource(sourceDir: File, reader: CSVReader) extends CSVSource {

  private def toCSV(source: File): List[File] = {
    val csv = source.listFiles(file => file.getName.endsWith(".csv")).toList
    val dirWithCSV = source.listFiles((file) => file.isDirectory).toList.flatMap(toCSV)
    csv ++ dirWithCSV
  }

  override def readCSV(): Stream[CSV] = {
    val csvFiles = toCSV(sourceDir)
    val csvFilesStream = Stream(csvFiles:_*)
    csvFilesStream.map(new FileInputStream(_))map(reader.read)
  }

}
