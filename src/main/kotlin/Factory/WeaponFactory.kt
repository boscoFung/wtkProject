package Factory

import Card.EquipmentCard
import Equipment.*
import General.Player

object WeaponFactory {
    fun createWeapon(player: Player, name: String, card: EquipmentCard): Weapon {
        return when (name) {
            "Zhuge Crossbow" -> ZhugeCrossbow(player, card)
            "Rock Cleaving Axe" -> RockCleavingAxe(player, card)
            "Sky Piercing Halberd" -> SkyPiercingHalberd(player, card)
            "Yin-Yang Swords" -> YinYangSwords(player, card)
            "Green Dragon Blade" -> GreenDragonBlade(player, card)
            "Blue Steel Blade" -> BlueSteelBlade(player, card)
            "Serpent Spear" -> SerpentSpear(player, card)
            "Kirin Bow" -> KirinBow(player, card)
            else -> throw IllegalArgumentException("Unknown weapon: $name")
        }
    }
}