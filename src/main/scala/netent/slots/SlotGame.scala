package netent.slots

import cats.effect.IO
import netent.rng.RandomNumberGenerator

sealed trait RoundType

object RoundType {
  case object Normal extends RoundType
  case object Free extends RoundType
}

sealed trait SlotGameRoundResult extends GameRoundResult[RoundType]

object SlotGameRoundResult {
  case class Won(bet: Int, nextRound: RoundType, prize: Int) extends SlotGameRoundResult

  case class Lost(bet: Int, nextRound: RoundType) extends SlotGameRoundResult {
    val prize = 0
  }
}

object SlotGame extends Game[SlotGameRoundResult] {

  val ROUND_WIN_PROBABILITY = 0.3
  val FREE_ROUND_PROBABILITY = 0.1

  def play(
      bet: Int,
      rng: RandomNumberGenerator,
      previousRoundOpt: Option[SlotGameRoundResult] = None): IO[SlotGameRoundResult] =
    previousRoundOpt match {
      case None => playNormalRound(bet, rng)
      case Some(previousRound) =>
        previousRound.nextRound match {
          case RoundType.Normal => playNormalRound(bet, rng)
          case RoundType.Free   => playFreeRound(bet, rng)
        }
    }

  private def playNormalRound(bet: Int, generator: RandomNumberGenerator): IO[SlotGameRoundResult] =
    for {
      wonRound <- generator.nextBoolean(ROUND_WIN_PROBABILITY)
      nextRoundIsFree <- generator.nextBoolean(FREE_ROUND_PROBABILITY)

      nextRoundType = if (nextRoundIsFree) RoundType.Free else RoundType.Normal
      result =
        if (wonRound)
          SlotGameRoundResult.Won(bet, nextRoundType, bet * 2)
        else
          SlotGameRoundResult.Lost(bet, nextRoundType)
    } yield result

  private def playFreeRound(bet: Int, generator: RandomNumberGenerator): IO[SlotGameRoundResult] =
    for {
      wonRound <- generator.nextBoolean(ROUND_WIN_PROBABILITY)
      nextRoundIsFree <- generator.nextBoolean(FREE_ROUND_PROBABILITY)

      nextRoundType = if (nextRoundIsFree) RoundType.Free else RoundType.Normal
      result =
        if (wonRound)
          SlotGameRoundResult.Won(0, nextRoundType, bet * 2)
        else
          SlotGameRoundResult.Lost(0, nextRoundType)
    } yield result

}
