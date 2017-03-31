package com.clemble.aws.analysis

import java.text.SimpleDateFormat
import java.util.Date

import scala.util.Try

trait CSVTransformer {

  def transform(csv: AWSResults): CSV

}

object AWSScoutTransformer extends CSVTransformer with Loggable {

  private val NON_RELEVANT = List("Available From", "Seller", "#", "ASIN", "Brand", "URL", "Min Price")

  private def dropNonRelevant(csv: CSV): CSV = {
    csv.map(line => line.filterKeys(!NON_RELEVANT.contains(_)))
  }

  private def reviewsLessThen50(csv: CSV): Int = {
    csv.flatMap(_.get("# of Reviews").flatMap(_.asBigDecimal)).count(_ <= 50)
  }

  private def analyzeMedians(csv: CSV): CSVLine = {
    for {
      (key, value) <- csv.head
    } yield {
      value.asBigDecimal match {
        case Some(_) =>
          val allValues = csv.
            flatMap(line => line.get(key)).
            filterNot(_.trim.isEmpty).
            flatMap(_.asBigDecimal)
          key -> allValues.median().toString()
        case None =>
          LOG.debug(s"Read $key $value as String")
          key -> value
      }
    }
  }

  private def analyzeCSV(csv: CSV): CSVLine = {
    val relevant = dropNonRelevant(csv)
    val line = analyzeMedians(relevant)
    val reviews = reviewsLessThen50(relevant)
    line + ("Reviews < 50" -> reviews.toString)
  }

  def normalizeQuery(q: String): String = {
    q.
      replaceAll("Search Term of", "").
      replaceAll("\\sat\\s*[0-9|-]*", "").
      replaceAll("\\.csv", "").
      trim
  }

  private def parseDate(res: AWSResults): Date = {
    val createdDate = res.query.replaceAll(".*at\\s*|\\.csv", "").trim
    Try({
      new SimpleDateFormat("M-dd-yyyy").parse(createdDate)
    }).toOption.
      getOrElse(res.created)
  }

  override def transform(res: AWSResults): CSV = {
    if (res.csv.isEmpty || res.csv.size < 15)
      LOG.error("Error, csv is insufficient")
    val normQuery = normalizeQuery(res.query)
    val createdDate = parseDate(res)
    LOG.debug(s"Query after normalization $normQuery")

    val dateFormat = new SimpleDateFormat("dd/M/yyyy")
    val top10 = analyzeCSV(res.csv.take(10)) + ("name" -> normQuery) + ("#" -> 10.toString) + ("Created" -> dateFormat.format(createdDate))
    val top15 = analyzeCSV(res.csv.take(15)) + ("name" -> normQuery) + ("#" -> 15.toString) + ("Created" -> dateFormat.format(createdDate))

    val csv = List(top10, top15)
    csv
  }

}
