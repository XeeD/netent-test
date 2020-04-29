package netent.slots

import cats.effect.IO
import netent.slots.BonusGameRoundResult.{ BonusRoundEnded, Lost }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BonusGameSpec extends AnyFlatSpec with Matchers {
  it should "triggers a bonus round" in {
    val rng = new TestRNG(Vector(false, true), Vector(1))
    val chooseBox = IO.pure(1)
    BonusGame.play(10, chooseBox, rng).unsafeRunSync() shouldBe Lost(10)
    BonusGame.play(10, chooseBox, rng).unsafeRunSync() shouldBe BonusRoundEnded(10, 0)
  }

  it should "ask the player to input the box number" in {
    var boxWasChosen = false
    val chooseBox = IO {
      boxWasChosen = true
      1
    }
    val rng = new TestRNG(Vector(true), Vector(1))
    BonusGame.play(10, chooseBox, rng).unsafeRunSync() shouldBe BonusGameRoundResult
      .BonusRoundEnded(10, 0)
    boxWasChosen shouldBe true
  }

  it should "add half the amount of bet when bonus round is won" in {
    val choices = List(0, 1, 1, 4).iterator
    val chooseBox = IO {
      choices.next()
    }
    val rng = new TestRNG(Vector(true), Vector(1, 2, 3, 4))
    BonusGame.play(10, chooseBox, rng).unsafeRunSync() shouldBe BonusGameRoundResult
      .BonusRoundEnded(10, 15)
  }
}
