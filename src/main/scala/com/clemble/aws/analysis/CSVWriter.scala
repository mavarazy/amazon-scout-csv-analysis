package com.clemble.aws.analysis

import java.io.{File, FileOutputStream, FileWriter}

import org.apache.poi.ss.usermodel._

import scala.collection.JavaConverters.asScalaIteratorConverter

trait CSVWriter {

  def write(csv: Stream[AWSResults]): Unit

}

case class FileCSVWriter(file: File) extends CSVWriter {

  override def write(resStream: Stream[AWSResults]): Unit = {
    val fos = new FileWriter(file)
    val header = resStream.headOption.flatMap(_.csv.headOption).map(_.keys.mkString(",")).getOrElse("")
    fos.write(header)
    fos.write("\n")
    resStream.foreach(res => {
      val text = res.csv.map(line => line.values.mkString(",")).mkString("\n")
      fos.write(text)
      fos.write("\n")
    })
    fos.close()
  }

}

case class ExcelCSVWriter(file: File) extends CSVWriter {
  private val workbook = WorkbookFactory.create(file)

  private def headerToOrder(header: Iterator[Cell]): Map[String, Int] = {
    header.map(_.getStringCellValue()).zipWithIndex.toMap
  }

  private def writeRow(row: Row, line: Iterable[(Int, String)]) = {
    for {
      (pos, value) <- line
    } {
      value.asBigDecimal match {
        case Some(num) =>
          val cell = row.createCell(pos, CellType.NUMERIC)
          cell.setCellValue(num.toDouble)
        case None =>
          val cell = row.createCell(pos, CellType.STRING)
          cell.setCellValue(value.replaceAll("\"", ""))
      }
    }
  }

  private def writeSheet(sheet: Sheet, csvLines: Stream[CSVLine]): Unit = {
    val relevantCells = headerToOrder(sheet.getRow(0).cellIterator().asScala)
    for {
      (line, id) <- csvLines.zipWithIndex
    } {
      val relLineValues = line.
        flatMap({ case (key, value) => relevantCells.get(key).map(pos => (pos, value)) })
      writeRow(sheet.getRow(id + 1), relLineValues)
    }
  }

  private def saveWorkbook(): Unit = {
    val fos = new FileOutputStream(new File(file.getParent, s"gen-${file.getName}"))
    workbook.write(fos)
    fos.close()
  }

  override def write(resStream: Stream[AWSResults]): Unit = {
    val sheet = workbook.getSheetAt(0)
    val lines = resStream.flatMap(res => Stream(res.csv :_*))
    writeSheet(sheet, lines)
    saveWorkbook()
    workbook.close()
  }

}