package com.clemble.aws.analysis

import scala.io.Source

trait CSVReader {

  def read(is: Source): CSV

}

object SimpleCSVReader extends CSVReader with Loggable {

  val headerNormalizer = Map[String, String](
    "№" -> "#",
    "Мин. цена" -> "Min Price",
    "Нетто" -> "Net",
    "Цена" -> "Price",
    "Бренд" -> "Brand",
    "FBA сбор" -> "FBA Fees",
    "Категория" -> "Category",
    "Ранг" -> "Rank",
    "Прибл. продажи" -> "Est. Sales",
    "Прибл. прибыль" -> "Est. Revenue",
    "Кол-во коммент." -> "# of Reviews",
    "Дата добавления" -> "Available From",
    "Рейтинг" -> "Rating",
    "Вес" -> "Weight",
    "Прод." -> "Seller",
    "# прод." -> "Sellers"
  )

  private def toCSV(lines: List[List[String]]): CSV = {
    lines match {
      case head :: csv =>
        val normHead = head.map(h => headerNormalizer.get(h).getOrElse(h))
        csv.map(normHead.zip(_).toMap.mapValues(Option(_).getOrElse("")))
      case Nil =>
        Nil
    }
  }

  override def read(source: Source): CSV = {
    val lines = source.getLines().
      map[List[String]]((line) => line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).toList).
      toList
    toCSV(lines)
  }

}