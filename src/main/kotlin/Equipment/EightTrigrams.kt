import Card.CardDeck
import Card.DodgeCard
import kotlin.random.Random

class EightTrigrams(player: Player) : Armor(player) {
    override val name: String = "Eight Trigrams"

    private val eightTrigramsEffect = EightTrigramsEffect()

    override fun beingAttacked() {
        eightTrigramsEffect.applyEffect(player) {
            // Default behavior: try to dodge normally if Eight Trigrams fails
            if (player.hasDodgeCard()) {
                player.removeCardOfType(DodgeCard::class.java)
                println("${player.name} dodged attack by spending a dodge card.")
            } else {
                player.currentHP--
                println("${player.name} can't dodge the attack, current HP is ${player.currentHP}.")
            }
        }
    }
}

class EightTrigramsEffect : ArmorEffect {
    override fun applyEffect(player: Player, onBeingAttacked: () -> Unit) {
        println("Triggering the Eight Trigrams judgment")

        // Draw a card from the deck for judgment
        val judgmentCard = CardDeck.drawCard()

        if (judgmentCard != null) {
            println("Judgment card: ${judgmentCard.Suit} ${judgmentCard.Number} - ${judgmentCard.Name}")

            // Check if the card is red (Hearts or Diamonds)
            val isRed = judgmentCard.Suit == "Hearts" || judgmentCard.Suit == "Diamonds"

            if (isRed) {
                println("Judgment result is red - Eight Trigrams automatically provides a Dodge")
                CardDeck.discardCard(judgmentCard) // Discard the judgment card
                // Attack is automatically dodged without consuming a hand card
            } else {
                println("Judgment result is black")
                CardDeck.discardCard(judgmentCard) // Discard the judgment card
                if (player.hasDodgeCard()) {
                    println("${player.name} uses a Dodge card from hand")
                    player.removeCardOfType(DodgeCard::class.java)
                } else {
                    println("${player.name} has no Dodge card available")
                    onBeingAttacked.invoke()
                }
            }
        } else {
            println("Deck is empty - cannot perform judgment")
            onBeingAttacked.invoke()
        }
    }
}