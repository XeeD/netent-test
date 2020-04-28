package netent.slots

trait GameRoundResult[RT] {
  def bet: Int
  def nextRound: RT
  def prize: Int
}
