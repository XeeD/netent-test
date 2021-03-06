package netent

import cats.effect.{ ExitCode, IO, IOApp }
import netent.rng.PseudoRandomRNG
import netent.simulation.GameSimulation
import netent.slots.{ BonusGame, BonusGameRoundResult, BoxGame, SlotGame, SlotGameRoundResult }
import cats.syntax.show._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  implicit private val unsafeLogger = Slf4jLogger.getLogger[IO]

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
      .flatMap { result =>
        Logger[IO].info(result.show)
      }

  private val runBonusGameSimulation =
    GameSimulation
      .simulate(1000000, 10) { (bet, _: Option[BonusGameRoundResult]) =>
        BonusGame.play(bet, PseudoRandomRNG.nextInt(BoxGame.NUMBER_OF_BOXES), PseudoRandomRNG)
      }
      .flatMap { result =>
        Logger[IO].info(result.show)
      }
}
