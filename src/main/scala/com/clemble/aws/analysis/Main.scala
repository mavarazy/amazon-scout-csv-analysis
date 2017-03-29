package com.clemble.aws.analysis

import java.io.File

object Main extends App {

  val reader: CSVReader = SimpleCSVReader

  val sourceDir = new File(classOf[FileDirCSVSource].getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
  val source: CSVSource = new FileDirCSVSource(sourceDir, reader)

  val transformer: CSVTransformer = AWSScoutTransformer
  val writer: CSVWriter = new FileCSVWriter(new File(sourceDir, "analysis.csv"))

  val transformedStream = source.readCSV().map(transformer.transform)

  writer.write(transformedStream)

}
