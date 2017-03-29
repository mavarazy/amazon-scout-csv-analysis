package com.clemble.aws.analysis

import scala.util.{Failure, Success, Try}

trait CSVTransformer {

  def transform(csv: CSV): CSV

}

object AWSScoutTransformer extends CSVTransformer {

  private def median(numbers: Seq[BigDecimal]): BigDecimal = {
    val (lower, upper) = numbers.sorted.splitAt(numbers.size / 2)
    if (numbers.size % 2 == 0)
      (lower.last + upper.head) / 2
    else
      upper.head
  }

  private def analyze(csv: CSV): CSVLine = {
    for {
      (key, value) <- csv.head
    } yield {
      Try(BigDecimal(value)) match {
        case Success(_) =>
          println(s"$key with $value is a number")
          val allValues = csv.
            map(line => line(key)).
            filterNot(_.trim.isEmpty).
            map(BigDecimal(_))
          key -> median(allValues).toString()
        case Failure(_) =>
          key -> value
      }
    }
  }


  override def transform(csv: CSV): CSV = {
    if (csv.isEmpty || csv.size < 15)
      println("Error, csv is insufficient")

    val top10 = analyze(csv.take(10))
    val top15 = analyze(csv.take(15))

    List(top10, top15)
  }

}
