package netent

import cats.effect.{ ExitCode, IOApp }
import netent.rng.PseudoRandomRNG
import netent.slots.SlotGame

object Main extends IOApp {

  override def run(args: List[String]) =
    SlotGame
      .play(10, PseudoRandomRNG, None)
      .map { result =>
        println(result)
      }
      .as(ExitCode.Success)
}
