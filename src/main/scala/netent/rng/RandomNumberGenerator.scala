package netent.rng

import cats.effect.IO

trait RandomNumberGenerator {
  def nextBoolean(probability: Double): IO[Boolean]
  def nextInt(n: Int): IO[Int]
}
