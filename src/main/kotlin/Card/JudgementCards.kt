package Card
import General.*
import Strategy.GeneralManager

class AcediaCard(Suit: String, Number: String) : JudgementCard(Suit, Number, "Acedia") {


    // Helper method to apply Acedia to a target player
    fun applyTo(target: Player, acediaCard: AcediaCard) {
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
                CardDeck.discardCard(acediaCard)
                CardDeck.printDiscardPile()
            } else {
                println("The deck is empty. Acedia has no effect.")
            }
        }
    }
}

class LightningCard(Suit: String, Number: String) : JudgementCard(Suit, Number, "Lightning") {

    fun applyTo(target: Player, lightningCard: LightningCard) {
        target.judgementCommands.add { player ->
            println("${player.name} is judging the Lightning card.")
            val judgementCard = CardDeck.drawCard()
            if (judgementCard != null) {
                println("${player.name} draws ${judgementCard.Suit} ${judgementCard.Number} - ${judgementCard.Name} for Lightning judgement.")
                val isStruck = judgementCard.Suit == "Spades" && judgementCard.Number.toIntOrNull() in 2..9
                if (isStruck) {
                    println("${player.name} is struck by lightning! Losing 3 HP.")
                    player.currentHP -= 3
                    if (player.currentHP <= 0) {
                        println("${player.name} has been defeated by the lightning strike (HP: ${player.currentHP}).")
                        player.handleDefeat() // No killer for lightning strike
                    } else {
                        println("${player.name}'s HP is now ${player.currentHP}.")
                    }
                    CardDeck.discardCard(lightningCard) // Discard the Lightning card since the effect triggered
                } else {
                    println("${player.name} avoids the lightning strike.")
                    // Pass the Lightning card to the next player
                    val allPlayers = GeneralManager.getAlivePlayerList().filter { it.currentHP > 0 }.sortedBy { it.seat }
                    // val currentSeat = player.seat
                    // Debug output
                    println("All players: ${allPlayers.map { "${it.name} (seat ${it.seat})" }}")
                    println("Current seat: ${player.seat}")
                    // Find the current player's index in the sorted list
                    val currentIndex = allPlayers.indexOfFirst { it.seat == player.seat }
                    if (currentIndex == -1) {
                        println("Current player not found in the list. Discarding the Lightning card.")
                        CardDeck.discardCard(lightningCard)
                    } else {
                        // Find the next player by index, wrapping around
                        val nextIndex = (currentIndex + 1) % allPlayers.size
                        val nextPlayer = allPlayers[nextIndex]
                        println("The Lightning card is passed to ${nextPlayer.name} (seat ${nextPlayer.seat}).")
                        applyTo(nextPlayer, lightningCard) // Re-apply to the next player
                    }
                }
                CardDeck.discardCard(judgementCard) // Send the judgement card to discard pile
                CardDeck.printDiscardPile()
            } else {
                println("The deck is empty. Lightning has no effect.")
                CardDeck.discardCard(lightningCard)
                CardDeck.printDiscardPile()
            }
        }
    }
}