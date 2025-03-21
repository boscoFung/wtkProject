package Card

class EightTrigramsCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Eight Trigrams Formation") {
    // This represents the card in the deck that can be equipped as EightTrigrams armor
}

// +1馬卡牌
class RedHareCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Red Hare")

class ShadowrunnerCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Shadowrunner")

class FlyingLightningCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Flying Lightning")

// -1馬卡牌
class HexMarkCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Hex Mark")

class VioletStallionCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Violet Stallion")

class FerghanaHorseCard(override val Suit: String, override val Number: String)
    : EquipmentCard(Suit, Number, "Ferghana Horse")
