package netent.slots

import cats.effect.IO
import netent.rng.RandomNumberGenerator

sealed trait RoundType

object RoundType {
  case object Normal extends RoundType
  case object Free extends RoundType
}

sealed trait SlotGameRoundResult extends GameRoundResult {
  def nextRound: RoundType
}

object SlotGameRoundResult {
  case class Won(bet: Int, nextRound: RoundType, prize: Int) extends SlotGameRoundResult

  case class Lost(bet: Int, nextRound: RoundType) extends SlotGameRoundResult {
    val prize = 0
  }
}

/**
  * A Slot game with the following rules:
  * - The player bets X coins to play a normal game round
  * - In any round (free or normal), the player has a 30 % chance of winning back 20
  *   coins.
  * - In any round (free or normal), the player also has a 10% chance of triggering a
  *   free round where the player does not have to pay for bet. The free round
  *    works in the same way as a normal round except it costs 0 coins.
  *    The free round should follow immediately after the normal round.
  *    Also, a free round can win another free round.
  * - The player can both win coins and free round at the same time.
  */
object SlotGame extends {

  val ROUND_WIN_PROBABILITY = 0.3
  val FREE_ROUND_PROBABILITY = 0.1

  /**
    * This method is called each time the player presses the Spin button.
    * @param bet The amount the player bets on this spin
    * @param rng The random number generator to use
    * @param previousRoundOpt Optional result of the previous spin
    * @return Result of this spin with possible free spin for the next round
    */
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
