package com.clemble.aws.analysis

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.specs2.mutable.Specification

import scala.io.Source

@RunWith(classOf[JUnitRunner])
class SimpleTests extends Specification {

  val csvReader = SimpleCSVReader

  def source = Source.fromResource("Search Term of autospout gizmo water bottles at 3-23-2017.csv")

  "reader" should {
    val csv = csvReader.read(source)

    "read as expected" in {
      csv.length shouldEqual 15
      csv.head.size shouldEqual 21
      csv.find(_.size != 21) shouldEqual None
    }
  }

  val transformer = AWSScoutTransformer

  "transformer" should {
    val csv = csvReader.read(source)

    "return 2 lines" in {
      val transformed = transformer.transform(csv)
      transformed.size shouldEqual 2
      transformed(0) shouldNotEqual transformed(1)
    }

  }

}
