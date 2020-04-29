package netent.simulation

import cats.effect.IO
import cats.effect.concurrent.Ref
import fs2.Stream
import netent.slots.GameRoundResult

object GameSimulation {

  /**
    * This method simulates X rounds of a game and tracks their result.
    * @param rounds The number of game rounds to simulate
    * @param bet How much to bet in each round
    * @param play Callback that is responsible for actually running each round of the simulation
    * @tparam R Result of the game round simulation
    * @return The accumulated result of the simulation
    */
  def simulate[R <: GameRoundResult](rounds: Int, bet: Int)(
      play: (Int, Option[R]) => IO[R]): IO[SimulationResult] =
    for {
      previousValueRef <- Ref.of[IO, Option[R]](None)
      initialResult <- SimulationResult(bet, rounds)
      finalResult <-
        Stream
        // Run X rounds of the simulation
          .range(1, rounds)
          // Run the game round which might depend on the previous round result
          .evalMap { _ =>
            for {
              previousRound <- previousValueRef.get
              roundResult <- play(bet, previousRound)
              _ <- previousValueRef.set(Some(roundResult))
            } yield roundResult
          }
          .compile
          // Collect and accumulate the results
          .fold(initialResult) { (acc, roundResult) =>
            acc.increase(roundResult)
          }

    } yield finalResult

}
