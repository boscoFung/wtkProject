package Card
import Equipment.*
import General.Player

enum class HorseType {
    PLUS, MINUS
}

class HorseCard(override val Suit: String, override val Number: String, override val Name: String, val type: HorseType)
    : EquipmentCard(Suit, Number, Name)