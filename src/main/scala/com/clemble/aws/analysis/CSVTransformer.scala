package com.clemble.aws.analysis

import javax.management.Query

import scala.util.{Failure, Success, Try}

trait CSVTransformer {

  def transform(csv: AWSResults): CSV

}

object AWSScoutTransformer extends CSVTransformer {

  implicit class StringToBigDecimal(str: String) {
    def asBigDecimal = Try(BigDecimal(str)).toOption
  }

  private def median(numbers: Seq[BigDecimal]): BigDecimal = {
    val (lower, upper) = numbers.sorted.splitAt(numbers.size / 2)
    if (numbers.size % 2 == 0)
      (lower.last + upper.head) / 2
    else
      upper.head
  }

  val NON_RELEVANT = List("Available From", "Seller", "#", "ASIN", "Brand", "URL")

  private def dropNonRelevant(csv: CSV): CSV = {
    csv.map(line => line.filterKeys(!NON_RELEVANT.contains(_)))
  }

  private def reviewsLessThen50(csv: CSV): Int = {
    csv.map(_.get("# of Reviews").flatMap(_.asBigDecimal)).flatten.filter(_ <= 50).size
  }

  private def countMedians(csv: CSV): CSVLine = {
    for {
      (key, value) <- csv.head
    } yield {
      value.asBigDecimal match {
        case Some(_) =>
          val allValues = csv.
            map(line => line(key)).
            filterNot(_.trim.isEmpty).
            map(_.asBigDecimal).
            flatten
          key -> median(allValues).toString()
        case None =>
          key -> value
      }
    }
  }

  private def analyzeCSV(csv: CSV): CSVLine = {
    val relevant = dropNonRelevant(csv)
    val line = countMedians(relevant)
    val reviews = reviewsLessThen50(relevant)
    line + ("Reviews < 50" -> reviews.toString)
  }

  private def normalizeQuery(q: String): String = {
    q.
      replaceAll("Search Term of", "").
      replaceAll("at\\s*[0-9|-]*", "").
      replaceAll("\\.csv", "").
      trim
  }

  override def transform(res: AWSResults): CSV = {
    if (res.csv.isEmpty || res.csv.size < 15)
      println("Error, csv is insufficient")
    val normQuery = normalizeQuery(res.query)

    val top10 = analyzeCSV(res.csv.take(10)) + ("name" -> normQuery) + ("#" -> 10.toString)
    val top15 = analyzeCSV(res.csv.take(15)) + ("name" -> normQuery) + ("#" -> 15.toString)

    List(top10, top15)
  }

}
