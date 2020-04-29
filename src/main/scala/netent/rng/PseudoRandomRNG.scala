package netent.rng

import cats.effect.IO

import scala.util.Random

object PseudoRandomRNG extends RandomNumberGenerator {
  override def nextBoolean(probability: Double) =
    IO.delay {
      math.random() < probability
    }

  override def nextInt(n: Int) =
    IO.delay {
      Random.nextInt(n)
    }
}
