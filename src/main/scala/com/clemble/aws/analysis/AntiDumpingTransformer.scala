package com.clemble.aws.analysis

import java.io.File

import org.apache.poi.ss.usermodel.{WorkbookFactory}

case class AntiDumpingTransformer(db: AntiDumpingDatabase) extends CSVTransformer {

  override def transform(res: AWSResults): AWSResults = {
    val isRegulated = db.isRegulated(res.query)
    val csvWithRegulations = res.csv.map(line => line + ("regulated" -> isRegulated.toString))
    res.copy(csv = csvWithRegulations)
  }

}

trait AntiDumpingDatabase {

  def isRegulated(name: String): Boolean

}

private case class ListAntiDumpingDatabase(names: Set[String]) extends AntiDumpingDatabase {

  override def isRegulated(name: String): Boolean = {
    names.contains(name)
  }

}

object AntiDumpingDatabase {

  private def readNames(file: File): Seq[String] = {
    val workbook = WorkbookFactory.create(file)
    val sheet = workbook.getSheetAt(0)
    val products = for {
      i <- 1 to sheet.getLastRowNum
    } yield {
      val row = sheet.getRow(i)
      Option(row.getCell(6)).map(_.getStringCellValue())
    }
    products.flatten
  }

  def fromExcel(files: Seq[File]): AntiDumpingDatabase = {
    val allProducts = files.flatMap(readNames).toSet
    ListAntiDumpingDatabase(allProducts)
  }

  def fromExcel(file: File): AntiDumpingDatabase = {
    val products = readNames(file)
    ListAntiDumpingDatabase(products.toSet)
  }

}