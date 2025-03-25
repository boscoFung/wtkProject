package General

import Card.*
import Strategy.*

class LuBu : General("Lu Bu", 5, "Male") {

    override fun performAttack() {
        if (!hasAttackCard()) {
            println("$name has no Attack card to use.")
            return
        }

        val attackCard = removeCardOfType(AttackCard::class.java, discard = false)
        if (attackCard == null) {
            println("$name failed to retrieve an Attack card.")
            return
        }

        val range = calculateAttackRange()
        val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }
        val target = strategy?.whomToAttack(this, alivePlayers, range)

        if (target == null) {
            println("$name has no valid target to attack.")
            CardDeck.discardCard(attackCard)
            return
        }

        val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
        if (distance <= range && target.canBeTargeted(this, attackCard)) {
            attacksThisTurn++
            println("[Without Equal] $name uses ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} on ${target.name}")
            CardDeck.discardCard(attackCard)

            var dodged = false
            val dodgeCards = target.hand.filterIsInstance<DodgeCard>()
            if (dodgeCards.size >= 2) {
                target.hand.remove(dodgeCards[0])
                target.hand.remove(dodgeCards[1])
                CardDeck.discardCard(dodgeCards[0])
                CardDeck.discardCard(dodgeCards[1])
                println("${target.name} used 2 Dodge cards to dodge the attack.")
                dodged = true
            } else {
                (target as? General)?.reduceHP(1, this)
                println("${target.name} failed to use 2 Dodge cards and took 1 damage.")
            }

            if (strategy is LordStrategy) notifyObservers(dodged)
        } else {
            println("$name cannot attack ${target.name} (distance: $distance > range: $range or restricted).")
            CardDeck.discardCard(attackCard)
        }
    }
    override fun getRequiredDuelCards(isOpponent: Boolean): Int {
        return if (isOpponent) 2 else 1
    }

}
