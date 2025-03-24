package Factory
import Card.EquipmentCard
import Card.HorseType
import Equipment.Equipment
import Equipment.HorseMinus
import Equipment.HorsePlus
import General.Player

object HorseFactory {
    fun createHorse(player: Player, name: String, type: HorseType, card: EquipmentCard): Equipment {
        return when (type) {
            HorseType.PLUS -> object : HorsePlus(player, card) {
                override val name: String = name
            }
            HorseType.MINUS -> object : HorseMinus(player, card) {
                override val name: String = name
            }
        }
    }
}
