package com.clemble.aws.analysis

import java.io.{File, FileInputStream}

import com.sun.net.httpserver.Authenticator.Success

import scala.util.{Failure, Try}

trait CSVSource {

  def readCSV(): Stream[CSV]

}

case class FileDirCSVSource(sourceDir: File, reader: CSVReader) extends CSVSource {

  private def toCSV(source: File): List[File] = {
    val csv = Option(source.listFiles(file => file.getName.endsWith(".csv"))).map(_.toList).getOrElse(List.empty)
    val dirWithCSV = Option(source.listFiles((file) => file.isDirectory)).map(_.toList).getOrElse(List.empty).flatMap(toCSV)
    csv ++ dirWithCSV
  }

  override def readCSV(): Stream[CSV] = {
    val csvFiles = toCSV(sourceDir)
    println(s"Found ${csvFiles.length} files")
    val csvFilesStream = Stream(csvFiles:_*)
    csvFilesStream.map(file => {
      println(s"Processing ${file}")
      new FileInputStream(file)
    }).map(reader.read)
  }

}
