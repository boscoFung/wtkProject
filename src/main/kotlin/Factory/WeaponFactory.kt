package Factory

import Equipment.*
import General.Player

object WeaponFactory {
    fun createWeapon(player: Player, name: String): Weapon {
        return when (name) {
            "Zhuge Crossbow" -> ZhugeCrossbow(player)
            "Rock Cleaving Axe" -> RockCleavingAxe(player)
            "Heaven Scorcher Halberd" -> HeavenScorcherHalberd(player)
            "Gender Double Swords" -> GenderDoubleSwords(player)
            "Green Dragon Crescent Blade" -> GreenDragonCrescentBlade(player)
            "Blue Steel Blade" -> BlueSteelBlade(player)
            "Serpent Spear" -> SerpentSpear(player)
            "Kirin Bow" -> KirinBow(player)
            else -> throw IllegalArgumentException("Unknown weapon: $name")
        }
    }
}