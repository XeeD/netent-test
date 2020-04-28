package netent

import cats.effect.{ ExitCode, IOApp }
import netent.rng.PseudoRandomRNG
import netent.simulation.GameSimulation
import netent.slots.SlotGame
import cats.syntax.show._

object Main extends IOApp {

  override def run(args: List[String]) =
    GameSimulation
      .simulate(10, 1000000, SlotGame, PseudoRandomRNG)
      .map { result =>
        println(result.show)
      }
      .as(ExitCode.Success)
}
