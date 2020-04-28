package netent.slots

import cats.effect.IO
import netent.rng.RandomNumberGenerator
import netent.slots.SlotGameRoundResult.{ Lost, Won }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SlotGameSpec extends AnyFlatSpec with Matchers {
  it should "win a round and provide a normal round" in {
    val rng = new TestRNG(Vector(true, false))
    SlotGame.play(10, rng).unsafeRunSync() shouldBe Won(10, RoundType.Normal, 20)
  }

  it should "win a round and provide a free round" in {
    val rng = new TestRNG(Vector(true, true))
    SlotGame.play(10, rng).unsafeRunSync() shouldBe Won(10, RoundType.Free, 20)
  }

  it should "lose a round and continue with a normal round" in {
    val rng = new TestRNG(Vector(false, false))
    SlotGame.play(10, rng).unsafeRunSync() shouldBe Lost(10, RoundType.Normal)
  }

  it should "lose a round and continue with a free round" in {
    val rng = new TestRNG(Vector(false, true, true, false, true, true, false, false))
    val result1 = SlotGame.play(10, rng).unsafeRunSync()
    result1 shouldBe Lost(10, RoundType.Free)

    val result2 = SlotGame.play(10, rng, Some(result1)).unsafeRunSync()
    result2 shouldBe Won(0, RoundType.Normal, 20)

    val result3 = SlotGame.play(10, rng, Some(result2)).unsafeRunSync()
    result3 shouldBe Won(10, RoundType.Free, 20)

    val result4 = SlotGame.play(10, rng, Some(result3)).unsafeRunSync()
    result4 shouldBe Lost(0, RoundType.Normal)
  }

  it should "continue playing based on previous round results" in {
    val rng = new TestRNG(Vector(false, true))
    SlotGame.play(10, rng).unsafeRunSync() shouldBe Lost(10, RoundType.Free)
  }
}

class TestRNG(results: Vector[Boolean]) extends RandomNumberGenerator {
  val resultsIterator = results.iterator
  override def nextBoolean(probability: Double) =
    IO.pure(resultsIterator.next())
}
