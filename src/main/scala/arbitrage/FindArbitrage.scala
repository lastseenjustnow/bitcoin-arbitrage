package arbitrage

import arbitrage.Solution._

object FindArbitrage {
  def main(args: Array[String]): Unit = {
    val resp = convertResponse(requestRates())
    val arb = bitcoinArbitrage(resp._2)
    consoleOut(resp._1, arb)
  }
}
