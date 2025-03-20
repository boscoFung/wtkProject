package Card

abstract class Card(open val Suit: String, open val Number: String, open val Name: String) {
    abstract fun effect()
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
    abstract override fun effect()
}

abstract class EquipmentCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name) {
    abstract override fun effect()
}

abstract class JudgementCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name) {
    abstract override fun effect()
}
