package com.github.zwrss.gameorganizer.console

object TablePrinter {

  private def line(colSizes: List[Int]): String = {
    "+" + colSizes.map("-" * _).mkString("+") + "+"
  }

  private def printRow(row: List[String], colSizes: List[Int]): String = {
    val inner = row.zip(colSizes).map {
      case (row, size) =>
        row + (" " * (size - row.size))
    }.mkString("|")

    "|" + inner + "|"
  }

  def format(headers: List[String], rows: List[List[String]]): String = {
    rows.foreach { row =>
      assert(headers.size == row.size)
    }

    val colSizes: List[Int] = headers.indices.toList.map { i =>
      val toCompare: List[String] = rows.map(_ apply i) :+ headers(i)
      toCompare.map(_.size).max
    }

    line(colSizes) + "\n" +
    printRow(headers, colSizes) + "\n" +
    line(colSizes) + "\n" +
    rows.map(printRow(_, colSizes)).mkString("\n") + "\n" +
    line(colSizes) + "\n"

  }

}
