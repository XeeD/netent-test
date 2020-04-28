package netent.rng

import cats.effect.IO

object PseudoRandomRNG extends RandomNumberGenerator {
  override def nextBoolean(probability: Double) =
    IO.delay {
      math.random() < probability
    }
}
