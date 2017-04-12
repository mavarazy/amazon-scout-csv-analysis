package com.clemble.aws.analysis

import java.io.File

object Main extends App with Loggable {

  val reader: CSVReader = SimpleCSVReader

  val sourceDir = {
    val currentDir = new File(classOf[FileDirCSVSource].getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    if (!currentDir.isDirectory)
      currentDir.getParentFile
    else
      currentDir
  }
  val source: CSVSource = new FileDirCSVSource(sourceDir, reader)

  val transformer: CSVTransformer = {
    val antiDumpingFiles = sourceDir.getParentFile.listFiles((file) => file.getName.contains("GAD") && file.getName.endsWith(".xls")).toSeq
    if (antiDumpingFiles.isEmpty) {
      val antiDumpDB = AntiDumpingDatabase.fromExcel(antiDumpingFiles)
      AWSScoutTransformer andThen AntiDumpingTransformer(antiDumpDB)
    } else {
      LOG.warn("No antidumping data exists, falling back to just AWSScoutTransformer")
      AWSScoutTransformer
    }
  }
  val writer: CSVWriter = {
    val excelFile = new File(sourceDir.getParentFile(), "analysis.xlsx")
    if (excelFile.exists()) {
      new ExcelCSVWriter(excelFile)
    } else {
      LOG.warn("No analysis.xsls found falling back to CSV output")
      new FileCSVWriter(new File(sourceDir.getParentFile(), "analysis.csv"))
    }
  }

  val transformedStream = source.readResults().map(transformer.transform)

  writer.write(transformedStream)

}
