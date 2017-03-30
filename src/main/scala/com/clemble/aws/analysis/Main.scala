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
  val source: CSVSource = new FileDirCSVSource(sourceDir, reader)

  val transformer: CSVTransformer = AWSScoutTransformer
  val writer: CSVWriter = {
    val excelFile = new File(sourceDir.getParentFile(), "analysis.xlsx")
    if (excelFile.exists())
      new ExcelCSVWriter(excelFile)
    else
      new FileCSVWriter(new File(sourceDir.getParentFile(), "analysis.csv"))
  }

  val transformedStream = source.readResults().map(transformer.transform)

  writer.write(transformedStream)

}
