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
class RockCleavingAxe(player: Player) : Weapon(player) {
    override val name: String = "Rock Cleaving Axe"
    override val attackLimitModifier: Int = 1
    override val attackRangeModifier: Int = 2

    override fun canAttack(attacksThisTurn: Int): Boolean = attacksThisTurn < player.currentAttackLimit

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        println("${attacker.name} uses $name with ${attackCard?.Suit} ${attackCard?.Number} - ${attackCard?.Name} to attack ${target.name}")
        val initialHP = target.currentHP
        target.attack(attacker)
        if (target.currentHP == initialHP && attacker.hand.size + equippedCardCount(attacker) >= 2) { // Target dodged
            println("${attacker.name} can discard 2 cards to force damage with $name (Simulating choice: Yes)")
            discardTwoCards(attacker)
            target.currentHP--
            println("${target.name} takes 1 forced damage from $name, HP: ${target.currentHP}")
            if (target.currentHP <= 0) target.handleDefeat(attacker)
        }
    }

    private fun equippedCardCount(player: Player): Int {
        return listOfNotNull(player.eWeapon, player.eArmor, player.eHorsePlus, player.eHorseMinus).size
    }

    private fun discardTwoCards(player: Player) {
        var discarded = 0
        while (discarded < 2 && (player.hand.isNotEmpty() || equippedCardCount(player) > 0)) {
            if (player.hand.isNotEmpty()) {
                val card = player.hand.removeAt(0)
                CardDeck.discardCard(card)
                println("${player.name} discards ${card.Suit} ${card.Number} - ${card.Name}")
            } else {
                // Discard equipped items (priority: horse -> armor -> weapon)
                when {
                    player.eHorsePlus != null -> player.eHorsePlus = null
                    player.eHorseMinus != null -> player.eHorseMinus = null
                    player.eArmor != null -> player.eArmor = null
                    player.eWeapon != null -> (player.eWeapon as Weapon).unequip()
                }
                println("${player.name} discards an equipped item")
            }
            discarded++
        }
    }
}

class SkyPiercingHalberd(player: Player) : Weapon(player) {
    override val name: String = "Sky Piercing Halberd"
    override val attackLimitModifier: Int = 1
    override val attackRangeModifier: Int = 3

    override fun canAttack(attacksThisTurn: Int): Boolean = attacksThisTurn < player.currentAttackLimit

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        println("${attacker.name} uses $name with ${attackCard?.Suit} ${attackCard?.Number} - ${attackCard?.Name}")
        val allPlayers = GeneralManager.getAlivePlayerList()
        if (attacker.hand.size == 1) { // Last card
            val targets = allPlayers.filter {
                it != attacker && attacker.calculateDistanceTo(it, allPlayers.size) <= attacker.calculateAttackRange()
            }.shuffled().take(3) // Up to 3 targets
            targets.forEach {
                println("${attacker.name} attacks ${it.name} with $name")
                it.attack(attacker)
            }
        } else {
            println("${attacker.name} attacks ${target.name}")
            target.attack(attacker)
        }
    }
}

class YinYangSwords(player: Player) : Weapon(player) {
    override val name: String = "Yin-Yang Swords"
    override val attackLimitModifier: Int = 1
    override val attackRangeModifier: Int = 2

    override fun canAttack(attacksThisTurn: Int): Boolean = attacksThisTurn < player.currentAttackLimit

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        println("${attacker.name} uses $name with ${attackCard?.Suit} ${attackCard?.Number} - ${attackCard?.Name} to attack ${target.name}")
        if (attacker.gender != target.gender) {
            println("${target.name} (opposite gender) must choose: 1) Discard 1 card, 2) Let ${attacker.name} draw 1 card (Simulating choice: 1)")
            if (target.hand.isNotEmpty()) {
                val card = target.hand.removeAt(0)
                CardDeck.discardCard(card)
                println("${target.name} discards ${card.Suit} ${card.Number} - ${card.Name}")
            } else {
                println("${target.name} has no cards to discard, ${attacker.name} draws a card instead")
                val card = CardDeck.drawCard()
                if (card != null) attacker.hand.add(card)
            }
        }
        target.attack(attacker)
    }
}

