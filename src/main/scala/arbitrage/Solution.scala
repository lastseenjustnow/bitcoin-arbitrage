package arbitrage

import io.circe.Json
import io.circe.parser.parse

import scala.Console.{GREEN, RED, RESET}
import scala.annotation.tailrec
import scala.collection.SortedMap

object Solution {

  def requestRates(): String =
    requests.get("https://api.swissborg.io/v1/challenge/rates").text

  def convertResponse(r: String): (Map[String, Int], Array[Array[Double]]) = {
    val json: Json = parse(r).right.get

    var ix: Map[String, Int] = Map()
    var res: Array[Array[Double]] = Array()
    var matL = 0

    json.hcursor.keys.head.foreach(key => {
      val tickers = key.split('-')
      val rate = json.hcursor.downField(key).as[Double].right.get

      if (!ix.contains(tickers.head)) {
        ix = ix + (tickers.head -> matL)
        res = res.map(x => x :+ 0.0) :+ (Array.fill(matL)(0.0) :+ 0.0)
        matL += 1
      }

      if (!ix.contains(tickers.last)) {
        ix = ix + (tickers.last -> matL)
        res = res.map(x => x :+ 0.0) :+ (Array.fill(matL)(0.0) :+ 0.0)
        matL += 1
      }

      res(ix(tickers.head))(ix(tickers.last)) = rate
    })

    (ix, res)
  }

  def bitcoinArbitrage(
      rate: Array[Array[Double]]
  ): SortedMap[Double, Array[Int]] = {

    /** All Pairs Shortest Path: Floyd-Marshall variant with modification
      *
      * Time complexity: O(n ** 4)
      */

    // Recreate path to show currency pairs together with arbitrage profitability
    @tailrec
    def recreatePath(
        s: Int,
        t: Int,
        l: Int,
        res: Array[Int],
        succ: Array[Array[Array[Int]]]
    ): Array[Int] = {
      if (l == 0) res :+ s
      else recreatePath(succ(l)(s)(t), t, l - 1, res :+ s, succ)
    }

    val n = rate.length

    // Matrix to keep the most beneficial path on each iteration
    val benefit: Array[Array[Array[Double]]] = {
      Array.fill(n)(Array.fill(n)(Array.fill(n)(0)))
    }

    // Matrix to recreate currency pairs path
    val succ: Array[Array[Array[Int]]] = {
      Array.fill(n)(Array.fill(n)(Array.fill(n)(-1)))
    }

    for (i <- 0 until n; j <- 0 until n) {
      benefit(1)(i)(j) = rate(i)(j)
      if (rate(i)(j) > 0) succ(1)(i)(j) = j
    }

    implicit val ordering: Ordering[Double] = Ordering.Double.reverse
    var ops: SortedMap[Double, Array[Int]] = SortedMap()
    for (l <- 2 until n; i <- 0 until n; j <- 0 until n; k <- 0 until n) {
      if (benefit(l)(i)(j) < rate(i)(k) * benefit(l - 1)(k)(j)) {
        benefit(l)(i)(j) = rate(i)(k) * benefit(l - 1)(k)(j)
        succ(l)(i)(j) = k
        if (benefit(l)(i)(i) > 1 && !ops.contains(benefit(l)(i)(i))) {
          ops = ops + (benefit(l)(i)(i) -> recreatePath(i, i, l, Array(), succ))
        }
      }
    }

    ops
  }

  def consoleOut(
      ix: Map[String, Int],
      res: SortedMap[Double, Array[Int]]
  ): Unit = {

    if (res.isEmpty) {
      Console.println("No arbitrage opportunities are found!")
    } else {

      val rev: Map[Int, String] = for ((k, v) <- ix) yield (v, k)

      Console.println(
        "Here are arbitrage opportunities sorted by profitability in descending order:"
      )

      res.foreach(x => {
        Console.printf(
          f"Trade sequence: $RESET$RED${x._2
            .map(y => rev(y))
            .mkString("->")}$RESET with profitability:$RESET$GREEN %%.4f%%c $RESET %n",
          (x._1 - 1) * 100,
          37
        )
      })
    }
  }
}
