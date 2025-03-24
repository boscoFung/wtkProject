package Card
import General.*
import Strategy.GeneralManager

class AcediaCard(Suit: String, Number: String) : JudgementCard(Suit, Number, "Acedia") {
    fun applyTo(target: Player, acediaCard: AcediaCard) {

        if (target.currentHP <= 0) {
            println("${target.name} is already defeated, Acedia has no effect.")
            CardDeck.discardCard(acediaCard)
            return
        }

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
                CardDeck.discardCard(judgementCard)
                CardDeck.discardCard(acediaCard)
            } else {
                println("The deck is empty. Acedia has no effect.")
                CardDeck.discardCard(acediaCard)
            }
        }
    }
}

class LightningCard(Suit: String, Number: String) : JudgementCard(Suit, Number, "Lightning") {
    fun applyTo(target: Player, lightningCard: LightningCard) {
        // 檢查目標是否存活
        if (target.currentHP <= 0) {
            println("${target.name} is already defeated, Lightning is discarded.")
            CardDeck.discardCard(lightningCard)
            return
        }

        target.judgementCommands.add { player ->
            println("${player.name} is judging the Lightning card.")
            val judgementCard = CardDeck.drawCard()
            if (judgementCard != null) {
                println("${player.name} draws ${judgementCard.Suit} ${judgementCard.Number} - ${judgementCard.Name} for Lightning judgement.")
                val isStruck = judgementCard.Suit == "Spades" && judgementCard.getNumericValue() in 2..9
                if (isStruck) {
                    println("${player.name} is struck by lightning! Losing 3 HP.")
                    (player as? General)?.reduceHP(3) ?: run { player.currentHP -= 3 }
                    CardDeck.discardCard(lightningCard) // 受傷後丟棄閃電
                } else {
                    println("${player.name} avoids the lightning strike.")
                    val allPlayers = GeneralManager.getAlivePlayerList().filter { it.currentHP > 0 }.sortedBy { it.seat }
                    if (allPlayers.isEmpty()) {
                        println("No alive players left. Discarding the Lightning card.")
                        CardDeck.discardCard(lightningCard)
                    } else {
                        val currentIndex = allPlayers.indexOfFirst { it.seat == player.seat }
                        if (currentIndex == -1) {
                            println("Current player not found in the list. Discarding the Lightning card.")
                            CardDeck.discardCard(lightningCard)
                        } else {
                            val nextIndex = (currentIndex + 1) % allPlayers.size
                            val nextPlayer = allPlayers[nextIndex]
                            println("The Lightning card is passed to ${nextPlayer.name} (seat ${nextPlayer.seat}).")
                            nextPlayer.judgementCommands.add(this@LightningCard::applyTo.bindTo(lightningCard))
                        }
                    }
                }
                CardDeck.discardCard(judgementCard)
            } else {
                println("The deck is empty. Lightning has no effect.")
                CardDeck.discardCard(lightningCard)
            }
        }
    }

    private fun ((Player, LightningCard) -> Unit).bindTo(lightningCard: LightningCard): (Player) -> Unit {
        return { player -> this(player, lightningCard) }
    }
}