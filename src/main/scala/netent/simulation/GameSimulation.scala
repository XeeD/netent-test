package netent.simulation

import java.util.UUID

import cats.Show
import cats.effect.IO
import cats.effect.concurrent.Ref
import fs2.Stream
import netent.rng.RandomNumberGenerator
import netent.slots.{ Game, GameRoundResult }

case class SimulationResult(
    simulationId: UUID,
    rounds: Int,
    bet: Int,
    betsSum: Int,
    prizeSum: Int
) {
  def increase[R <: GameRoundResult[_]](roundResult: R) =
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
       |number of game round: ${result.rounds}
       |win total: ${result.prizeSum}
       |bet total: ${result.betsSum}
       |RPT: ${result.rtp}""".stripMargin
  }
}

object GameSimulation {
  def simulate[R <: GameRoundResult[_]](
      bet: Int,
      rounds: Int,
      game: Game[R],
      rng: RandomNumberGenerator): IO[SimulationResult] =
    for {
      previousValueRef <- Ref.of[IO, Option[R]](None)
      initialResult <- SimulationResult(bet, rounds)
      finalResult <-
        Stream
          .range(1, rounds)
          .evalMap { _ =>
            for {
              previousRound <- previousValueRef.get
              roundResult <- game.play(bet, rng, previousRound)
              _ <- previousValueRef.set(Some(roundResult))
            } yield roundResult
          }
          .compile
          .fold(initialResult) { (acc, roundResult) =>
            acc.increase(roundResult)
          }

    } yield finalResult

}
