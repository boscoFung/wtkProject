package Factory
import Card.HorseType
import Equipment.Equipment
import Equipment.HorseMinus
import Equipment.HorsePlus
import General.Player

object HorseFactory {
    fun createHorse(player: Player, name: String, type: HorseType): Equipment {
        return when (type) {
            HorseType.PLUS -> object : HorsePlus(player) {
                override val name: String = name
            }
            HorseType.MINUS -> object : HorseMinus(player) {
                override val name: String = name
            }
        }
    }
}