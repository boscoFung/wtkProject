package Card

class AttackCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Attack") {
    override fun effect() {}
}

class DodgeCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Dodge") {
    override fun effect() {}
}

class PeachCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Peach") {
    override fun effect() {}
}