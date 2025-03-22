package Equipment
import Card.AttackCard
import Card.Card
import Card.CardDeck
import General.*
import Strategy.*
import kotlin.random.Random

class ZhugeCrossbow(player: Player) : Weapon(player) {
    override val name: String = "Zhuge Crossbow"
    override val attackLimitModifier: Int = -1
    override val attackRangeModifier: Int = 0

    override fun canAttack(attacksThisTurn: Int): Boolean {
        return true
    }

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        println("${attacker.name} uses $name with ${attackCard?.Suit} ${attackCard?.Number} - ${attackCard?.Name} to attack ${target.name}")
        target.attack(attacker)
    }
}
//class RockCleavingAxe(player: Player) : Weapon(player) {
//    override val name: String = "Rock Cleaving Axe"
//    override fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
//        if (!target.hasDodgeCard() && attacker.hand.size >= 2) {
//            println("${attacker.name} may discard 2 cards to force hit with $name")
//            repeat(2) { attacker.hand.removeAt(0).also { CardDeck.discardCard(it) } }
//            target.currentHP--
//            println("${target.name} is hit by $name, HP: ${target.currentHP}")
//        } else {
//            target.beingAttacked()
//        }
//    }
//}
//
//class SkyPiercingHalberd(player: Player) : Weapon(player) {
//    override val name: String = "Sky Piercing Halberd"
//    override fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
//        val range = attacker.calculateAttackRange()
//        val allPlayers = GeneralManager.getPlayerList()
//        allPlayers.filter { it != attacker && attacker.calculateDistanceTo(it, allPlayers.size) <= range }.forEach {
//            println("${attacker.name} attacks ${it.name} with $name")
//            it.beingAttacked()
//        }
//    }
//}
//
//class YinYangSwords(player: Player) : Weapon(player) {
//    override val name: String = "Yin-Yang Swords"
//    override fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
//        target.beingAttacked()
//        if (target.currentHP < target.maxHP && target.hand.isNotEmpty()) {
//            println("${target.name} must discard a card or ${attacker.name} draws a card")
//            target.hand.removeAt(0).also { CardDeck.discardCard(it) }
//        } else {
//            val card = CardDeck.drawCard()
//            if (card != null) attacker.hand.add(card)
//        }
//    }
//}
//
//class GreenDragonBlade(player: Player) : Weapon(player) {
//    override val name: String = "Green Dragon Blade"
//    override fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
//        target.beingAttacked()
//        if (target.currentHP > 0 && attacker.hasAttackCard()) {
//            println("${attacker.name} may use another Attack with $name")
//            val nextAttack = attacker.removeCardOfType(AttackCard::class.java)
//            if (nextAttack != null) target.beingAttacked()
//        }
//    }
//}
//
//class BlueSteelBlade(player: Player) : Weapon(player) {
//    override val name: String = "Blue Steel Blade"
//    override fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
//        println("${attacker.name} ignores armor with $name")
//        if (target.hasDodgeCard()) {
//            target.removeCardOfType(Card::class.java) // 消耗閃，但無效
//            target.currentHP--
//            println("${target.name} loses 1 HP, armor ignored, HP: ${target.currentHP}")
//        } else {
//            target.currentHP--
//            println("${target.name} loses 1 HP, HP: ${target.currentHP}")
//        }
//    }
//}
//
//class SerpentSpear(player: Player) : Weapon(player) {
//    override val name: String = "Serpent Spear"
//    override fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
//        if (attacker.hand.size >= 2) {
//            println("${attacker.name} uses 2 cards as Attack with $name")
//            repeat(2) { attacker.hand.removeAt(0).also { CardDeck.discardCard(it) } }
//            target.beingAttacked()
//        } else {
//            target.beingAttacked()
//        }
//    }
//}
//
//class KirinBow(player: Player) : Weapon(player) {
//    override val name: String = "Kirin Bow"
//    override fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
//        target.beingAttacked()
//        if (target.eHorsePlus != null || target.eHorseMinus != null) {
//            println("${attacker.name} removes a horse from ${target.name} with $name")
//            target.eHorsePlus?.let { target.eHorsePlus = null }
//                ?: target.eHorseMinus?.let { target.eHorseMinus = null }
//        }
//    }
//}