package netent.slots

import cats.effect.IO
import netent.rng.RandomNumberGenerator

class TestRNG(booleanResults: Vector[Boolean], intResults: Vector[Int] = Vector.empty)
  extends RandomNumberGenerator {
  val booleanResultsIterator = booleanResults.iterator
  override def nextBoolean(probability: Double) =
    IO.pure(booleanResultsIterator.next())

  val intResultsIterator = intResults.iterator
  override def nextInt(maximum: Int) =
    IO.pure(intResultsIterator.next())
}
