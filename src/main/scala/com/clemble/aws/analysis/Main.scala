package com.clemble.aws.analysis

import java.io.File

object Main extends App {

  val reader: CSVReader = SimpleCSVReader

  val sourceDir = {
    val currentDir = new File(classOf[FileDirCSVSource].getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    if (!currentDir.isDirectory)
      currentDir.getParentFile
    else
      currentDir
  }
  val source: CSVSource = new FileDirCSVSource(sourceDir.getParentFile(), reader)

  val transformer: CSVTransformer = AWSScoutTransformer
  val writer: CSVWriter = new FileCSVWriter(new File(sourceDir, "analysis.csv"))

  val transformedStream = source.readCSV().map(transformer.transform)

  writer.write(transformedStream)

}
