package Card

import Player

abstract class Card(open val Suit: String, open val Number: String, open val Name: String) {
}

abstract class BasicCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name)

abstract class EffectCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name) {
    abstract fun effect(currentPlayer: Player, allPlayers: List<Player>)
}
abstract class EquipmentCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name) {
}

abstract class JudgementCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name) {
}
class EightTrigramsCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Eight Trigrams Formation") {
    // This represents the card in the deck that can be equipped as EightTrigrams armor
}