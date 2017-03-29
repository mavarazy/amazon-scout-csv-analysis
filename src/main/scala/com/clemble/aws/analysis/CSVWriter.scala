package com.clemble.aws.analysis

import java.io.{File, FileWriter}

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