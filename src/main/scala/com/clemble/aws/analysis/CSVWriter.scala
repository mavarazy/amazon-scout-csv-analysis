package com.clemble.aws.analysis

import java.io.{ByteArrayOutputStream, File, FileOutputStream, FileWriter}
import java.nio.file.Files

import org.apache.poi.ss.usermodel.{Cell, Row, Sheet, WorkbookFactory}

import scala.collection.JavaConverters.asScalaIteratorConverter

trait CSVWriter {

  def write(csv: Stream[CSV]): Unit

}

case class FileCSVWriter(file: File) extends CSVWriter {

  override def write(csv: Stream[CSV]): Unit = {
    val fos = new FileWriter(file)
    val header = csv.headOption.flatMap(_.headOption).map(_.keys.mkString(",")).getOrElse("")
    fos.write(header)
    fos.write("\n")
    csv.foreach(csv => {
      val text = csv.map(line => line.values.mkString(",")).mkString("\n")
      fos.write(text)
      fos.write("\n")
    })
    fos.close()
  }

}

case class ExcelCSVWriter(file: File) extends CSVWriter {
  val workbook = WorkbookFactory.create(file)

  def headerToOrder(header: Iterator[Cell]): Map[String, Int] = {
    header.map(_.getStringCellValue()).zipWithIndex.toMap
  }

  def writeRow(row: Row, line: Iterable[(Int, String)]) = {
    for {
      (pos, value) <- line
    } {
      value.asBigDecimal match {
        case Some(num) =>
          val cell = row.createCell(pos, Cell.CELL_TYPE_NUMERIC)
          cell.setCellValue(num.toDouble)
        case None =>
          val cell = row.createCell(pos, Cell.CELL_TYPE_STRING)
          cell.setCellValue(value.replaceAll("\"", ""))
      }
    }
  }

  def writeSheet(sheet: Sheet, csvLines: Stream[CSVLine]): Unit = {
    val relevantCells = headerToOrder(sheet.getRow(0).cellIterator().asScala)
    for {
      (line, id) <- csvLines.zipWithIndex
    } {
      val relLineValues = line.
        map({ case (key, value) => relevantCells.get(key).map(pos => (pos, value)) }).
        flatten
      writeRow(sheet.getRow(id + 1), relLineValues)
    }
  }

  def saveWorkbook(): Unit = {
    val fos = new FileOutputStream(new File(file.getParent, "gen-" + file.getName()))
    workbook.write(fos)
    fos.close()
  }

  override def write(csvStream: Stream[CSV]): Unit = {
    val sheet = workbook.getSheetAt(0)
    val lines = csvStream.flatMap(csv => Stream(csv :_*))
    writeSheet(sheet, lines)
    saveWorkbook()
    workbook.close()
  }

}