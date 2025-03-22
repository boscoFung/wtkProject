package Card
import Equipment.*
import General.Player

class WeaponCard(override val Suit: String, override val Number: String, override val Name: String)
    : EquipmentCard(Suit, Number, Name)