class GreenDragonBlade(player: Player) : Weapon(player) {
    override val name: String = "Green Dragon Blade"
    override val attackLimitModifier: Int = 1
    override val attackRangeModifier: Int = 2

    override fun canAttack(attacksThisTurn: Int): Boolean = attacksThisTurn < player.currentAttackLimit

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        println("${attacker.name} uses $name with ${attackCard?.Suit} ${attackCard?.Number} - ${attackCard?.Name} to attack ${target.name}")
        while (true) {
            val initialHP = target.currentHP
            target.attack(attacker)
            if (target.currentHP < initialHP || !attacker.hasAttackCard()) break // Damage dealt or no more AttackCards
            println("${attacker.name} can use another Attack with $name (Simulating choice: Yes)")
            val nextAttack = attacker.removeCardOfType(AttackCard::class.java)
            if (nextAttack != null) {
                println("${attacker.name} uses $name with ${nextAttack.Suit} ${nextAttack.Number} - ${nextAttack.Name}")
            } else break
        }
    }
}

class BlueSteelBlade(player: Player) : Weapon(player) {
    override val name: String = "Blue Steel Blade"
    override val attackLimitModifier: Int = 1
    override val attackRangeModifier: Int = 1

    override fun canAttack(attacksThisTurn: Int): Boolean = attacksThisTurn < player.currentAttackLimit

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        println("${attacker.name} uses $name with ${attackCard?.Suit} ${attackCard?.Number} - ${attackCard?.Name} to attack ${target.name}, ignoring armor")
        target.eArmor = null // Temporarily ignore armor
        target.attack(attacker)
        // Armor is not re-equipped here; assume it’s not destroyed, just bypassed
    }
}

class SerpentSpear(player: Player) : Weapon(player) {
    override val name: String = "Serpent Spear"
    override val attackLimitModifier: Int = 1
    override val attackRangeModifier: Int = 2

    override fun canAttack(attacksThisTurn: Int): Boolean = attacksThisTurn < player.currentAttackLimit

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        if (attackCard == null && attacker.hand.size >= 2) { // Special ability used
            println("${attacker.name} discards 2 cards to use $name as an Attack")
            repeat(2) {
                val card = attacker.hand.removeAt(0)
                CardDeck.discardCard(card)
                println("${attacker.name} discards ${card.Suit} ${card.Number} - ${card.Name}")
            }
            target.attack(attacker)
        } else if (attackCard != null) { // Normal AttackCard used
            println("${attacker.name} uses $name with ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack ${target.name}")
            target.attack(attacker)
        }
    }
}

class KirinBow(player: Player) : Weapon(player) {
    override val name: String = "Kirin Bow"
    override val attackLimitModifier: Int = 1
    override val attackRangeModifier: Int = 4

    override fun canAttack(attacksThisTurn: Int): Boolean = attacksThisTurn < player.currentAttackLimit

    override fun attackTarget(attacker: Player, target: Player, attackCard: Card?) {
        println("${attacker.name} uses $name with ${attackCard?.Suit} ${attackCard?.Number} - ${attackCard?.Name} to attack ${target.name}")
        val initialHP = target.currentHP
        target.attack(attacker)
        if (target.currentHP < initialHP && (target.eHorsePlus != null || target.eHorseMinus != null)) {
            println("${attacker.name} can discard a horse from ${target.name} with $name (Simulating choice: Yes)")
            if (target.eHorsePlus != null) {
                target.eHorsePlus = null
                println("${target.name}'s +1 Horse discarded")
            } else if (target.eHorseMinus != null) {
                target.eHorseMinus = null
                println("${target.name}'s -1 Horse discarded")
            }
        }
    }

}