import arbitrage.Solution._

import org.scalatest.{Matchers, WordSpec}
import java.io.ByteArrayOutputStream
import scala.Console.in

class SolutionTest extends WordSpec with Matchers {

  val testCaseMatrix: Array[Array[Double]] = Array(
    Array(0, 0.7779, 102.4590, 0.0083),
    Array(1.2851, 0, 131.7110, 0.01125),
    Array(0.0098, 0.0075, 0, 0.0000811),
    Array(115.65, 88.8499, 12325.44, 0)
  )

  "Arbitrage solution components" should {

    "correctly parse matrix" in {
      val j =
        scala.io.Source.fromResource("server-response.json").getLines().next()
      val res = convertResponse(j)
      res._1.size shouldEqual 4
    }

    "find one of the arbitrage opportunities for particular example" in {
      val arbitrage =
        bitcoinArbitrage(testCaseMatrix).mapValues(_.mkString(","))
      arbitrage.keys.toArray.apply(2) * 100 shouldBe (101.20965 +- 0.01)
    }

    "find particular arbitrage opportunities using emulated server response" in {
      val j =
        scala.io.Source.fromResource("test-case.json").getLines().next()
      val resp = convertResponse(j)
      val arb = bitcoinArbitrage(resp._2)
      arb
        .mapValues(_.mkString(","))
        .keys
        .toArray
        .apply(2) * 100 shouldBe (101.20965 +- 0.01)
    }

    "correctly print results" in {
      val out = new ByteArrayOutputStream()
      Console.withOut(out) {
        Console.withIn(in) {
          consoleOut(
            Map("USD" -> 0, "EUR" -> 1, "JPY" -> 2, "BTC" -> 3),
            bitcoinArbitrage(testCaseMatrix)
          )
        }
      }
      out.toString should include("Here are")
    }
  }
}
