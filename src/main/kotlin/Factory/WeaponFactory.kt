package Factory

import Equipment.*
import General.Player

object WeaponFactory {
    fun createWeapon(player: Player, name: String): Weapon {
        return when (name) {
            "Zhuge Crossbow" -> ZhugeCrossbow(player)
            "Rock Cleaving Axe" -> RockCleavingAxe(player)
            "Sky Piercing Halberd" -> SkyPiercingHalberd(player)
            "Yin-Yang Swords" -> YinYangSwords(player)
            "Green Dragon Blade" -> GreenDragonBlade(player)
            "Blue Steel Blade" -> BlueSteelBlade(player)
            "Serpent Spear" -> SerpentSpear(player)
            "Kirin Bow" -> KirinBow(player)
            else -> throw IllegalArgumentException("Unknown weapon: $name")
        }
    }
}