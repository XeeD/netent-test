package netent.simulation

import java.util.UUID

import cats.Show
import cats.effect.IO
import netent.slots.GameRoundResult

case class SimulationResult(
    simulationId: UUID,
    rounds: Int,
    bet: Int,
    betsSum: Int,
    prizeSum: Int
) {
  def increase[R <: GameRoundResult](roundResult: R) =
    copy(betsSum = betsSum + roundResult.bet, prizeSum = prizeSum + roundResult.prize)

  lazy val rtp: Double = prizeSum / betsSum.toDouble
}

object SimulationResult {
  def apply(bet: Int, rounds: Int): IO[SimulationResult] =
    IO(UUID.randomUUID()).map { id =>
      new SimulationResult(id, rounds, bet, 0, 0)
    }

  implicit val simulationResultShow: Show[SimulationResult] = Show.show { result =>
    s"""Result (id: ${result.simulationId}):
       |number of game rounds: ${result.rounds}
       |win total: ${result.prizeSum}
       |bet total: ${result.betsSum}
       |RPT: ${result.rtp}""".stripMargin
  }
}
