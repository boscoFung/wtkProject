package General

import Card.CardDeck
import Card.DodgeCard

abstract class WeiGeneral(name: String, maxHP: Int) : General(name, maxHP) {
    var next: WeiGeneral? = null
    var forceDodgeForTesting: Boolean = false

    open fun handleRequest(): Boolean {
        if (currentHP <= 0) {
            println("$name is defeated and cannot help.")
            return next?.handleRequest() ?: false
        }
        // In a real game, forceDodgeForTesting should be false
        if (forceDodgeForTesting) {
            println("$name helps Cao Cao dodge an attack (forced for testing).")
            return true
        }
        if (hasDodgeCard() && Math.random() < 0.5) {
            val dodgeCard = hand.first { it is DodgeCard }
            hand.remove(dodgeCard)
            CardDeck.discardCard(dodgeCard)
            println("$name helps Cao Cao dodge an attack by spending ${dodgeCard.Suit} ${dodgeCard.Number} - ${dodgeCard.Name}.")
            return true
        } else {
            println("$name cannot dodge the attack. Passing to the next general.")
            return next?.handleRequest() ?: false
        }
    }
}