package netent.rng

import cats.effect.IO

/**
  * A random number generator. Each value can be obtained asynchronously,
  * hence it is suspended in an IO. In a production system these values
  * could be obtained from a dedicated service and we might want to attach
  * logging and tracing to them.
  */
trait RandomNumberGenerator {
  def nextBoolean(probability: Double): IO[Boolean]
  def nextInt(n: Int): IO[Int]
}
