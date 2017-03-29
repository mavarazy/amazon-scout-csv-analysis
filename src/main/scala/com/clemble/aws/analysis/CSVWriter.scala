package com.clemble.aws.analysis

import java.io.{File, FileWriter}

trait CSVWriter {

  def write(csv: Stream[CSV]): Unit

}

case class FileCSVWriter(file: File) extends CSVWriter {

  override def write(csv: Stream[CSV]): Unit = {
    val fos = new FileWriter(file)
    csv.foreach(csv => {
      val text = csv.map(line => line.mkString(",")).mkString("\n")
      fos.write(text)
    })
    fos.close()
  }

}