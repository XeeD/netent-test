package netent.slots

import cats.effect.IO
import netent.rng.RandomNumberGenerator

trait Game[Result] {
  def play(bet: Int, rng: RandomNumberGenerator, previousResultOpt: Option[Result]): IO[Result]
}
