package Card
import Equipment.*
import General.Player

class WeaponCard(override val Suit: String, override val Number: String, override val Name: String)
    : EquipmentCard(Suit, Number, Name)

object WeaponFactory {
    fun createWeapon(player: Player, name: String): Weapon {
        return when (name) {
            "Zhuge Crossbow" -> ZhugeCrossbow(player)
            else -> throw IllegalArgumentException("Unknown weapon: $name")
        }
    }
}


//            "Rock Cleaving Axe" -> RockCleavingAxe(player)
//            "Sky Piercing Halberd" -> SkyPiercingHalberd(player)
//            "Yin-Yang Swords" -> YinYangSwords(player)
//            "Green Dragon Blade" -> GreenDragonBlade(player)
//            "Blue Steel Blade" -> BlueSteelBlade(player)
//            "Serpent Spear" -> SerpentSpear(player)
//            "Kirin Bow" -> KirinBow(player)