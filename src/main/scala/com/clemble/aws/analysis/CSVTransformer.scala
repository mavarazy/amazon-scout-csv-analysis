package com.clemble.aws.analysis

import java.text.SimpleDateFormat

import scala.collection.mutable

trait CSVTransformer {

  def transform(csv: AWSResults): CSV

}

object AWSScoutTransformer extends CSVTransformer {

  private val NON_RELEVANT = List("Available From", "Seller", "#", "ASIN", "Brand", "URL", "Min Price")

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
          key -> allValues.median().toString()
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

  private val DESIRED_ORDER = List(
    "Created",
    "name",//
    "Category",//
    "#",//
    "Price",//
    "Est. Revenue",//
    "Reviews < 50",//
    "LQS",//
    "# of Reviews",//
    "Weight",//
    "FBA Fees",//
    "Est. Sales",
    "Rating",//
    "Rank",//
    "Sellers",
    "Net",
    "RPR"
  )


  override def transform(res: AWSResults): CSV = {
    if (res.csv.isEmpty || res.csv.size < 15)
      println("Error, csv is insufficient")
    val normQuery = normalizeQuery(res.query)

    val dateFormat = new SimpleDateFormat("dd/M/yyyy")
    val top10 = analyzeCSV(res.csv.take(10)) + ("name" -> normQuery) + ("#" -> 10.toString) + ("Created" -> dateFormat.format(res.created))
    val top15 = analyzeCSV(res.csv.take(15)) + ("name" -> normQuery) + ("#" -> 15.toString) + ("Created" -> dateFormat.format(res.created))

    val csv = List(top10, top15)
    CSVUtils.order(csv, DESIRED_ORDER)
  }

}
