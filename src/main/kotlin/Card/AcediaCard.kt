package Card

import Player

class AcediaCard(Suit: String, Number: String) : JudgementCard(Suit, Number, "Acedia") {
    override fun effect() {
        // This method will be called when the card is played on a target
        // It adds a command to the target's judgementCommands list
        // The actual judgement logic will be handled in judgementPhase()
    }

    // Helper method to apply Acedia to a target player
    fun applyTo(target: Player) {
        target.judgementCommands.add { player ->
            println("${player.name} is judging the Acedia card.")
            val judgementCard = CardDeck.drawCard()
            if (judgementCard != null) {
                println("${player.name} draws ${judgementCard.Suit} ${judgementCard.Number} - ${judgementCard.Name} for Acedia judgement.")
                if (judgementCard.Suit == "Hearts") {
                    println("${player.name} dodges the Acedia effect (suit is Hearts).")
                } else {
                    println("${player.name} fails to dodge the Acedia effect. Skipping their next Play Phase.")
                    player.skipPlayPhase = true
                }
                CardDeck.discardCard(judgementCard) // Send the judgement card to discard pile
            } else {
                println("The deck is empty. Acedia has no effect.")
            }
        }
    }
}