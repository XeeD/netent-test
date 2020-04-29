package netent

import cats.effect.{ ExitCode, IO, IOApp }
import netent.rng.PseudoRandomRNG
import netent.simulation.GameSimulation
import netent.slots.{ BonusGame, BonusGameRoundResult, BoxGame, SlotGame, SlotGameRoundResult }
import cats.syntax.show._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- runSlotGameSimulation
      _ <- runBonusGameSimulation
    } yield ExitCode.Success

  private val runSlotGameSimulation =
    GameSimulation
      .simulate(1000000, 10) { (bet, previousRound: Option[SlotGameRoundResult]) =>
        SlotGame.play(bet, PseudoRandomRNG, previousRound)
      }
      .map { result =>
        println(result.show)
      }

  private val runBonusGameSimulation =
    GameSimulation
      .simulate(1000000, 10) { (bet, _: Option[BonusGameRoundResult]) =>
        BonusGame.play(bet, PseudoRandomRNG.nextInt(BoxGame.NUMBER_OF_BOXES), PseudoRandomRNG)
      }
      .map { result =>
        println(result.show)
      }
}
