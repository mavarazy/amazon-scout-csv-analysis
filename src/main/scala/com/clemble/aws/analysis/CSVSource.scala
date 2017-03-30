package com.clemble.aws.analysis

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.Date

import scala.io.Source

trait CSVSource {

  def readResults(): Stream[AWSResults]

}

case class FileDirCSVSource(sourceDir: File, reader: CSVReader) extends CSVSource {

  private def toQuery(source: File): String = {
    source.getName()
  }

  private def listQueries(source: File): List[File] = {
    val csv = Option(source.listFiles(file => file.getName.endsWith(".csv"))).map(_.toList).getOrElse(List.empty)
    val dirWithCSV = Option(source.listFiles((file) => file.isDirectory)).map(_.toList).getOrElse(List.empty).flatMap(listQueries)
    csv ++ dirWithCSV
  }

  override def readResults(): Stream[AWSResults] = {
    val queryFiles = listQueries(sourceDir)
    println(s"Found ${queryFiles.length} files")
    val csvFilesStream = Stream(queryFiles:_*)
    for {
      file <- csvFilesStream
    } yield{
      val csv = reader.read(Source.fromFile(file))
      val q = toQuery(file)
      val creationTime = Files.readAttributes(file.toPath, classOf[BasicFileAttributes]).creationTime()
      println(s"Created ${creationTime}")
      AWSResults(q, csv, new Date(creationTime.toMillis))
    }
  }

}
