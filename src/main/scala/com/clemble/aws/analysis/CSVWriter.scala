package com.clemble.aws.analysis

import java.io.{File, FileWriter}

import org.apache.poi.ss.usermodel.{Cell, Sheet, WorkbookFactory}

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

  def writeSheet(sheet: Sheet, csvLines: Stream[CSVLine]): Unit = {
    val relevantCells = headerToOrder(sheet.getRow(sheet.getFirstRowNum()).cellIterator().asScala)
    for {
      (line, id) <- csvLines.zipWithIndex
    } {
      val row = sheet.getRow(id + 2)
      val relLineValues = line.
        map({ case (key, value) => relevantCells.get(key).map(pos => (pos, value)) }).
        flatten
      for {
        (pos, value) <- relLineValues
      } {
        val cell = row.createCell(pos)
        cell.setCellValue(value)
      }
    }
  }

  override def write(csvStream: Stream[CSV]): Unit = {
    val sheet = workbook.getSheetAt(1)
    val lines = csvStream.flatMap(csv => Stream(csv :_*))
    writeSheet(sheet, lines)
    workbook.close()
  }

}