package netent.slots

import cats.effect.IO
import netent.rng.RandomNumberGenerator

sealed trait RoundType

case object NormalRound extends RoundType
case object FreeRound extends RoundType

sealed trait RoundResult {
  def bet: Int
  def nextRound: RoundType
}

case class RoundWon(bet: Int, nextRound: RoundType, win: Int) extends RoundResult
case class RoundLost(bet: Int, nextRound: RoundType) extends RoundResult

object SlotGame {

  val ROUND_WIN_PROBABILITY = 0.3
  val FREE_ROUND_PROBABILITY = 0.1

  def play(
      bet: Int,
      rng: RandomNumberGenerator,
      previousRoundOpt: Option[RoundResult] = None): IO[RoundResult] =
    previousRoundOpt match {
      case None => playNormalRound(bet, rng)
      case Some(previousRound) =>
        previousRound.nextRound match {
          case NormalRound => playNormalRound(bet, rng)
          case FreeRound   => playFreeRound(bet, rng)
        }
    }

  private def playNormalRound(bet: Int, generator: RandomNumberGenerator): IO[RoundResult] =
    for {
      wonRound <- generator.nextBoolean(ROUND_WIN_PROBABILITY)
      nextRoundIsFree <- generator.nextBoolean(FREE_ROUND_PROBABILITY)

      nextRoundType = if (nextRoundIsFree) FreeRound else NormalRound
      result =
        if (wonRound)
          RoundWon(bet, nextRoundType, bet * 2)
        else
          RoundLost(bet, nextRoundType)
    } yield result

  private def playFreeRound(bet: Int, generator: RandomNumberGenerator): IO[RoundResult] =
    for {
      wonRound <- generator.nextBoolean(ROUND_WIN_PROBABILITY)
      nextRoundIsFree <- generator.nextBoolean(FREE_ROUND_PROBABILITY)

      nextRoundType = if (nextRoundIsFree) FreeRound else NormalRound
      result =
        if (wonRound)
          RoundWon(0, nextRoundType, bet * 2)
        else
          RoundLost(0, nextRoundType)
    } yield result

}
