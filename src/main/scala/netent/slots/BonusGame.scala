package netent.slots

import cats.effect.IO
import cats.syntax.applicative._
import netent.rng.RandomNumberGenerator

sealed trait BonusGameRoundType

object BonusGameRoundType {
  case object Normal extends BonusGameRoundType
}

sealed trait BonusGameRoundResult extends GameRoundResult[BonusGameRoundType]

object BonusGameRoundResult {
  case class Lost(bet: Int) extends BonusGameRoundResult {
    val nextRound = BonusGameRoundType.Normal
    val prize = 0
  }

  case class BonusRoundEnded(bet: Int, prize: Int) extends BonusGameRoundResult {
    val nextRound = BonusGameRoundType.Normal
  }
}

case class BoxGame private (losingBox: Int) {
  def isLosingBox(chooseBox: IO[Int]): IO[Boolean] =
    chooseBox.map(boxNumber => losingBox == boxNumber)
}

object BoxGame {
  val NUMBER_OF_BOXES = 5

  def make(rng: RandomNumberGenerator): IO[BoxGame] =
    rng.nextInt(NUMBER_OF_BOXES).map(losingBox => BoxGame(losingBox))
}

object BonusGame {

  val BONUS_ROUND_PROBABILITY = 0.1

  def play(bet: Int, chooseBox: IO[Int], rng: RandomNumberGenerator): IO[BonusGameRoundResult] =
    playNormalRound(bet, chooseBox, rng)

  private def playNormalRound(
      bet: Int,
      chooseBox: IO[Int],
      rng: RandomNumberGenerator): IO[BonusGameRoundResult] =
    for {
      wonBonusRound <- rng.nextBoolean(BONUS_ROUND_PROBABILITY)

      result <-
        if (wonBonusRound)
          BoxGame.make(rng).flatMap(boxGame => playBonusRound(chooseBox, boxGame, bet, 0, rng))
        else
          BonusGameRoundResult.Lost(bet).pure[IO]
    } yield result

  private def playBonusRound(
      chooseBox: IO[Int],
      boxGame: BoxGame,
      bet: Int,
      prizeSum: Int,
      rng: RandomNumberGenerator): IO[BonusGameRoundResult] =
    for {
      lostBonusRound <- boxGame.isLosingBox(chooseBox)
      result <-
        if (lostBonusRound)
          BonusGameRoundResult.BonusRoundEnded(bet, prizeSum).pure[IO]
        else
          BoxGame
            .make(rng)
            .flatMap(nextBoxGame =>
              playBonusRound(chooseBox, nextBoxGame, bet, prizeSum + (bet / 2), rng))
    } yield result

}
